package community.solace.mc.idea.plugin.ui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.table.JBTable;
import com.solace.semp.config.api.QueueApi;
import com.solace.semp.config.invoker.ApiCallback;
import com.solace.semp.config.invoker.ApiClient;
import com.solace.semp.config.invoker.ApiException;
import com.solace.semp.config.model.MsgVpnQueue;
import com.solace.semp.config.model.MsgVpnQueueSubscription;
import com.solace.semp.config.model.MsgVpnQueueSubscriptionsResponse;
import com.solace.semp.config.model.MsgVpnQueuesResponse;
import com.solace.semp.monitor.model.MsgVpnQueueMsg;
import com.solace.semp.monitor.model.MsgVpnQueueMsgsResponse;
import community.solace.mc.idea.plugin.ui.common.PopupTextInput;
import community.solace.mc.idea.plugin.ui.common.SolaceTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueTab extends ServiceDetailsTab {
    private static final Gson GSON = new GsonBuilder().create();

    private enum VALUE_TYPE {
        STRING,
        DOUBLE,
        BOOL,
    }

    private QueueApi queueApi;
    private com.solace.semp.monitor.api.QueueApi queueMonitorApi;
    private final DefaultTableModel queueListTableModel = new DefaultTableModel();
    private String vpnName;

    public QueueTab() {
        super();

        queueListTableModel.setColumnIdentifiers(new String[]{"Name", "Access Type", "Spool"});
        AnAction createQueueButton = new AnAction("Create", "Create a queue", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                PopupTextInput.show("Create queue", q -> {
                    MsgVpnQueue queueBody = new MsgVpnQueue();
                    queueBody.setQueueName(q);

                    // Use same defaults as WebUI
                    queueBody.setIngressEnabled(true);
                    queueBody.setEgressEnabled(true);
                    queueBody.setPermission(MsgVpnQueue.PermissionEnum.CONSUME);
                    try {
                        queueApi.createMsgVpnQueue(vpnName, queueBody, null, null);
                        refreshQueues();
                    } catch (ApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }, e.getInputEvent().getComponent());
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(queueApi != null);
            }
        };

        AnAction refreshButton = new AnAction("Refresh", "Refresh list of queues", AllIcons.Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                refreshQueues();
            }
        };

        actionGroup.add(createQueueButton);
        actionGroup.add(refreshButton);
    }

    public void init(String vpnName, String sempHostname, int sempPort, String sempUsername, String sempPassword) {
        this.vpnName = vpnName;

        ApiClient sempClient = new ApiClient();
        sempClient.setBasePath("https://" + sempHostname + ":" + sempPort + "/SEMP/v2/config");
        sempClient.setUsername(sempUsername);
        sempClient.setPassword(sempPassword);
        queueApi = new QueueApi(sempClient);

        com.solace.semp.monitor.invoker.ApiClient sempMonitorClient = new com.solace.semp.monitor.invoker.ApiClient();
        sempMonitorClient.setBasePath("https://" + sempHostname + ":" + sempPort + "/SEMP/v2/monitor");
        sempMonitorClient.setUsername(sempUsername);
        sempMonitorClient.setPassword(sempPassword);
        queueMonitorApi = new com.solace.semp.monitor.api.QueueApi(sempMonitorClient);

        refreshQueues();

        JPopupMenu queueOptions = new JPopupMenu();
        JMenuItem deleteQueue = new JMenuItem("Delete Queue");
        queueOptions.add(deleteQueue);

        SolaceTable queueListTable = new SolaceTable(queueListTableModel, queueOptions, (s) -> new QueueDetailsDialog(new QueueDetails(s[0])).show());

        deleteQueue.addActionListener(e -> {
            String queueName = queueListTable.getValueForSelectedRow(0);
            int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete queue " + queueName + "?", "Delete Queue", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    queueApi.deleteMsgVpnQueue(vpnName, queueName);
                    refreshQueues();
                } catch (ApiException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        add(queueListTable, BorderLayout.CENTER);
    }

    private void refreshQueues() {
        queueListTableModel.setRowCount(0);

        try {
            queueApi.getMsgVpnQueuesAsync(vpnName, null, null, null, null, null, new GetQueuesCallback());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static class QueueDetailsDialog extends DialogWrapper {
        private final QueueDetails queueDetails;

        public QueueDetailsDialog(QueueDetails queueDetails) {
            super(true); // use current window as parent
            this.queueDetails = queueDetails;
            setTitle(queueDetails.getQueueName());
            init();
        }

        @Override
        protected JComponent createCenterPanel() {
            return queueDetails;
        }

        @Override
        protected Action @NotNull [] createActions() {
            return new Action[]{};
        }
    }

    private class QueueDetails extends OnePixelSplitter {
        private final String queueName;
        public QueueDetails(String queueName) {
            this.queueName = queueName;
            setPreferredSize(new Dimension(600, 500));
            setFirstComponent(new QueueAttributes(queueName));

            OnePixelSplitter msgsAndSubscriptionsPanel = new OnePixelSplitter(true);
            msgsAndSubscriptionsPanel.setFirstComponent(new QueueMessages(queueName));
            msgsAndSubscriptionsPanel.setSecondComponent(new QueueSubscriptions(queueName));

            setSecondComponent(msgsAndSubscriptionsPanel);
        }

        public String getQueueName() {
            return queueName;
        }
    }

    private class QueueAttributes extends JPanel {
        public QueueAttributes(String queueName) {
            setLayout(new BorderLayout());

            try {
                MsgVpnQueue queue = queueApi.getMsgVpnQueue(vpnName, queueName, null, null).getData();

                if (queue != null) {
                    Map<String, Object> result = GSON.fromJson(queue.toJson(), Map.class);

                    DefaultTableModel queueTableModel = new DefaultTableModel() {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            // Attribute names are in col 0
                            return column != 0;
                        }
                    };

                    queueTableModel.setColumnIdentifiers(new Object[]{"Attribute", "Value"});
                    List<VALUE_TYPE> valueTypes = new ArrayList<>();

                    /*
                      Queue attributes are handled dynamically to avoid hardcoding all its attributes for updating.
                      This implementation can handle string, double, and boolean attributes. Any other types are not
                      displayed (ex. threshold attributes are objects).

                      The value types are saved because while the table model supports Objects, when they are edited,
                      they're converted to Strings so saving the value types allows parsing back to the original type.

                      Finally, the updated object is converted to a JSON string so it can be consumed by the SEMP API client.
                     */
                    queueTableModel.addTableModelListener(e -> {
                        if (e.getType() == TableModelEvent.UPDATE) {
                            Object changed = ((TableModel) e.getSource()).getValueAt(e.getFirstRow(), 1);

                            // After parsing an updated value, it fires another event, so only consider this for non-String values
                            if (changed instanceof String) {
                                switch (valueTypes.get(e.getFirstRow())) {
                                    case DOUBLE:
                                        queueTableModel.setValueAt(Double.parseDouble(changed.toString()), e.getFirstRow(), 1);
                                        break;
                                    case BOOL:
                                        queueTableModel.setValueAt(Boolean.parseBoolean(changed.toString()), e.getFirstRow(), 1);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    });

                    // Go through all the attributes of a queue and store their types
                    for (Map.Entry<String, Object> e : result.entrySet()) {
                        if (e.getValue() instanceof Double) {
                            queueTableModel.addRow(new Object[]{e.getKey(), e.getValue()});
                            valueTypes.add(VALUE_TYPE.DOUBLE);
                        } else if (e.getValue() instanceof Boolean) {
                            queueTableModel.addRow(new Object[]{e.getKey(), e.getValue()});
                            valueTypes.add(VALUE_TYPE.BOOL);
                        } else if (e.getValue() instanceof String) {
                            queueTableModel.addRow(new Object[]{e.getKey(), e.getValue()});
                            valueTypes.add(VALUE_TYPE.STRING);
                        }
                    }

                    JBTable queueTable = new JBTable(queueTableModel);
                    add(new JScrollPane(queueTable), BorderLayout.CENTER);

                    JButton updateQueue = new JButton("Update");

                    updateQueue.addActionListener(a -> {
                        Map<String, Object> updatedValues = new HashMap<>();

                        for (int i = 0; i < queueTableModel.getRowCount(); i++) {
                            String k = queueTableModel.getValueAt(i, 0).toString();
                            Object v = queueTableModel.getValueAt(i, 1);
                            updatedValues.put(k, v);
                        }

                        try {
                            queueApi.replaceMsgVpnQueue(vpnName, queue.getQueueName(), MsgVpnQueue.fromJson(GSON.toJson(updatedValues)), null, null);
                            refreshQueues();
                        } catch (ApiException|IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    add(updateQueue, BorderLayout.PAGE_END);
                }
            } catch (com.solace.semp.config.invoker.ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class QueueSubscriptions extends JPanel {
        private final String queueName;
        private final DefaultTableModel queueSubscriptionsTableModel = new DefaultTableModel();

        public QueueSubscriptions(String queueName) {
            this.queueName = queueName;
            setLayout(new BorderLayout());

            JPopupMenu queueSubscriptionMenu = new JPopupMenu();
            JMenuItem deleteQueueSubscription = new JMenuItem("Delete");
            queueSubscriptionMenu.add(deleteQueueSubscription);

            SolaceTable queueSubscriptionTable = new SolaceTable(queueSubscriptionsTableModel, queueSubscriptionMenu, s -> {});
            queueSubscriptionsTableModel.setColumnIdentifiers(new Object[]{"Queue Subscriptions"});

            deleteQueueSubscription.addActionListener(e -> {
                String subscriptionTopic = queueSubscriptionTable.getValueForSelectedRow(0);
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete queue subscription " + subscriptionTopic + "?", "Delete Queue Subscription", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        queueApi.deleteMsgVpnQueueSubscription(vpnName, queueName, subscriptionTopic);
                        refreshQueueSubscriptions();
                    } catch (ApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            refreshQueueSubscriptions();

            JButton addQueueSubscription = new JButton("Add Queue Subscription");

            addQueueSubscription.addActionListener(e -> {
                PopupTextInput.show("Add queue subscription", s -> {
                    try {
                        MsgVpnQueueSubscription body = new MsgVpnQueueSubscription();
                        body.setSubscriptionTopic(s);
                        queueApi.createMsgVpnQueueSubscription(vpnName, queueName, body, null, null);
                        refreshQueueSubscriptions();
                    } catch (ApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }, addQueueSubscription);
            });

            add(queueSubscriptionTable, BorderLayout.CENTER);
            add(addQueueSubscription, BorderLayout.PAGE_END);
        }

        public void refreshQueueSubscriptions() {
            queueSubscriptionsTableModel.setRowCount(0);

            try {
                queueApi.getMsgVpnQueueSubscriptionsAsync(vpnName, queueName, null, null, null, null, null, new GetQueueSubscriptionsCallback(queueName, queueSubscriptionsTableModel));
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class QueueMessages extends JPanel {
        private final String queueName;
        private final DefaultTableModel msgsTableModel;
        public QueueMessages(String queueName) {
            setLayout(new BorderLayout());

            this.queueName = queueName;
            msgsTableModel = new DefaultTableModel();
            msgsTableModel.setColumnIdentifiers(new Object[]{"ID", "Spooled Time", "Content Size", "Attachment Size"});
            SolaceTable msgTable = new SolaceTable(msgsTableModel, new JPopupMenu(), s -> {});
            add(new JScrollPane(msgTable), BorderLayout.CENTER);

            JButton refresh = new JButton("Refresh");
            refresh.addActionListener(a -> refreshMessages());
            add(refresh, BorderLayout.PAGE_END);

            refreshMessages();
        }

        public void refreshMessages() {
            try {
                msgsTableModel.setRowCount(0);
                queueMonitorApi.getMsgVpnQueueMsgsAsync(vpnName, queueName, null, null, null, null, new MsgVpnQueueMsgsCallback(queueName, msgsTableModel));
            } catch (com.solace.semp.monitor.invoker.ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class GetQueuesCallback implements ApiCallback<MsgVpnQueuesResponse> {

        @Override
        public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {

        }

        @Override
        public void onSuccess(MsgVpnQueuesResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
            if (result != null && result.getData() != null) {
                for (MsgVpnQueue q : result.getData()) {
                    queueListTableModel.addRow(new Object[]{q.getQueueName(), q.getAccessType(), q.getMaxMsgSpoolUsage()});
                }

                if (result.getMeta().getPaging() != null) {
                    String cursor = result.getMeta().getPaging().getCursorQuery();

                    try {
                        queueApi.getMsgVpnQueuesAsync(vpnName, null, cursor, null, null, null, new GetQueuesCallback());
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

        }

        @Override
        public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

        }
    }

    private class GetQueueSubscriptionsCallback implements ApiCallback<MsgVpnQueueSubscriptionsResponse> {
        private final String queueName;
        private final DefaultTableModel tableModel;

        public GetQueueSubscriptionsCallback(String queueName, DefaultTableModel tableModel) {
            super();
            this.queueName = queueName;
            this.tableModel = tableModel;
        }

        @Override
        public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {

        }

        @Override
        public void onSuccess(MsgVpnQueueSubscriptionsResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
            if (result != null && result.getData() != null) {
                for (MsgVpnQueueSubscription s : result.getData()) {
                    tableModel.addRow(new Object[]{s.getSubscriptionTopic()});
                }

                if (result.getMeta().getPaging() != null) {
                    String cursor = result.getMeta().getPaging().getCursorQuery();

                    try {
                        queueApi.getMsgVpnQueueSubscriptionsAsync(vpnName, queueName, null, cursor, null, null, null, new GetQueueSubscriptionsCallback(queueName, tableModel));
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

        }

        @Override
        public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

        }
    }

    private class MsgVpnQueueMsgsCallback implements com.solace.semp.monitor.invoker.ApiCallback<MsgVpnQueueMsgsResponse> {
        private final String queueName;
        private final DefaultTableModel tableModel;

        private MsgVpnQueueMsgsCallback(String queueName, DefaultTableModel tableModel) {
            this.queueName = queueName;
            this.tableModel = tableModel;
        }

        @Override
        public void onFailure(com.solace.semp.monitor.invoker.ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
        }

        @Override
        public void onSuccess(MsgVpnQueueMsgsResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
            if (result != null && result.getData() != null) {
                for (MsgVpnQueueMsg m : result.getData()) {
                    tableModel.addRow(new Object[]{m.getMsgId(), m.getSpooledTime(), m.getContentSize() + "B", m.getAttachmentSize() + "B"});
                }

                if (result.getMeta().getPaging() != null) {
                    String cursor = result.getMeta().getPaging().getCursorQuery();

                    try {
                        queueMonitorApi.getMsgVpnQueueMsgsAsync(vpnName, queueName, null, cursor, null, null, new MsgVpnQueueMsgsCallback(queueName, tableModel));
                    } catch (com.solace.semp.monitor.invoker.ApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

        }

        @Override
        public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

        }
    }
}

package community.solace.mc.idea.plugin.ui.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.table.JBTable;
import com.solace.semp.api.QueueApi;
import com.solace.semp.invoker.ApiCallback;
import com.solace.semp.invoker.ApiClient;
import com.solace.semp.invoker.ApiException;
import com.solace.semp.model.MsgVpnQueue;
import com.solace.semp.model.MsgVpnQueuesResponse;
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
    private final DefaultTableModel queueListTableModel = new DefaultTableModel();
    private String vpnName;

    public QueueTab() {
        super();

        queueListTableModel.setColumnIdentifiers(new String[]{"Name", "Access Type", "Spool"});
        AnAction createQueueButton = new AnAction("Create", "Create a queue", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                JTextField queueInput = new JTextField("", 18);

                MsgVpnQueue queueBody = new MsgVpnQueue();

                JBPopup createQueuePopup = JBPopupFactory.getInstance().createComponentPopupBuilder(queueInput, null)
                        .setAdText("Create queue")
                        .setRequestFocus(true)
                        .setCancelOnClickOutside(true)
                        .setCancelOnOtherWindowOpen(false)
                        .setOkHandler(() -> {
                            queueBody.setQueueName(queueInput.getText());
                            try {
                                queueApi.createMsgVpnQueue(vpnName, queueBody, null, null);
                                refreshQueues();
                            } catch (ApiException ex) {
                                throw new RuntimeException(ex);
                            }
                        })
                        .createPopup();

                queueInput.addActionListener(l -> createQueuePopup.closeOk(null));
                createQueuePopup.showUnderneathOf(e.getInputEvent().getComponent());
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

        refreshQueues();

        JPopupMenu queueOptions = new JPopupMenu();
        JMenuItem deleteQueue = new JMenuItem("Delete Queue");
        queueOptions.add(deleteQueue);

        /*
          Queue attributes are handled dynamically to avoid hardcoding all its attributes for updating.
          This implementation can handle string, double, and boolean attributes. Any other types are not
          displayed (ex. threshold attributes are objects).

          The value types are saved because while the table model supports Objects, when they are edited,
          they're converted to Strings so saving the value types allows parsing back to the original type.

          Finally, the updated object is converted to a JSON string so it can be consumed by the SEMP API client.
         */
        SolaceTable queueListTable = new SolaceTable(queueListTableModel, queueOptions, (s) -> {
            try {
                MsgVpnQueue queue = queueApi.getMsgVpnQueue(vpnName, s[0], null, null).getData();
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
                JPanel queueTablePanel = new JPanel();
                queueTablePanel.setLayout(new BorderLayout());
                queueTablePanel.add(new JScrollPane(queueTable), BorderLayout.CENTER);

                QueueDetailsDialog dialog = new QueueDetailsDialog(queue.getQueueName(), new JScrollPane(queueTablePanel));

                if (dialog.showAndGet()) {
                    Map<String, Object> updatedValues = new HashMap<>();

                    for (int i = 0; i < queueTableModel.getRowCount(); i++) {
                        String k = queueTableModel.getValueAt(i, 0).toString();
                        Object v = queueTableModel.getValueAt(i, 1);
                        updatedValues.put(k, v);
                    }

                    try {
                        queueApi.replaceMsgVpnQueue(vpnName, queue.getQueueName(), MsgVpnQueue.fromJson(GSON.toJson(updatedValues)), null, null);
                        refreshQueues();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (com.solace.semp.invoker.ApiException e) {
                throw new RuntimeException(e);
            }
        });

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

    private class GetQueuesCallback implements ApiCallback<MsgVpnQueuesResponse> {

        @Override
        public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {

        }

        @Override
        public void onSuccess(MsgVpnQueuesResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
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

        @Override
        public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

        }

        @Override
        public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

        }
    }

    private static class QueueDetailsDialog extends DialogWrapper {
        final JComponent queueDetails;

        public QueueDetailsDialog(String queueName, JComponent queueDetails) {
            super(true); // use current window as parent
            this.queueDetails = queueDetails;
            setTitle(queueName);
            init();
        }

        @Override
        protected JComponent createCenterPanel() {
            JPanel dialogPanel = new JPanel(new BorderLayout());
            queueDetails.setPreferredSize(new Dimension(200, 500));
            dialogPanel.add(queueDetails, BorderLayout.CENTER);

            return dialogPanel;
        }
    }
}

package community.solace.mc.idea.plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import community.solace.mc.idea.plugin.rest.MissionControlCallback;
import community.solace.mc.idea.plugin.settings.AppSettingsState;
import community.solace.mc.idea.plugin.ui.common.SolaceTable;
import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiException;
import com.solace.mc.model.GetAllServicesResponse;
import com.solace.mc.model.GetServices;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.PatternSyntaxException;

public class ServiceListTab extends SimpleToolWindowPanel {
    private final EventBrokerServicesApi api;
    DefaultTableModel serviceTableModel = new DefaultTableModel();

    public ServiceListTab(EventBrokerServicesApi api, BiConsumer<String, String> serviceDetailAction) {
        super(false, true);
        this.api = api;

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem openInBrowser = new JMenuItem("Open in Browser");
        JMenuItem deleteItem = new JMenuItem("Delete");

        popupMenu.add(openInBrowser);
        popupMenu.add(deleteItem);

        serviceTableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Class", "Provider", "Region"});

        SolaceTable serviceTable = new SolaceTable(serviceTableModel, popupMenu, (row) -> serviceDetailAction.accept(row[0], row[1]));

        // ID is only in table for storage, not for viewing by the user, so hide the column
        serviceTable.getTable().getColumnModel().getColumn(0).setMinWidth(0);
        serviceTable.getTable().getColumnModel().getColumn(0).setMaxWidth(0);
        serviceTable.getTable().getColumnModel().getColumn(0).setWidth(0);

        openInBrowser.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(URI.create("https://" + AppSettingsState.getInstance().url + "/services/" + serviceTable.getValueForSelectedRow(0)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        deleteItem.addActionListener(e -> {
            ServiceDeletionDialog serviceDeletionDialog = new ServiceDeletionDialog(api, serviceTable.getValueForSelectedRow(0), serviceTable.getValueForSelectedRow(1));
            int result = JOptionPane.showConfirmDialog(null, serviceDeletionDialog, "Delete Service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                if (serviceDeletionDialog.deleteService()) {
                    refreshTable();
                }
            }
        });

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(serviceTableModel);

        JTextField filter = new JTextField();
        filter.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
            Document document = e.getDocument();

            if (document.getLength() > 0) {
                try {
                    String input = document.getText(0, document.getLength());
                    sorter.setRowFilter(RowFilter.regexFilter(input));
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                } catch (PatternSyntaxException ex) {
                    // Not a valid regex pattern, but not an issue since this could be in progress.
                }
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(".*"));
            }
        });

        serviceTable.getTable().setRowSorter(sorter);

        ServiceCreationDialog serviceCreationDialog = new ServiceCreationDialog(api);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel(AllIcons.Actions.Search), BorderLayout.LINE_START);
        headerPanel.add(filter, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.PAGE_START);
        add(serviceTable, BorderLayout.CENTER);

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        AnAction refreshButton = new AnAction("Refresh", "Refresh list of services", AllIcons.Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                refreshTable();
            }
        };

        AnAction createButton = new AnAction("Create", "Create a new service", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int result = JOptionPane.showConfirmDialog(e.getInputEvent().getComponent(), serviceCreationDialog, "Create Service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    if (serviceCreationDialog.createService()) {
                        refreshTable();
                    }
                }
            }
        };

        actionGroup.add(refreshButton);
        actionGroup.add(createButton);

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("mc-toolbar", actionGroup, false);
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
    }

    public void refreshTable() {
        serviceTableModel.setRowCount(0);
        getServices(1);
    }

    private void getServices(int pageNumber) {
        try {
            api.getServicesAsync(pageNumber, 10, new GetServicesCallback());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private class GetServicesCallback implements MissionControlCallback<GetAllServicesResponse> {
        @Override
        public void onSuccess(GetAllServicesResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
            for (GetServices service : result.getData()) {
                String[] datacenterInfo = deriveCloudAndRegionFromDatacenterId(service.getDatacenterId());
                serviceTableModel.addRow(new Object[] {service.getId(), service.getName(), service.getServiceClassId(), datacenterInfo[0], datacenterInfo[1]});
            }

            Map<String, Object> pagination = (Map<String, Object>) result.getMeta().get("pagination");
            Object nextPageResult = pagination.get("nextPage");

            if (nextPageResult != null) {
                int nextPage = (int) Double.parseDouble(nextPageResult.toString());
                getServices(nextPage);
            }
        }
    }

    private String[] deriveCloudAndRegionFromDatacenterId(String datacenterId) {
        if (datacenterId.startsWith("gke-gcp")) {
            return new String[]{"gke-gcp", datacenterId.split("gke-gcp-")[1]};
        } else {
            return datacenterId.split("-", 2);
        }
    }

    private interface SimpleDocumentListener extends DocumentListener {
        void update(DocumentEvent e);

        @Override
        default void insertUpdate(DocumentEvent e) {
            update(e);
        }
        @Override
        default void removeUpdate(DocumentEvent e) {
            update(e);
        }
        @Override
        default void changedUpdate(DocumentEvent e) {
            update(e);
        }
    }
}

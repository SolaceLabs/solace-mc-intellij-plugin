package community.solace.mc.idea.plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import community.solace.mc.idea.plugin.rest.MissionControlCallback;
import community.solace.mc.idea.plugin.settings.AppSettingsState;
import community.solace.mc.idea.plugin.ui.common.SolaceTable;
import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiException;
import com.solace.mc.model.GetAllServicesResponse;
import com.solace.mc.model.GetServices;

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
import java.util.function.Consumer;
import java.util.regex.PatternSyntaxException;

import static community.solace.mc.idea.plugin.rest.RestUtil.SERVICE_CLASSES;

public class ServiceListTab extends SimpleToolWindowPanel {
    private final EventBrokerServicesApi api;

    public final ServiceCreationDialog serviceCreationDialog;
    private final DefaultTableModel serviceTableModel = new DefaultTableModel();

    public ServiceListTab(EventBrokerServicesApi api, BiConsumer<String, String> serviceDetailAction, Consumer<String> deleteAction) {
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
                    deleteAction.accept(serviceTable.getValueForSelectedRow(0));
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

        serviceCreationDialog = new ServiceCreationDialog(api);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel(AllIcons.Actions.Search), BorderLayout.LINE_START);
        headerPanel.add(filter, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.PAGE_START);
        add(serviceTable, BorderLayout.CENTER);
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
            if (result != null && result.getData() != null) {
                for (GetServices service : result.getData()) {
                    if (service.getDatacenterId() != null && service.getServiceClassId() != null) {
                        String[] datacenterInfo = deriveCloudAndRegionFromDatacenterId(service.getDatacenterId());

                        serviceTableModel.addRow(new Object[]{service.getId(), service.getName(), SERVICE_CLASSES.get(service.getServiceClassId().getValue()), datacenterInfo[0], datacenterInfo[1]});
                    }
                }

                if (result.getMeta() != null) {
                    Map<String, Object> pagination = (Map<String, Object>) result.getMeta().get("pagination");
                    Object nextPageResult = pagination.get("nextPage");

                    if (nextPageResult != null) {
                        int nextPage = (int) Double.parseDouble(nextPageResult.toString());
                        getServices(nextPage);
                    }
                }
            }
        }
    }

    private String[] deriveCloudAndRegionFromDatacenterId(String datacenterId) {
        String[] cloudAndRegion;

        if (datacenterId.startsWith("gke-gcp")) {
            cloudAndRegion = new String[]{"gke-gcp", datacenterId.split("gke-gcp-")[1]};
        } else {
            cloudAndRegion = datacenterId.split("-", 2);
        }

        switch (cloudAndRegion[0]) {
            case "gke-gcp":
                cloudAndRegion[0] = "GCP";
                break;
            case "eks":
                cloudAndRegion[0] = "AWS (EKS)";
                break;
            case "aws":
                cloudAndRegion[0] = "AWS";
                break;
            case "aks":
                cloudAndRegion[0] = "Azure";
                break;
            default:
                break;
        }

        return cloudAndRegion;
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

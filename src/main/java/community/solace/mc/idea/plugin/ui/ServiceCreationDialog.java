package community.solace.mc.idea.plugin.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.FormBuilder;
import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiException;
import com.solace.mc.model.CreateServiceRequest;
import community.solace.mc.idea.plugin.rest.Datacenter;
import community.solace.mc.idea.plugin.rest.RestUtil;

import javax.swing.*;

import java.awt.*;

import static community.solace.mc.idea.plugin.rest.RestUtil.DATACENTERS;
import static community.solace.mc.idea.plugin.rest.RestUtil.REVERSE_SERVICE_CLASSES;

public class ServiceCreationDialog {
    JPanel dialogPanel;
    JTextField serviceNameField = new JTextField();
    ButtonGroup providerSelection = new ButtonGroup();
    ComboBox<Datacenter> regionComboBox = new ComboBox<>(250);
    ComboBox<String> serviceClassId = new ComboBox<>(250);

    private final EventBrokerServicesApi api;

    public ServiceCreationDialog(EventBrokerServicesApi api) {
        this.api = api;

        ProviderRadioButton providerAws = new ProviderRadioButton("AWS", "aws");
        ProviderRadioButton providerAzure = new ProviderRadioButton("Azure", "azure");
        ProviderRadioButton providerGcp = new ProviderRadioButton("GCP", "gcp");

        providerSelection.add(providerAws);
        providerSelection.add(providerAzure);
        providerSelection.add(providerGcp);

        JPanel providerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        providerPanel.add(providerAws);
        providerPanel.add(providerAzure);
        providerPanel.add(providerGcp);

        serviceClassId.setEnabled(false);
        regionComboBox.setEnabled(false);
        regionComboBox.addActionListener(e -> {
            Datacenter selectedDc = regionComboBox.getItemAt(regionComboBox.getSelectedIndex());
            serviceClassId.setModel(new DefaultComboBoxModel<>(selectedDc.getServiceClasses()));
            serviceClassId.setEnabled(true);
        });

        dialogPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JLabel("Name"), serviceNameField)
            .addLabeledComponent(new JLabel("Provider"), providerPanel)
            .addLabeledComponent(new JLabel("Region"), regionComboBox)
            .addLabeledComponent(new JLabel("Class"), serviceClassId)
            .getPanel();
    }

    public JPanel getDialogPanel() {
        return dialogPanel;
    }

    public boolean createService() {
        if (isInputValid()) {
            String createServiceResult = createService(serviceNameField.getText(), regionComboBox.getItemAt(regionComboBox.getSelectedIndex()).getId(), REVERSE_SERVICE_CLASSES.get(serviceClassId.getItem()));

            if (createServiceResult != null) {
                JOptionPane.showConfirmDialog(null, "Error creating service:\n" + createServiceResult, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                clearInputs();
                return true;
            }
        } else {
            JOptionPane.showConfirmDialog(null, "Must input all parameters", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private String createService(String name, String datacenterId, String classId) {
        try {
            CreateServiceRequest request = new CreateServiceRequest();
            request.setName(name);
            request.setDatacenterId(datacenterId);
            request.setServiceClassId(classId);
            api.createService(request);
            return null;
        } catch (ApiException e) {
            return RestUtil.getMessage(e.getResponseBody());
        } catch (IllegalArgumentException e) {
            // There is a bug in the Mission Control spec where status is defined as camelCase but actual response is UPPERCASE
            if (e.getMessage().startsWith("Unexpected value")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public boolean isInputValid() {
        return !serviceNameField.getText().isBlank() && providerSelection.getSelection() != null && regionComboBox.getSelectedItem() != null && serviceClassId.getSelectedItem() != null;
    }

    public void clearInputs() {
        serviceNameField.setText("");
        providerSelection.clearSelection();
        regionComboBox.setModel(new DefaultComboBoxModel<>());
        serviceClassId.setModel(new DefaultComboBoxModel<>());
    }

    private class ProviderRadioButton extends JRadioButton {

        private ProviderRadioButton(String displayName, String providerKey) {
            super(displayName);

            addActionListener(e -> {
                regionComboBox.setModel(new DefaultComboBoxModel<>(DATACENTERS.get(providerKey)));
                regionComboBox.setSelectedIndex(0);
                regionComboBox.setEnabled(true);
            });
        }
    }
}

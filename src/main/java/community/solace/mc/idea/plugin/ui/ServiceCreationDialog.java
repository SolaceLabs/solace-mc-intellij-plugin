package community.solace.mc.idea.plugin.ui;

import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiException;
import com.solace.mc.model.CreateServiceRequest;
import community.solace.mc.idea.plugin.rest.RestUtil;

import javax.swing.*;
import java.awt.*;

public class ServiceCreationDialog extends Panel {
    JTextField serviceNameField = new JTextField();
    JTextField serviceClassId = new JTextField();
    JTextField datacenterId = new JTextField();

    private final EventBrokerServicesApi api;

    public ServiceCreationDialog(EventBrokerServicesApi api) {
        this.api = api;

        setLayout(new GridLayout(0, 2));
        add(new JLabel("Name:"));
        add(serviceNameField);
        add(new JLabel("Class:"));
        add(serviceClassId);
        add(new JLabel("Datacenter:"));
        add(datacenterId);

        JButton create = new JButton("Create");
        create.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(null, this, "Create Service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                if (serviceNameField.getText().isBlank() || serviceClassId.getText().isBlank() || datacenterId.getText().isBlank()) {
                    JOptionPane.showConfirmDialog(null, "Must input all parameters", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else {
                    String createServiceResult = createService(serviceNameField.getText(), datacenterId.getText(), serviceClassId.getText());
                    if (createServiceResult != null) {
                        JOptionPane.showConfirmDialog(null, "Error creating service:\n" + createServiceResult, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    } else {
                        serviceNameField.setText("");
                        datacenterId.setText("");
                        serviceClassId.setText("");
                    }
                }
            }
        });
    }

    public boolean createService() {
        if (isInputValid()) {
            String createServiceResult = createService(serviceNameField.getText(), datacenterId.getText(), serviceClassId.getText());

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
        return !serviceNameField.getText().isBlank() || !serviceClassId.getText().isBlank() || !datacenterId.getText().isBlank();
    }

    public void clearInputs() {
        serviceNameField.setText("");
        datacenterId.setText("");
        serviceClassId.setText("");
    }
}

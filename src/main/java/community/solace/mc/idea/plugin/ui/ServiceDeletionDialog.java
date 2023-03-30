package community.solace.mc.idea.plugin.ui;

import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiException;

import javax.swing.*;
import java.awt.*;

public class ServiceDeletionDialog extends Panel {
    JTextField serviceNameField = new JTextField();

    private final EventBrokerServicesApi api;
    private final String serviceId;
    private final String serviceName;

    public ServiceDeletionDialog(EventBrokerServicesApi api, String serviceId, String serviceName) {
        this.api = api;
        this.serviceId = serviceId;
        this.serviceName = serviceName;

        setLayout(new GridLayout(0, 1));
        add(new JLabel("To confirm deletion, enter the service's name below"));

        JLabel serviceNameLabel = new JLabel(serviceName);
        serviceNameLabel.setFont(serviceNameLabel.getFont().deriveFont(Font.BOLD));
        add(serviceNameLabel);
        add(serviceNameField);
    }

    public boolean deleteService() {
        if (isInputValid()) {
            try {
                api.deleteService(serviceId);
                return true;
            } catch (ApiException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                // There is a bug in the Mission Control spec where status is defined as camelCase but actual response is UPPERCASE
                if (e.getMessage().startsWith("Unexpected value")) {
                    return true;
                } else {
                    throw e;
                }
            }
        } else {
            JOptionPane.showConfirmDialog(null, "Entered name does not match", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    private boolean isInputValid() {
        return serviceNameField.getText().equals(serviceName);
    }
}

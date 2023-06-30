package community.solace.mc.idea.plugin.settings;

import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class AppSettingsPanel {
    private final JPanel settingsPanel;
    private final JBTextField baseUrlField = new JBTextField("console.solace.cloud");
    private final JBPasswordField apiTokenField = new JBPasswordField();
    public AppSettingsPanel() {
        settingsPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JLabel("Base URL"), baseUrlField)
                .addLabeledComponent(new JLabel("API Token"), apiTokenField)
                .getPanel();
    }

    public JPanel getPanel() { return settingsPanel; }

    public String getBaseUrl() {
        return baseUrlField.getText();
    }

    public String getApiToken() {
        return String.copyValueOf(apiTokenField.getPassword());
    }

    public void setBaseUrl(String baseUrl) {
        baseUrlField.setText(baseUrl);
    }

    public void setApiToken(String apiToken) {
        apiTokenField.setText(apiToken);
    }
}

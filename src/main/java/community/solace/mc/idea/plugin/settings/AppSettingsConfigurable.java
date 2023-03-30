package community.solace.mc.idea.plugin.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {
    private AppSettingsPanel appSettingsPanel;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Solace Mission Control";
    }

    @Override
    public @Nullable JComponent createComponent() {
        appSettingsPanel = new AppSettingsPanel();
        return appSettingsPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();

        boolean modified = !appSettingsPanel.getBaseUrl().equals(settings.url);
        modified |= !appSettingsPanel.getApiToken().equals(settings.getToken());

        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.setToken(appSettingsPanel.getApiToken());
        settings.url = appSettingsPanel.getBaseUrl();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        appSettingsPanel.setApiToken(settings.getToken());
        appSettingsPanel.setBaseUrl(settings.url);
    }

    @Override
    public void disposeUIResources() {
        appSettingsPanel = null;
    }
}

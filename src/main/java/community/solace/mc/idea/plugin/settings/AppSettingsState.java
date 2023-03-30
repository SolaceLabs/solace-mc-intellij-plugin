package community.solace.mc.idea.plugin.settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.solace.mc.api.EventBrokerServicesApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "community.solace.mc.idea.plugin.settings.AppSettingsState",
        storages = @Storage("SolaceCloudMissionControl.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {
    private static final String TOKEN_KEY = "token";
    public String url = "console.solace.cloud";
    private EventBrokerServicesApi api;

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    public String getToken() {
        String apiToken = null;
        CredentialAttributes credentialAttributes = createCredentialAttributes(TOKEN_KEY);

        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        if (credentials != null) {
            apiToken = credentials.getPasswordAsString();
        }

        return apiToken;
    }

    public void setToken(String token) {
        CredentialAttributes credentialAttributes = createCredentialAttributes(TOKEN_KEY);
        PasswordSafe.getInstance().set(credentialAttributes, new Credentials(TOKEN_KEY, token));

        // Anytime the settings are updated, we should set the token in the API
        // However, setToken is run on load before we assign the API client
        // The tool window initialization will set the token in the API later on for this case
        if (api != null) {
            setTokenInApi();
        }
    }

    public void setTokenInApi() {
        String token = getToken();

        if (token != null) {
            api.getApiClient().setBearerToken(token);
        } else {
            Notifications.Bus.notify(new Notification("Solace Mission Control", "Configure token in settings", NotificationType.ERROR));
        }
    }

    @Override
    public @Nullable AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void setApi(EventBrokerServicesApi api) {
        this.api = api;
        setTokenInApi();
    }

    private CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(
                CredentialAttributesKt.generateServiceName("community.solace.mc.idea.plugin", key)
        );
    }
}

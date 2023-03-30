package community.solace.mc.idea.plugin.pubsub;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;

import java.util.Properties;

public class MessagingClient {
    public static void main(String[] args) {
    }

    public static MessagingService messagingService(String host, String vpnName, String username, String password) {
        final Properties properties = new Properties();
        properties.setProperty(SolaceProperties.TransportLayerProperties.HOST, "tcps://" + host);
        properties.setProperty(SolaceProperties.ServiceProperties.VPN_NAME, vpnName);
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_USER_NAME, username);
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_PASSWORD, password);

        MessagingService messagingService = MessagingService.builder(ConfigurationProfile.V1).fromProperties(properties).build();
        messagingService.connect();
        return messagingService;
    }
}

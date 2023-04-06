package community.solace.mc.idea.plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.TextTransferable;
import community.solace.mc.idea.plugin.pubsub.MessagingClient;
import community.solace.mc.idea.plugin.rest.MissionControlCallback;
import community.solace.mc.idea.plugin.ui.common.IconButton;
import community.solace.mc.idea.plugin.ui.common.SolaceMessageTable;
import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiException;
import com.solace.mc.model.ConnectionEndpoint;
import com.solace.mc.model.GetService;
import com.solace.mc.model.GetServiceResponse;
import com.solace.messaging.MessagingService;
import com.solace.messaging.publisher.DirectMessagePublisher;
import com.solace.messaging.receiver.MessageReceiver;
import com.solace.messaging.resources.Topic;
import com.solace.messaging.resources.TopicSubscription;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServiceDetailsTab extends SimpleToolWindowPanel {
    private static final Set<String> EXPAND = Set.of("broker", "serviceConnectionEndpoints");
    JComboBox<String> endpointList = new ComboBox<>();
    IconButton pubButton = new IconButton(MyIcons.Pub, "Publish");
    JTextField pubTopic = new JTextField();
    JTextField pubMessage = new JTextField();
    IconButton subButton = new IconButton(AllIcons.General.Add, "Subscribe");
    JTextField subTopic = new JTextField();
    SubscriptionListPanel subscriptionListPanel = new SubscriptionListPanel();
    String vpnName;
    String username;
    String password;
    MessagingService messagingService;
    DirectMessagePublisher publisher;

    public ServiceDetailsTab(EventBrokerServicesApi api, String id) {
        super(false, true);

        setLayout(new BorderLayout());

        // Display the endpoints for the service
        JPanel connectionPanel = new JPanel(new BorderLayout());
        connectionPanel.add(new JLabel("Endpoint"), BorderLayout.LINE_START);
        connectionPanel.add(endpointList, BorderLayout.CENTER);

        // Add buttons to toolbar for connecting, disconnecting, and copying endpoint details
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ConnectToEndpointAction());
        actionGroup.addSeparator();
        actionGroup.add(new CopyEndpointsAction());

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("mc-toolbar", actionGroup, false);
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());

        add(connectionPanel, BorderLayout.PAGE_START);

        // Add Try Me pub/sub panels
        createPubSubPanels();

        try {
            // Get endpoint details
            api.getServiceAsync(id, EXPAND, new GetServiceCallback());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void createPubSubPanels() {
        JPanel pub = new JPanel(new BorderLayout());
        pub.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        SolaceMessageTable pubTable = new SolaceMessageTable(pubTopic, pubMessage);
        pubTable.getTable().getEmptyText().setText("Publish");
        pub.add(pubTable, BorderLayout.CENTER);

        pubButton.setEnabled(false);
        pubTopic.setEnabled(false);
        pubMessage.setEnabled(false);

        ActionListener publishMessage = e -> {
            if (publisher != null && publisher.isReady() && !pubTopic.getText().isBlank() && !pubMessage.getText().isBlank()) {
                publisher.publish(pubMessage.getText(), Topic.of(pubTopic.getText()));
                ((DefaultTableModel) pubTable.getTable().getModel()).addRow(new String[] {pubTopic.getText(), pubMessage.getText()});
            }
        };

        pubMessage.addActionListener(publishMessage);
        pubButton.addActionListener(publishMessage);

        JPanel messageSenderPanel = new JPanel(new BorderLayout());
        messageSenderPanel.add(pubMessage, BorderLayout.CENTER);
        messageSenderPanel.add(pubButton, BorderLayout.LINE_END);

        JPanel pubFields = FormBuilder.createFormBuilder()
                .addLabeledComponent("Topic ", pubTopic)
                .addLabeledComponent("Message ", messageSenderPanel)
                .getPanel();
        pubFields.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pub.add(pubFields, BorderLayout.PAGE_END);

        JPanel sub = new JPanel(new BorderLayout());
        sub.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        SolaceMessageTable subTable = new SolaceMessageTable(pubTopic, pubMessage);
        subTable.getTable().getEmptyText().setText("Subscribe");
        sub.add(subTable, BorderLayout.CENTER);

        final MessageReceiver.MessageHandler handler = (msg) -> ((DefaultTableModel) subTable.getTable().getModel()).addRow(new Object[] {msg.getDestinationName(), msg.getPayloadAsString()});

        subButton.setEnabled(false);
        subTopic.setEnabled(false);

        ActionListener subscribeToTopic = e -> {
            if (!subTopic.getText().isBlank()) {
                messagingService.createDirectMessageReceiverBuilder()
                        .withSubscriptions(TopicSubscription.of(subTopic.getText()))
                        .build().start().receiveAsync(handler);

                subscriptionListPanel.addSubscription(subTopic.getText());
                subTopic.setText("");
            }
        };

        subTopic.addActionListener(subscribeToTopic);
        subButton.addActionListener(subscribeToTopic);

        JPanel topicSubscriberPanel = new JPanel(new BorderLayout());
        topicSubscriberPanel.add(subTopic, BorderLayout.CENTER);
        topicSubscriberPanel.add(subButton, BorderLayout.LINE_END);

        JPanel subFields = new JPanel(new BorderLayout());
        subFields.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        subFields.add(subscriptionListPanel, BorderLayout.PAGE_START);
        subFields.add(FormBuilder.createFormBuilder().addLabeledComponent("Topic ", topicSubscriberPanel).getPanel(), BorderLayout.CENTER);
        sub.add(subFields, BorderLayout.PAGE_END);

        JBSplitter pubsub = new JBSplitter();
        pubsub.setFirstComponent(pub);
        pubsub.setSecondComponent(sub);
        add(pubsub, BorderLayout.CENTER);
    }

    private void setInputStates(boolean connected) {
        pubButton.setEnabled(connected);
        pubTopic.setEnabled(connected);
        pubMessage.setEnabled(connected);
        subButton.setEnabled(connected);
        subTopic.setEnabled(connected);
        endpointList.setEnabled(!connected);
    }

    private void connectToEndpoint(String hostname) {
        // In case already connected to another endpoint, tear it down
        if (messagingService != null) {
            messagingService.disconnect();
        }

        messagingService = MessagingClient.messagingService(hostname, vpnName, username, password);
        publisher = messagingService.createDirectMessagePublisherBuilder().build().start();
        setInputStates(true);
    }

    public void close() {
        // Tear down messaging clients
        if (messagingService != null && messagingService.isConnected()) {
            messagingService.disconnectAsync();
        }
    }

    private class GetServiceCallback implements MissionControlCallback<GetServiceResponse> {
        @Override
        public void onSuccess(GetServiceResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
            GetService response = result.getData();

            vpnName = response.getBroker().getMsgVpns().get(0).getMsgVpnName();
            username = response.getBroker().getMsgVpns().get(0).getServiceLoginCredential().getUsername();
            password = response.getBroker().getMsgVpns().get(0).getServiceLoginCredential().getPassword();

            for (ConnectionEndpoint endpoint : response.getServiceConnectionEndpoints()) {
                for (String hostname : endpoint.getHostNames()) {
                    endpointList.addItem(hostname + ":" + endpoint.getPorts().get("serviceSmfTlsListenPort"));
                }
            }
        }
    }

    // Used by "Connect" toolbar button to know when connection is available
    public boolean isConnectable() {
        if (endpointList.getItemCount() >= 1) {
            if (messagingService == null || !messagingService.isConnected()) {
                return true;
            }
        }

        return false;
    }

    // Used by "Disconnect" toolbar button to know when disconnection is available
    public boolean isDisconnectable() {
        return messagingService != null && messagingService.isConnected();
    }

    private class ConnectToEndpointAction extends AnAction {
        public ConnectToEndpointAction() {
            super("Connect", "Connect to service", MyIcons.Disconnected);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            if (isConnectable()) {
                connectToEndpoint((String) endpointList.getSelectedItem());
            } else if (isDisconnectable()) {
                close();
                setInputStates(false);
                subscriptionListPanel.clear();
            }
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            if (isConnectable()) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setIcon(MyIcons.Disconnected);
                e.getPresentation().setText("Connect");
                e.getPresentation().setDescription("Connect to the event broker service");
            } else if (isDisconnectable()) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setIcon(MyIcons.Connected);
                e.getPresentation().setText("Disconnect");
                e.getPresentation().setDescription("Disconnect from event broker service");
            } else {
                e.getPresentation().setEnabled(false);
            }
        }
    }

    private class CopyEndpointsAction extends AnAction {
        private final JPopupMenu endpointMenu = new JPopupMenu();

        public CopyEndpointsAction() {
            super("Copy Endpoint", "Copy selected endpoint details", AllIcons.Actions.Copy);

            JMenuItem copyConnection = new JMenuItem("Copy Connection Properties");
            JMenuItem copyUrl = new JMenuItem("Copy URL");
            JMenuItem copyVpn = new JMenuItem("Copy VPN Name");
            JMenuItem copyUsername = new JMenuItem("Copy Username");
            JMenuItem copyPassword = new JMenuItem("Copy Password");

            endpointMenu.add(copyConnection);
            endpointMenu.add(copyUrl);
            endpointMenu.add(copyVpn);
            endpointMenu.add(copyUsername);
            endpointMenu.add(copyPassword);

            copyConnection.addActionListener(e -> copyToClipboard(
                    String.format("host: '%s'\nmsgVpn: %s\nclientUsername: %s\nclientPassword: %s",
                            "tcps://" + endpointList.getSelectedItem(),
                            vpnName, username, password)));
            copyUrl.addActionListener(e -> copyToClipboard("tcps://" + endpointList.getSelectedItem()));
            copyVpn.addActionListener(e -> copyToClipboard(vpnName));
            copyUsername.addActionListener(e -> copyToClipboard(username));
            copyPassword.addActionListener(e -> copyToClipboard(password));
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Component c = e.getInputEvent().getComponent();
            endpointMenu.show(c, 0, 0);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(endpointList.getItemCount() >= 1);
        }

        private void copyToClipboard(String contents) {
            CopyPasteManager.getInstance().setContents(new TextTransferable(contents));
        }
    }
}

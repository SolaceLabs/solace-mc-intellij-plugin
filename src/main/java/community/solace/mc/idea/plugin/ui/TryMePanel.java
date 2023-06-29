package community.solace.mc.idea.plugin.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.util.ui.FormBuilder;
import com.solace.messaging.MessagingService;
import com.solace.messaging.publisher.DirectMessagePublisher;
import com.solace.messaging.resources.Topic;
import com.solace.messaging.resources.TopicSubscription;
import community.solace.mc.idea.plugin.pubsub.MessagingClient;
import community.solace.mc.idea.plugin.ui.common.IconButton;
import community.solace.mc.idea.plugin.ui.common.SolaceMessageTable;
import community.solace.mc.idea.plugin.ui.common.SolaceTable;
import icons.MyIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TryMePanel extends JPanel {
    IconButton pubButton = new IconButton(MyIcons.Pub, "Publish");
    JTextField pubTopic = new JTextField();
    JTextField pubMessage = new JTextField();

    private String vpnName;
    private String username;
    private String password;
    private final DefaultComboBoxModel<String> connectionEndpoints = new DefaultComboBoxModel<>();
    private final ComboBox<String> connectionEndpointsComboBox = new ComboBox<>(connectionEndpoints);
    private final DefaultTableModel subscriptionTableModel = new DefaultTableModel();
    private final SolaceMessageTable messageTable;

    MessagingService messagingService;
    DirectMessagePublisher publisher;

    public TryMePanel() {
        setLayout(new BorderLayout());

        setVisible(false);
        messageTable = new SolaceMessageTable(pubTopic, pubMessage);

        SolaceTable subscriptionTable = new SolaceTable(subscriptionTableModel, new JPopupMenu(), e -> {});
        subscriptionTableModel.setColumnIdentifiers(new Object[]{"Subscriptions"});

        connectionEndpointsComboBox.setEnabled(false);
        add(connectionEndpointsComboBox, BorderLayout.PAGE_START);

        OnePixelSplitter parentTryMeSplitter = new OnePixelSplitter(true);
        parentTryMeSplitter.setFirstComponent(subscriptionTable);

        JPanel pubSubPanel = new JPanel(new BorderLayout());
        pubSubPanel.add(messageTable, BorderLayout.CENTER);
        pubSubPanel.add(createPubPanel(), BorderLayout.PAGE_END);

        parentTryMeSplitter.setSecondComponent(pubSubPanel);
        parentTryMeSplitter.setProportion(0.3f);

        add(parentTryMeSplitter, BorderLayout.CENTER);
    }

    public void init(List<String> endpoints, String vpnName, String username, String password) {
        destroy();

        this.vpnName = vpnName;
        this.username = username;
        this.password = password;

        for (String e : endpoints) {
            connectionEndpoints.addElement(e);
        }

        connectionEndpointsComboBox.setEnabled(true);
    }

    public void connectToEndpoint() {
        // In case already connected to another endpoint, tear it down
        disconnectFromEndpoint();

        messagingService = MessagingClient.messagingService(connectionEndpointsComboBox.getSelectedItem().toString(), vpnName, username, password);
        publisher = messagingService.createDirectMessagePublisherBuilder().build().start();
        setInputStates(true);
    }

    public void disconnectFromEndpoint() {
        // Tear down messaging clients
        if (messagingService != null && messagingService.isConnected()) {
            messagingService.disconnectAsync();
            setInputStates(false);
        }

        subscriptionTableModel.setRowCount(0);
    }

    public void destroy() {
        username = null;
        password = null;
        connectionEndpointsComboBox.setEnabled(false);
        connectionEndpoints.removeAllElements();
        ((DefaultTableModel) messageTable.getTable().getModel()).setRowCount(0);

        disconnectFromEndpoint();
    }

    // Used by "Connect" toolbar button to know when connection is available
    public boolean isConnectable() {
        if (connectionEndpointsComboBox.getItemCount() >= 1) {
            if (messagingService == null || !messagingService.isConnected()) {
                return true;
            }
        }

        return false;
    }

    public void setInputStates(boolean connected) {
        pubButton.setEnabled(connected);
        pubTopic.setEnabled(connected);
        pubMessage.setEnabled(connected);
    }

    // Used by "Disconnect" toolbar button to know when disconnection is available
    public boolean isDisconnectable() {
        return messagingService != null && messagingService.isConnected();
    }

    private JPanel createPubPanel() {
        pubButton.setEnabled(false);
        pubTopic.setEnabled(false);
        pubMessage.setEnabled(false);

        ActionListener publishMessage = e -> {
            if (publisher != null && publisher.isReady() && !pubTopic.getText().isBlank() && !pubMessage.getText().isBlank()) {
                publisher.publish(pubMessage.getText(), Topic.of(pubTopic.getText()));
            }
        };

        pubMessage.addActionListener(publishMessage);
        pubButton.addActionListener(publishMessage);

        JPanel messageSenderPanel = new JPanel(new BorderLayout());
        messageSenderPanel.add(pubMessage, BorderLayout.CENTER);
        messageSenderPanel.add(pubButton, BorderLayout.LINE_END);

        JPanel pubFields = FormBuilder.createFormBuilder()
                .addSeparator()
                .addSeparator()
                .addComponent(new JLabel("Publish"))
                .addLabeledComponent("Topic ", pubTopic)
                .addLabeledComponent("Message ", messageSenderPanel)
                .getPanel();

        return pubFields;
    }

    public void subscribeToTopic(String topic) {
        messagingService.createDirectMessageReceiverBuilder()
                .withSubscriptions(TopicSubscription.of(topic))
                .build().start().receiveAsync((msg) -> ((DefaultTableModel) messageTable.getTable().getModel()).addRow(new Object[] {msg.getDestinationName(), msg.getPayloadAsString()}));

        subscriptionTableModel.addRow(new Object[]{topic});
    }
}
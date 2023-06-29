package community.solace.mc.idea.plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import community.solace.mc.idea.plugin.ui.common.RotatedLabel;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class TryMeControlPanel extends JPanel {
    TryMePanel tryMePanel;

    public TryMeControlPanel(TryMePanel tryMePanel) {
        setLayout(new BorderLayout());
        this.tryMePanel = tryMePanel;

        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction hideButton = new AnAction("Hide", "Hides list of services", AllIcons.Actions.ArrowExpand) {
            @Override
            public void update(@NotNull AnActionEvent e) {
                // Expand and collapse are reversed because the toolbar is on the right
                if (tryMePanel.isVisible()) {
                    e.getPresentation().setIcon(AllIcons.Actions.ArrowExpand);
                } else {
                    e.getPresentation().setIcon(AllIcons.Actions.ArrowCollapse);
                }
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                tryMePanel.setVisible(!tryMePanel.isVisible());
            }
        };

        AnAction subscribeButton = new AnAction("Subscribe", "Subscribe to a topic", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                JTextField subscriptionInput = new JTextField("", 18);

                JBPopup subPopup = JBPopupFactory.getInstance().createComponentPopupBuilder(subscriptionInput, null)
//                        .setTitle("Subscribe")
                        .setAdText("Subscribe to topic")
                        .setRequestFocus(true)
                        .setCancelOnClickOutside(true)
                        .setCancelOnOtherWindowOpen(false)
                        .setOkHandler(() -> tryMePanel.subscribeToTopic(subscriptionInput.getText()))
                        .createPopup();

                subscriptionInput.addActionListener(l -> subPopup.closeOk(null));
                subPopup.showUnderneathOf(e.getInputEvent().getComponent());
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(tryMePanel.isDisconnectable());
            }
        };

        actionGroup.add(hideButton);
        actionGroup.addSeparator();
        actionGroup.add(new ConnectToEndpointAction());
        actionGroup.add(subscribeButton);

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("mc-toolbar", actionGroup, false);
        actionToolbar.setTargetComponent(this);

        add(actionToolbar.getComponent(), BorderLayout.CENTER);
        add(new RotatedLabel("Try Me   ", false), BorderLayout.PAGE_END);
    }

    private class ConnectToEndpointAction extends AnAction {
        public ConnectToEndpointAction() {
            super("Connect", "Connect to service", MyIcons.Disconnected);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            if (tryMePanel.isConnectable()) {
                tryMePanel.connectToEndpoint();
            } else if (tryMePanel.isDisconnectable()) {
                tryMePanel.disconnectFromEndpoint();
                tryMePanel.setInputStates(false);
            }
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            if (tryMePanel.isConnectable()) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setIcon(MyIcons.Disconnected);
                e.getPresentation().setText("Connect");
                e.getPresentation().setDescription("Connect to the event broker service");
            } else if (tryMePanel.isDisconnectable()) {
                e.getPresentation().setEnabled(true);
                e.getPresentation().setIcon(MyIcons.Connected);
                e.getPresentation().setText("Disconnect");
                e.getPresentation().setDescription("Disconnect from event broker service");
            } else {
                e.getPresentation().setEnabled(false);
            }
        }
    }
}

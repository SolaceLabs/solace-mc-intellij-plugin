package community.solace.mc.idea.plugin.ui;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class SubscriptionListPanel extends JScrollPane {
    private final JPanel subscriptions;

    public SubscriptionListPanel() {
        super();

        subscriptions = new JPanel();
        subscriptions.setLayout(new BoxLayout(subscriptions, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension((int) getPreferredSize().getWidth(), 40));

        setViewportView(subscriptions);
        addSpacer();
    }

    public void addSubscription(String subscription) {
        subscriptions.add(new SubLabel(subscription));
        addSpacer();
        subscriptions.revalidate();
    }

    private void addSpacer() {
        subscriptions.add(Box.createRigidArea(new Dimension(5,0)));
    }

    private static class SubLabel extends JLabel {
        private static final Color SOLACE_GREEN = Color.decode("#06c895");

        public SubLabel(String subscription) {
            setBorder(BorderFactory.createLineBorder(SOLACE_GREEN));
            setForeground(JBColor.BLACK);
            setBackground(SOLACE_GREEN);
            setOpaque(true);
            setText(subscription);
            setVerticalAlignment(JLabel.TOP);
        }
    }

    public void clear() {
        subscriptions.removeAll();
        subscriptions.revalidate();
    }
}

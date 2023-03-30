package community.solace.mc.idea.plugin.ui.common;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.TextTransferable;
import community.solace.mc.idea.plugin.rest.RestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.Consumer;

/**
 * An extension of the SolaceTable for the "Try Me" functionality in the service details page.
 * Both publish and subscribe functionality of "Try Me" support the same actions:
 * - Display the topic and message of the sent/received message
 * - Allow copying of the topic or message from the table
 * - Pre-populate the publish fields with a topic and message from the table
 * - Double-click on a table row to view the message and topic in a dialog window
 */
public class SolaceMessageTable extends SolaceTable {
    private SolaceMessageTable(DefaultTableModel messageTableModel, JPopupMenu messagePopUpMenu, Consumer<String[]> doubleClickHandler, JTextField pubTopicField, JTextField pubMsgField) {
        super(messageTableModel, messagePopUpMenu, doubleClickHandler);

        messageTableModel.setColumnIdentifiers(new Object[] {"Topic", "Message"});

        JMenuItem copyTopic = new JMenuItem("Copy Topic");
        JMenuItem copyMessage = new JMenuItem("Copy Message");
        JMenuItem resendMessage = new JMenuItem("Resend");
        messagePopUpMenu.add(copyTopic);
        messagePopUpMenu.add(copyMessage);
        messagePopUpMenu.add(resendMessage);

        copyTopic.addActionListener(e -> copyToClipboard(getValueForSelectedRow(0)));
        copyMessage.addActionListener(e -> copyToClipboard(getValueForSelectedRow(1)));
        resendMessage.addActionListener(e -> {
            String topic = getValueForSelectedRow(0);
            String message = getValueForSelectedRow(1);

            pubTopicField.setText(topic);
            pubMsgField.setText(message);
        });

        getVerticalScrollBar().addAdjustmentListener(
                e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
    }

    public SolaceMessageTable(JTextField pubTopicField, JTextField pubMsgField) {
        this(new DefaultTableModel(), new JPopupMenu(), (row) -> {
            String message = RestUtil.prettyPrint(row[1]);
            new MessageDialogWrapper(row[0], message).show();
        }, pubTopicField, pubMsgField);
    }

    private void copyToClipboard(String contents) {
        CopyPasteManager.getInstance().setContents(new TextTransferable(contents));
    }

    /**
     * Used to display message details when double-clicked on from the table
     */
    private static class MessageDialogWrapper extends DialogWrapper {
        private final String message;

        public MessageDialogWrapper(String topic, String message) {
            super(true); // use current window as parent
            setTitle(topic);
            this.message = message;
            init();
        }

        @Override
        protected Action @NotNull [] createActions() {
            return new Action[]{getOKAction()};
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel dialogPanel = new JPanel(new BorderLayout());

            JTextArea messageTextArea = new JTextArea(message);
            dialogPanel.add(messageTextArea, BorderLayout.CENTER);

            return dialogPanel;
        }
    }
}

package community.solace.mc.idea.plugin.ui.common;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class PopupTextInput {
    public static void show(String title, Consumer<String> okHandler, Component component) {
        JTextField textInput = new JTextField("", 18);

        JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(textInput, null)
                .setTitle(title)
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setCancelOnOtherWindowOpen(false)
                .setOkHandler(() -> okHandler.accept(textInput.getText()))
                .createPopup();

        textInput.addActionListener(l -> popup.closeOk(null));

        popup.showUnderneathOf(component);
    }
}

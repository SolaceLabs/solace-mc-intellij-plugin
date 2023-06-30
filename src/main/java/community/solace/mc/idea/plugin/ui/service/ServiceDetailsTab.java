package community.solace.mc.idea.plugin.ui.service;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

import javax.swing.*;
import java.awt.*;

// Generic tab for service details with a toolbar on top. Directly add AnActions to the actionGroup
public class ServiceDetailsTab extends JPanel {
    final DefaultActionGroup actionGroup = new DefaultActionGroup();

    public ServiceDetailsTab() {
        setLayout(new BorderLayout());

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("mc-toolbar", actionGroup, true);
        actionToolbar.setTargetComponent(this);
        add(actionToolbar.getComponent(), BorderLayout.PAGE_START);
    }
}

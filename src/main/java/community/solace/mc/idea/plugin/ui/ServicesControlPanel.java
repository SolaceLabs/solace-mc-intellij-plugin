package community.solace.mc.idea.plugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import community.solace.mc.idea.plugin.ui.common.RotatedLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ServicesControlPanel extends JPanel {
    ServiceListTab serviceListTab;

    public ServicesControlPanel(ServiceListTab serviceListTab) {
        setLayout(new BorderLayout());
        this.serviceListTab = serviceListTab;

        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction hideButton = new AnAction("Hide", "Hides list of services", AllIcons.Actions.ArrowCollapse) {
            @Override
            public void update(@NotNull AnActionEvent e) {
                if (serviceListTab.isVisible()) {
                    e.getPresentation().setIcon(AllIcons.Actions.ArrowCollapse);
                    e.getPresentation().setText("Hide");
                    e.getPresentation().setDescription("Hide list of services");
                } else {
                    e.getPresentation().setIcon(AllIcons.Actions.ArrowExpand);
                    e.getPresentation().setText("Show");
                    e.getPresentation().setDescription("Show list of services");
                }
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                serviceListTab.setVisible(!serviceListTab.isVisible());
            }
        };

        AnAction refreshButton = new AnAction("Refresh", "Refresh list of services", AllIcons.Actions.Refresh) {
            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setVisible(serviceListTab.isVisible());
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                serviceListTab.refreshTable();
            }
        };

        AnAction createButton = new AnAction("Create", "Create a new service", AllIcons.General.Add) {
            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setVisible(serviceListTab.isVisible());
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int result = JOptionPane.showConfirmDialog(e.getInputEvent().getComponent(), serviceListTab.serviceCreationDialog.getDialogPanel(), "Create Service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    if (serviceListTab.serviceCreationDialog.createService()) {
                        serviceListTab.refreshTable();
                    }
                }
            }
        };

        actionGroup.add(hideButton);
        actionGroup.addSeparator();
        actionGroup.add(refreshButton);
        actionGroup.addSeparator("Service List");
        actionGroup.add(createButton);

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("mc-toolbar", actionGroup, false);
        actionToolbar.setTargetComponent(this);

        add(actionToolbar.getComponent(), BorderLayout.CENTER);
        add(new RotatedLabel("   Event Broker Service List"), BorderLayout.PAGE_END);
    }
}

package community.solace.mc.idea.plugin.ui.service;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.util.ui.TextTransferable;
import community.solace.mc.idea.plugin.ui.common.SolaceTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InfoTab extends ServiceDetailsTab {
    public InfoTab() {
        super();
    }

    public void init(String vpnName, String username, String password, List<String> endpoints) {
        DefaultTableModel infoTableModel = new DefaultTableModel();
        infoTableModel.setColumnIdentifiers(new String[]{"Attribute", "Value"});
        infoTableModel.addRow(new Object[]{"VPN Name", vpnName});
        infoTableModel.addRow(new Object[]{"Username", username});
        infoTableModel.addRow(new Object[]{"Password", password});

        for (String e : endpoints) {
            infoTableModel.addRow(new Object[]{"Endpoint", e});
        }

        add(new SolaceTable(infoTableModel, new JPopupMenu(), (s) -> {}), BorderLayout.CENTER);
        actionGroup.add(new CopyEndpointsAction(vpnName, username, password, endpoints));
    }

    private static class CopyEndpointsAction extends AnAction {
        private final JPopupMenu endpointMenu = new JPopupMenu();

        public CopyEndpointsAction(String vpnName, String username, String password, List<String> endpoints) {
            super("Copy Endpoint", "Copy selected endpoint details", AllIcons.Actions.Copy);

            JMenu copyConnection = new JMenu("Copy Connection Properties");
            JMenu copyUrl = new JMenu("Copy URL");
            JMenuItem copyVpn = new JMenuItem("Copy VPN Name");
            JMenuItem copyUsername = new JMenuItem("Copy Username");
            JMenuItem copyPassword = new JMenuItem("Copy Password");

            for (String e : endpoints) {
                JMenuItem copyUrlItem = new JMenuItem("tcps://" + e);
                copyUrlItem.addActionListener(a -> copyToClipboard("tcps://" + e));
                copyUrl.add(copyUrlItem);

                JMenuItem copyConnectionItem = new JMenuItem(e);
                copyConnectionItem.addActionListener(a -> copyToClipboard(
                        String.format("host: '%s'\nmsgVpn: %s\nclientUsername: %s\nclientPassword: %s",
                                "tcps://" + e, vpnName, username, password)));
                copyConnection.add(copyConnectionItem);
            }

            endpointMenu.add(copyConnection);
            endpointMenu.add(copyUrl);
            endpointMenu.add(copyVpn);
            endpointMenu.add(copyUsername);
            endpointMenu.add(copyPassword);

            copyVpn.addActionListener(e -> copyToClipboard(vpnName));
            copyUsername.addActionListener(e -> copyToClipboard(username));
            copyPassword.addActionListener(e -> copyToClipboard(password));
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Component c = e.getInputEvent().getComponent();
            endpointMenu.show(c, 0, 0);
        }

        private void copyToClipboard(String contents) {
            CopyPasteManager.getInstance().setContents(new TextTransferable(contents));
        }
    }
}

package community.solace.mc.idea.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.solace.mc.api.EventBrokerServicesApi;

import javax.swing.*;
import java.awt.*;

public class ParentPanel extends JPanel {
    ServiceListTab serviceListTab;
    public ParentPanel(Project project, EventBrokerServicesApi api) {
        super();

        setLayout(new BorderLayout());

        ThreeComponentsSplitter mainPanel = new ThreeComponentsSplitter(false, true, project);
        mainPanel.setFirstSize(300);
        mainPanel.setLastSize(300);

        JPanel servicePlaceholderPanel = new JPanel();
        servicePlaceholderPanel.add(new JLabel("Select a service from the list"));

        TryMePanel tryMePanel = new TryMePanel();

        serviceListTab = new ServiceListTab(api, (id, name) -> {
            mainPanel.setInnerComponent(new ServicePanel(api, id, tryMePanel));
            mainPanel.revalidate();
            serviceListTab.setVisible(false);
            tryMePanel.setVisible(true);
        });
        ServicesControlPanel servicesControlPanel = new ServicesControlPanel(serviceListTab);
        serviceListTab.refreshTable();

        mainPanel.setFirstComponent(serviceListTab);
        mainPanel.setInnerComponent(servicePlaceholderPanel);
        mainPanel.setLastComponent(tryMePanel);

        add(servicesControlPanel, BorderLayout.LINE_START);
        add(mainPanel, BorderLayout.CENTER);
        add(new TryMeControlPanel(tryMePanel), BorderLayout.LINE_END);
    }
}

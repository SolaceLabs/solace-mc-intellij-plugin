package community.solace.mc.idea.plugin;

import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import community.solace.mc.idea.plugin.settings.AppSettingsState;
import community.solace.mc.idea.plugin.ui.ServiceDetailsTab;
import community.solace.mc.idea.plugin.ui.ServiceListTab;
import com.intellij.openapi.wm.ToolWindow;
import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiClient;

import javax.swing.*;
import java.awt.*;

public class SolaceMissionControlToolWindow {
  JPanel servicePanel;
  ApiClient apiClient;
  EventBrokerServicesApi api;
  ToolWindow toolWindow;

  public SolaceMissionControlToolWindow(ToolWindow toolWindow) {
    apiClient = new ApiClient();
    api = new EventBrokerServicesApi(apiClient);
    AppSettingsState.getInstance().setApi(api);

    this.toolWindow = toolWindow;

    servicePanel = new JPanel(new BorderLayout());

    ServiceListTab serviceListTab = new ServiceListTab(api, (id, name) -> createServiceDetailsTab(name, new ServiceDetailsTab(api, id)));
    servicePanel.add(serviceListTab, BorderLayout.CENTER);
    serviceListTab.refreshTable();
  }

  private void createServiceDetailsTab(String title, ServiceDetailsTab serviceDetailsTab) {
    ContentManager contentManager = toolWindow.getContentManager();
    Content existingContent = contentManager.findContent(title);

    if (existingContent != null) {
      contentManager.setSelectedContent(existingContent);
    } else {
      final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
      Content content = contentFactory.createContent(serviceDetailsTab, title, true);
      content.setCloseable(true);

      content.addPropertyChangeListener((evt) -> {
        // If the tab was closed
        if (evt.getNewValue() == null) {
          // Disconnect any active messaging services
          serviceDetailsTab.close();
        }
      });

      contentManager.addContent(content);
      contentManager.setSelectedContent(content);
    }
  }

  public JPanel getContent() {
    return servicePanel;
  }
}

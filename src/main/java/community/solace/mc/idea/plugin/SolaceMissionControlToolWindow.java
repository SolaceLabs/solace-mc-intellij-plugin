package community.solace.mc.idea.plugin;

import com.intellij.openapi.project.Project;
import community.solace.mc.idea.plugin.settings.AppSettingsState;
import community.solace.mc.idea.plugin.ui.ParentPanel;
import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiClient;

import javax.swing.*;

public class SolaceMissionControlToolWindow {
  ApiClient apiClient;
  EventBrokerServicesApi api;
  Project project;
  ParentPanel parentPanel;

  public SolaceMissionControlToolWindow(Project project) {
    this.project = project;
    apiClient = new ApiClient();
    api = new EventBrokerServicesApi(apiClient);
    AppSettingsState.getInstance().setApi(api);

    parentPanel = new ParentPanel(project, api);
  }

  public JPanel getContent() {
    return parentPanel;
  }
}

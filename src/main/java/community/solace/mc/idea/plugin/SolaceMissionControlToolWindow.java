package community.solace.mc.idea.plugin;

import com.intellij.openapi.project.Project;
import community.solace.mc.idea.plugin.settings.AppSettingsState;
import community.solace.mc.idea.plugin.ui.ParentPanel;
import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiClient;

import javax.swing.*;

public class SolaceMissionControlToolWindow {
  private final ParentPanel parentPanel;

  public SolaceMissionControlToolWindow(Project project) {
    ApiClient apiClient = new ApiClient();
    EventBrokerServicesApi api = new EventBrokerServicesApi(apiClient);
    AppSettingsState.getInstance().setApi(api);

    parentPanel = new ParentPanel(project, api);
  }

  public JPanel getContent() {
    return parentPanel;
  }
}

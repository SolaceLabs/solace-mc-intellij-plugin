package community.solace.mc.idea.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class SolaceMissionControlToolWindowFactory implements ToolWindowFactory {
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    SolaceMissionControlToolWindow solaceMissionControlToolWindow = new SolaceMissionControlToolWindow(toolWindow);
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(solaceMissionControlToolWindow.getContent(), "Services", false);
    content.setCloseable(false);
    toolWindow.getContentManager().addContent(content);
  }
}

package community.solace.mc.idea.plugin.rest;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.solace.mc.invoker.ApiCallback;
import com.solace.mc.invoker.ApiException;
import community.solace.mc.idea.plugin.settings.AppSettingsConfigurable;

import java.util.List;
import java.util.Map;

public interface MissionControlCallback<T> extends ApiCallback<T> {
    default void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
        if (e.getCode() == 401) {
            errorNotification("Token invalid. Re-configure token in settings. Error message: " + RestUtil.getMessage(e.getResponseBody()));
        } else if (e.getCode() == 403) {
            errorNotification("Token does not have permissions to perform action. Error message: " + RestUtil.getMessage(e.getResponseBody()));
        } else {
            errorNotification("An error has occurred. Error message: " + RestUtil.getMessage(e.getResponseBody()));
        }
    }

    default void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

    }

    default void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

    }

    default void errorNotification(String message) {
        Notification notification = new Notification("Solace Mission Control", message, NotificationType.ERROR);
        notification.addAction(NotificationAction.create("Open Solace Mission Control settings", (e, n) -> {
            DataContext dataContext = e.getDataContext();
            Project project = PlatformDataKeys.PROJECT.getData(dataContext);
            ShowSettingsUtil.getInstance().showSettingsDialog(project, AppSettingsConfigurable.class);
        }));
        Notifications.Bus.notify(notification);
    }
}

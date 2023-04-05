package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface MyIcons {
    Icon ToolbarIcon = IconLoader.getIcon("/icons/mc_toolbar.svg", MyIcons.class);
    Icon Pub = IconLoader.getIcon("/icons/send.svg", MyIcons.class);
    Icon Connected = IconLoader.getIcon("/icons/connected.svg", MyIcons.class);
    Icon Disconnected = IconLoader.getIcon("/icons/disconnected.svg", MyIcons.class);
}

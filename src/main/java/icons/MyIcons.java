package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface MyIcons {
    Icon ToolbarIcon = IconLoader.getIcon("/icons/mc_toolbar.svg", MyIcons.class);
    Icon Pub = IconLoader.getIcon("/icons/send.svg", MyIcons.class);
}

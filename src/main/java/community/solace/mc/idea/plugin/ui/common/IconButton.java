package community.solace.mc.idea.plugin.ui.common;

import javax.swing.*;

public class IconButton extends JButton {
    public IconButton(Icon icon, String tooltip) {
        super(icon);
        setToolTipText(tooltip);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
    }
}

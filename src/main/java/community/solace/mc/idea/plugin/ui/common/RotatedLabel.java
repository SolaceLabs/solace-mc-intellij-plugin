package community.solace.mc.idea.plugin.ui.common;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * Rotate a JLabel 90 or 270 degrees
 */
public class RotatedLabel extends JLabel {
    private boolean needsRotate;
    private final boolean bottomUp;

    public RotatedLabel(Icon image) {
        this(image, true);
    }

    public RotatedLabel(String text) {
        this(text, true);
    }

    public RotatedLabel(Icon image, boolean bottomUp) {
        super(image);
        this.bottomUp = bottomUp;
    }

    public RotatedLabel(String text, boolean bottomUp) {
        super(text);
        this.bottomUp = bottomUp;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        return new Dimension(preferredSize.height, preferredSize.width);
    }

    @Override
    public Dimension getSize() {
        if (!needsRotate) {
            return super.getSize();
        }

        Dimension size = super.getSize();
        return new Dimension(size.height, size.width);
    }

    @Override
    public int getHeight() {
        return getSize().height;
    }

    @Override
    public int getWidth() {
        return getSize().width;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gr = (Graphics2D) g.create();

        if (bottomUp) {
            gr.translate(0, getSize().getHeight());
            gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
        } else {
            gr.transform(AffineTransform.getQuadrantRotateInstance(1));
            gr.translate(0, -getSize().getWidth());
        }

        needsRotate = true;
        super.paintComponent(gr);
        needsRotate = false;
    }
}

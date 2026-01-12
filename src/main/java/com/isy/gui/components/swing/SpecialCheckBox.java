package com.isy.gui.components.swing;

import javax.swing.*;
import java.awt.*;

public class SpecialCheckBox extends JCheckBox {
    private int radius = 15;

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 20;

        // Achtergrond
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, getHeight(), radius, radius);

        width /= 2;
        width -= g2.getFontMetrics().stringWidth(getText()) / 2;

        g.translate(width, 0);
        super.paintComponent(g);
        g.translate(-width, 0);
    }
}

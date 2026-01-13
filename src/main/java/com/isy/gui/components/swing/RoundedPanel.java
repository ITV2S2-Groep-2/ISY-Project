package com.isy.gui.components.swing;

import com.isy.gui.Style;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int radius = 15;

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        setBackground(Style.primaryComponentBackgroundColor);
        setPreferredSize(new Dimension(256, 64));
        setMaximumSize(new Dimension(256, 64));
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Achtergrond
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
    }
}

package com.isy.gui.components.swing;

import javax.swing.*;
import java.awt.*;

public class RoundedTextField extends JTextField {
    private int radius = 15;
    private Color backgroundColor;
    private Color textColor;

    public RoundedTextField(Color backgroundColor, Color textColor){
        super();
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;

        setFont(UIManager.getDefaults().getFont("TabbedPane.font"));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        setPreferredSize(new Dimension(256, 64));
        setMaximumSize(new Dimension(256, 64));
        setForeground(this.textColor);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, 0, 0, getWidth(), getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Achtergrond
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int x = (getWidth() - fm.stringWidth(text)) / 2;


        g.translate(x, 0);

        super.paintComponent(g);

        g.translate(-x, 0);
    }
}

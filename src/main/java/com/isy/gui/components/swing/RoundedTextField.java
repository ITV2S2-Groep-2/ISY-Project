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
    }

    @Override
    protected void paintBorder(Graphics g) {

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Achtergrond
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Tekst
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();

        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, x, y);

        g2.dispose();
    }
}

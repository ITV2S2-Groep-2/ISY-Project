package com.isy.gui.components;

import com.isy.gui.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RoundedButton extends JButton {
    private int radius = 15;
    private Color backgroundColor;
    private Color textColor;

    public RoundedButton(String text, Color backgroundColor, Color textColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(256, 64));
    }
    public RoundedButton(String text, String langKey, Color backgroundColor, Color textColor, ActionListener actionListener, @Nullable Object... params) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(256, 64));

        LangHandler.get().bind(this::setText, langKey, params);
        this.addActionListener(actionListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Achtergrond
        if (getModel().isArmed()) {
            g2.setColor(backgroundColor.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(backgroundColor.brighter());
        } else {
            g2.setColor(backgroundColor);
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Tekst
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}


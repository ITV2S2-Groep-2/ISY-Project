package com.isy.gui.components.swing;

import com.isy.util.lang.LangHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class RoundedComboBox<T> extends JComboBox<T> {
    private int radius = 15;
    private Color backgroundColor;
    private Color textColor;

    public RoundedComboBox(T[] elements, Color backgroundColor, Color textColor){
        super(elements);

        this.backgroundColor = backgroundColor;
        this.textColor = textColor;

        this.setBackground(this.backgroundColor);
        this.setForeground(this.textColor);

        setFont(UIManager.getDefaults().getFont("TabbedPane.font"));
        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(256, 64));
        setMaximumSize(new Dimension(256, 64));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Achtergrond
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        //Tekst
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        Object o = getSelectedItem();
        String text = o == null ? null : o.toString();

        if (text != null){
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, x, y);
        }

        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
    }

    public static class ComboBoxTranslatedElement{
        private final String langKeyPrefix;
        private final String value;

        public ComboBoxTranslatedElement(String langKeyPrefix, String value){
            this.langKeyPrefix = langKeyPrefix;
            this.value = value;
        }

        @Override
        public String toString() {
            return LangHandler.get().translate(this.langKeyPrefix + value.toLowerCase());
        }

        public String getValue() {
            return this.value;
        }
    }
}

package com.isy.gui.components;

import com.isy.gui.Style;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class BoardTile {

    private static JButton addStyle(JButton button) {
        button.setBackground(Style.primaryComponentBackgroundColor);
        button.setForeground(Style.primaryTextColor);
        button.setBorder(new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor));

        button.setPreferredSize(new Dimension(80, 80));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        return button;
    }

    public static JButton createButton() {
        JButton button = new JButton();
        return addStyle(button);
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        return addStyle(button);
    }
}

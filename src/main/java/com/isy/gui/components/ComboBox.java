package com.isy.gui.components;

import com.isy.gui.Style;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.StrokeBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class ComboBox {
    public static JComboBox createComboBox(Object[] elements) {
        JComboBox box = new JComboBox(elements);

        box.setBackground(Style.primaryComponentBackgroundColor);
        box.setForeground(Style.primaryTextColor);
        box.setBorder(BorderFactory.createCompoundBorder(
                new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor),
                new EmptyBorder(10, 10, 10, 10)
        ));

        box.setPreferredSize(new Dimension(256, 64));

        box.setFocusable(false);
        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton("▼");
                arrow.setFont(new Font("Arial", Font.BOLD, 16));
                arrow.setBorder(BorderFactory.createEmptyBorder());
                arrow.setContentAreaFilled(false);
                arrow.setFocusPainted(false);
                arrow.setOpaque(false);
                arrow.setForeground(Style.primaryTextColor); // color of dropdown arrow
                return arrow;
            }
        });

        return box;
    }
}

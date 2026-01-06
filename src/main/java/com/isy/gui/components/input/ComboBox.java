package com.isy.gui.components.input;

import com.isy.gui.Style;
import com.isy.gui.components.swing.RoundedComboBox;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class ComboBox {
    public static <T> JComboBox<T> createComboBox(T[] elements) {
        JComboBox<T> box = new RoundedComboBox<>(elements, Style.primaryComponentBackgroundColor, Style.primaryTextColor);

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

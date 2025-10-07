package com.isy.gui.components;

import com.isy.gui.Style;

import javax.swing.*;

public class Label {

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Style.primaryTextColor);
        return label;
    }
}

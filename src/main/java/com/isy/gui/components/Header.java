package com.isy.gui.components;

import com.isy.gui.Style;

import javax.swing.*;
import java.awt.*;

public class Header {

    public static JLabel createHeader(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Style.primaryTextColor);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        return label;
    }
}

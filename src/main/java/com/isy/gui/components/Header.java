package com.isy.gui.components;

import com.isy.gui.Style;
import com.isy.gui.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class Header {
    public static JLabel createHeader(String langKey, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JLabel label = new JLabel(text);
        label.setForeground(Style.primaryTextColor);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        //bind the label
        LangHandler.get().bind(label, langKey, params);

        return label;
    }
}

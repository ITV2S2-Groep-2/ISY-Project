package com.isy.gui.components;

import com.isy.gui.Style;
import com.isy.gui.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Label {
    public static JLabel createLabel() {
        JLabel label = new JLabel();
        label.setForeground(Style.primaryTextColor);
        return label;
    }

    public static JLabel createLabel(String langKey, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JLabel label = new JLabel(text);
        label.setForeground(Style.primaryTextColor);

        //bind the label
        LangHandler.get().bind(label, langKey, params);

        return label;
    }
}

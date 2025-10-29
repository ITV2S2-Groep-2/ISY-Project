package com.isy.gui.components;

import com.isy.gui.Style;
import com.isy.util.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class TextField {

    public static JTextField createTextField(String langKey, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JTextField textField = new JTextField(text);
        textField.setBackground(Style.primaryComponentBackgroundColor);
        textField.setForeground(Style.primaryTextColor);
        textField.setBorder(BorderFactory.createCompoundBorder(
                new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor),
                new EmptyBorder(10, 10, 10, 10)
        ));

        textField.setPreferredSize(new Dimension(256, 64));

        textField.setCaretColor(Style.primaryTextColor);

        LangHandler.get().bind(textField::setText, langKey, params);

        return textField;
    }

    public static JTextField createTextField() {
        return createTextField("");
    }
}

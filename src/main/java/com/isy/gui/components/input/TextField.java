package com.isy.gui.components.input;

import com.isy.gui.Style;
import com.isy.gui.components.swing.RoundedTextField;
import com.isy.util.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class TextField {
    public static JTextField createTextField() {
        return new RoundedTextField(Style.primaryComponentBackgroundColor, Style.primaryTextColor);
    }

    public static JTextField createTextField(String langKey, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JTextField textField = createTextField();
        textField.setText(text);

        textField.setPreferredSize(new Dimension(256, 64));

        LangHandler.get().bind(textField::setText, langKey, params);

        return textField;
    }
}

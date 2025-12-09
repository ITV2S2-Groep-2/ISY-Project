package com.isy.gui.components;

import com.isy.gui.Style;
import com.isy.gui.components.swing.RoundedButton;
import com.isy.util.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

public class UIButton {

    public static JButton createButton() {
        return new RoundedButton(Style.primaryComponentBackgroundColor, Style.primaryTextColor);
    }

    public static JButton createButton(String langKey, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JButton button = createButton();
        button.setText(text);

        LangHandler.get().bind(button::setText, langKey, params);

        return button;
    }

    public static JButton createButton(String langKey, ActionListener listener, @Nullable Object... params) {
        JButton button = createButton(langKey, params);
        button.addActionListener(listener);

        return button;
    }

    public static JButton createButton(ActionListener listener) {
        JButton button = createButton();
        button.addActionListener(listener);
        return button;
    }

    public static JButton createButton(Icon icon) {
        JButton button = createButton();
        button.setIcon(icon);
        return button;
    }

    public static JButton createButton(String langKey, Icon icon, @Nullable Object... params) {
        JButton button = createButton(langKey, params);
        button.setIcon(icon);

        return button;
    }

}

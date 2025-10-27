package com.isy.gui.components;

import com.isy.gui.Style;
import com.isy.gui.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.StrokeBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class UIButton {

    private static JButton addStyle(JButton button) {
        button.setBackground(Style.primaryComponentBackgroundColor);
        button.setForeground(Style.primaryTextColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor),
                new EmptyBorder(10, 10, 10, 10)
        ));

        button.setPreferredSize(new Dimension(256, 64));

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

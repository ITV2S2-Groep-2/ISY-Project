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

    public static JButton createButton(String text) {
        return new RoundedButton(
                text,
                Style.primaryComponentBackgroundColor,
                Style.primaryTextColor
        );
    }

    public static JButton createButton(String langKey, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JButton button = new JButton(text);

        LangHandler.get().bind(button::setText, langKey, params);

        return addStyle(button);
    }

//    public static JButton createButton(String langKey, ActionListener listener, @Nullable Object... params) {
//        String text = LangHandler.get().translate(langKey, params);
//
//        JButton button = new JButton(text);
//        button.addActionListener(listener);
//
//        LangHandler.get().bind(button::setText, langKey, params);
//
//        return addStyle(button);
//    }
    public static JButton createButton(String langKey, ActionListener listener, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);
        return new RoundedButton(
                text,
                langKey,
                Style.primaryComponentBackgroundColor,
                Style.primaryTextColor,
                listener,
                params
        );
    }

    public static JButton createButton(ActionListener listener) {
        JButton button = new JButton();
        button.addActionListener(listener);
        return addStyle(button);
    }

    public static JButton createButton(Icon icon) {
        JButton button = new JButton(icon);
        return addStyle(button);
    }

    public static JButton createButton(String langKey, Icon icon, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JButton button = new JButton(text, icon);

        LangHandler.get().bind(button::setText, langKey, params);

        return addStyle(button);
    }

}

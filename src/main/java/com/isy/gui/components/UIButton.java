package com.isy.gui.components;

import com.isy.gui.Style;
import com.isy.gui.components.swing.RoundedButton;
import com.isy.util.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIButton {
    public static final String clickSound = "ClickMenuObject.wav";
    public static final String hoverSound = "HoverSound.wav";

    public static JButton createButton() {
        return new RoundedButton(Style.primaryComponentBackgroundColor, Style.primaryTextColor);
    }

    public static JButton createButton(String langKey, @Nullable Object... params) {
        String text = LangHandler.get().translate(langKey, params);

        JButton button = createButton();
        button.setText(text);
        button.addActionListener(e -> SoundUtils.playSoundEffect(clickSound));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                SoundUtils.playSoundEffect(hoverSound);
            }
        });

        LangHandler.get().bind(button::setText, langKey, params);

        return button;
    }

    public static JButton createButton(String langKey, ActionListener listener, @Nullable Object... params) {
        JButton button = createButton(langKey, params);
        button.addActionListener(e -> {
            listener.actionPerformed(e);
            SoundUtils.playSoundEffect(clickSound);
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                SoundUtils.playSoundEffect(hoverSound);
            }
        });

        return button;
    }

    public static JButton createButton(ActionListener listener) {
        JButton button = createButton();
        button.addActionListener(e -> {
            listener.actionPerformed(e);
            SoundUtils.playSoundEffect(clickSound);
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                SoundUtils.playSoundEffect(hoverSound);
            }
        });

        return button;
    }

    public static JButton createButton(Icon icon) {
        JButton button = createButton();
        button.setIcon(icon);
        button.addActionListener(e -> SoundUtils.playSoundEffect(clickSound));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                SoundUtils.playSoundEffect(hoverSound);
            }
        });

        return button;
    }

    public static JButton createButton(String langKey, Icon icon, @Nullable Object... params) {
        JButton button = createButton(langKey, params);
        button.setIcon(icon);
        button.addActionListener(e -> SoundUtils.playSoundEffect(clickSound));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                SoundUtils.playSoundEffect(hoverSound);
            }
        });

        return button;
    }

}

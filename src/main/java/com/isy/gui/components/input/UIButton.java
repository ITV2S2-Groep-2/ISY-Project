package com.isy.gui.components.input;

import com.isy.gui.Style;
import com.isy.gui.components.SoundUtils;
import com.isy.gui.components.swing.RoundedButton;
import com.isy.util.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    public static JButton createButton(String langKey, ActionListener listener, int width, int height, @Nullable Object... params) {
        JButton button = createButton(langKey, listener, params);
        Dimension dim = new Dimension(width, height);
        button.setPreferredSize(dim);
        button.setMaximumSize(dim);

        return button;
    }

    public static JButton createButton(ActionListener listener, int width, int height, String resourceName, String langFallback) {
        JButton button = createButton(langFallback, listener);

        Dimension dim = new Dimension(width, height);
        button.setPreferredSize(dim);
        button.setMaximumSize(dim);

        try {
            Image img = ImageIO.read(UIButton.class.getResource(resourceName));
            Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImg));
            button.setText("");
        } catch (Exception ex) {}

        return button;
    }

}

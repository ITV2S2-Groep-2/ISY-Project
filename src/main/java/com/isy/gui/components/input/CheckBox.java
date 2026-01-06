package com.isy.gui.components.input;

import com.isy.gui.Style;
import com.isy.util.lang.LangHandler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;

public class CheckBox {
    public static JCheckBox createCheckBox() {
        JCheckBox checkBox = new JCheckBox();

        checkBox.setBackground(null);
        checkBox.setForeground(Style.primaryTextColor);

        checkBox.setFont(UIManager.getDefaults().getFont("TabbedPane.font"));
        checkBox.setFocusPainted(false);
        checkBox.setBorderPainted(false);
        checkBox.setContentAreaFilled(false);
        checkBox.setOpaque(false);
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBox.setPreferredSize(new Dimension(256, 64));

        return checkBox;
    }

    public static JCheckBox createCheckBox(String langKey, @Nullable Object... params) {
        JCheckBox checkBox = createCheckBox();

        String text = LangHandler.get().translate(langKey, params);
        checkBox.setText(text);

        LangHandler.get().bind(checkBox::setText, langKey, params);

        return checkBox;
    }

    public static JCheckBox createCheckBox(String langKey, ItemListener listener, @Nullable Object... params) {
        JCheckBox checkBox = createCheckBox(langKey, params);

        checkBox.addItemListener(listener);

        return checkBox;
    }
}

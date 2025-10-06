package com.isy.gui.components;

import com.isy.gui.Style;

import javax.swing.*;

public class ScenePanel {

    public static JPanel createScenePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Style.sceneBackgroundColor);
        panel.setForeground(Style.primaryTextColor);
        return panel;
    }
}

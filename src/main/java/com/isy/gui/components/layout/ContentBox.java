package com.isy.gui.components.layout;

import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;

public class ContentBox {
    private final JPanel contentPanel;
    private final @MagicConstant(valuesFromClass = javax.swing.BoxLayout.class) int axis;

    public ContentBox(ScenePanel panel, @MagicConstant(valuesFromClass = javax.swing.BoxLayout.class) int axis){
        this.axis = axis;
        this.contentPanel = new JPanel();

        this.contentPanel.setLayout(new BoxLayout(this.contentPanel, this.axis));
        this.contentPanel.setOpaque(false);

        panel.setLayout(new GridBagLayout());

        panel.add(this.contentPanel);
    }

    public void add(Component component, int margin){
        this.contentPanel.add(component);

        if (this.axis == BoxLayout.X_AXIS){
            this.contentPanel.add(Box.createHorizontalStrut(margin));
        }else{
            this.contentPanel.add(Box.createVerticalStrut(margin));
        }
    }
}

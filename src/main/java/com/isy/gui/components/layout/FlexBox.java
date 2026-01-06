package com.isy.gui.components.layout;

import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;

public class FlexBox {
    private final JPanel contentPanel;
    private final @MagicConstant(valuesFromClass = javax.swing.BoxLayout.class) int axis;

    public FlexBox(@MagicConstant(valuesFromClass = javax.swing.BoxLayout.class) int axis){
        this.axis = axis;
        this.contentPanel = new JPanel();

        this.contentPanel.setLayout(new BoxLayout(this.contentPanel, this.axis));
        this.contentPanel.setOpaque(false);
        this.contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.contentPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    public void add(Component component, int margin){
        this.contentPanel.add(component);

        if (this.axis == BoxLayout.X_AXIS){
            this.contentPanel.add(Box.createHorizontalStrut(margin));
        }else{
            this.contentPanel.add(Box.createVerticalStrut(margin));
        }
    }

    public JPanel getComponent() {
        return contentPanel;
    }
}

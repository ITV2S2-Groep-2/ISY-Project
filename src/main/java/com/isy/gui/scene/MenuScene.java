package com.isy.gui.scene;

import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public abstract class MenuScene extends Scene{
    private final GridBagConstraints constraints;

    public MenuScene(String name, Window window) {
        super(name, window);
        this.getScenePanel().setLayout(new GridBagLayout());

        this.constraints = generateConstrains();
    }

    public GridBagConstraints generateConstrains(){
        GridBagConstraints gd = new GridBagConstraints();
        gd.gridx = 0;
        gd.fill = GridBagConstraints.NONE;
        gd.anchor = GridBagConstraints.CENTER;
        gd.insets = new Insets(5, 0, 5, 0);

        return gd;
    }

    public GridBagConstraints getConstraints() {
        return constraints;
    }

    public JButton createDefaultButton(String text){
        JButton button = new JButton(text);
        button.setBackground(Style.primaryBackgroundColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                new StrokeBorder(new BasicStroke(2), Style.primaryBorderColor),
                new EmptyBorder(10, 10, 10, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        return button;
    }
}

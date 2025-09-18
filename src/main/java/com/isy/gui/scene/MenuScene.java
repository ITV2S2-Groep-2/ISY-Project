package com.isy.gui.scene;

import com.isy.gui.Window;

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
}

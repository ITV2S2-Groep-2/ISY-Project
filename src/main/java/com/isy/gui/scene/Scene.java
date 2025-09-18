package com.isy.gui.scene;

import com.isy.gui.Window;

import javax.swing.*;

public abstract class Scene {
    private final String name;
    private final JPanel scenePanel;
    private final Window window;
    private boolean show = false;

    public Scene(String name, Window window){
        this.name = name;
        this.window = window;
        this.scenePanel = new JPanel();
        this.show = false;

        this.scenePanel.setVisible(this.show);
    }

    public String getName(){
        return this.name;
    }

    public JPanel getScenePanel() {
        return scenePanel;
    }

    public Window getWindow() {
        return window;
    }

    public void show(){
        this.show = true;
        this.scenePanel.setVisible(this.show);
    }

    public void hide(){
        this.show = false;
        this.scenePanel.setVisible(this.show);
    }

    public boolean isHidden(){
        return this.show;
    }

    public abstract void init();
}

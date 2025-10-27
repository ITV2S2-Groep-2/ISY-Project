package com.isy.gui.scene;

import com.isy.game.Game;
import com.isy.gui.Window;
import com.isy.gui.components.ScenePanel;

import javax.swing.*;
import java.awt.*;

public abstract class Scene {
    private final String name;
    private final JPanel scenePanel;
    private final Window window;

    public Scene(String name, Window window){
        this.name = name;
        this.window = window;
        this.scenePanel = ScenePanel.createScenePanel();

        this.scenePanel.setName(this.name);
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

    public void initGame(Game game){}

    public abstract void init();

    public void show() {

    }
}

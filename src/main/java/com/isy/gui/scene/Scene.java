package com.isy.gui.scene;

import com.isy.game.Game;
import com.isy.gui.Window;
import com.isy.gui.components.ScenePanel;

import javax.swing.*;
import java.awt.*;

public abstract class Scene {
    private final String name;
    private final ScenePanel scenePanel;
    private final Window window;

    public Scene(String name, Window window){
        this.name = name;
        this.window = window;
        this.scenePanel = new ScenePanel();
        scenePanel.setBackgroundImage("/back2.jpg"); //dit is te gebruiken als je alle scenes dezelfde background wil geven
        this.show = false;

        this.scenePanel.setName(this.name);
    }

    public String getName(){
        return this.name;
    }

    public ScenePanel getScenePanel() {
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

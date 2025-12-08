package com.isy.gui.scene;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private final List<Scene> scenes;
    private final JPanel scenePresenter;
    private Scene currentScene = null;

    public SceneManager(){
        this.scenes = new ArrayList<>();
        this.scenePresenter = new JPanel(new CardLayout());
    }

    public void addScene(Scene scene, boolean autoShow){
        Scene existingScene = this.getScene(scene.getName());
        if (existingScene != null) {
            this.scenes.remove(existingScene);
        }
        this.scenes.add(scene);
        this.scenePresenter.add(scene.getScenePanel(), scene.getName());

        if (autoShow){
            currentScene = scene;
            CardLayout cl = (CardLayout) this.scenePresenter.getLayout();
            cl.show(this.scenePresenter, scene.getName());
        }
        scene.init();
    }

    public void addScene(Scene scene){
        this.addScene(scene, false);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public @Nullable Scene getScene(String name){
        for (Scene scene : this.scenes) {
            if (scene.getName().equals(name))
                return scene;
        }

        return null;
    }

    public void showScene(String name){
        Scene scene = getScene(name);
        if (scene != null){
            CardLayout cl = (CardLayout) scenePresenter.getLayout();
            cl.show(scenePresenter, name);
            currentScene = scene;
            currentScene.show();
        }
    }

    public JPanel generatePanel(){
        return scenePresenter;
    }
}

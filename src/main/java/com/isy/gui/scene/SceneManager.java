package com.isy.gui.scene;

import com.isy.gui.Window;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private final List<Scene> scenes;
    private Scene currentScene = null;
    private final Window window;

    public SceneManager(Window window){
        this.window = window;
        this.scenes = new ArrayList<>();
    }

    public void addScene(Scene scene, boolean autoShow){
        this.scenes.add(scene);

        if (autoShow){
            if (currentScene != null)
                currentScene.hide();

            scene.show();
            currentScene = scene;
        }

        scene.init();
    }

    public void addScene(Scene scene){
        this.addScene(scene, false);
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
            if (currentScene != null){
                currentScene.hide();
            }

            scene.show();
            currentScene = scene;
        }
    }

    public JPanel generatePanel(){
        JPanel panel = new JPanel();
        BorderLayout layout = new BorderLayout();
        panel.setLayout(layout);

        for (Scene scene : this.scenes) {
            panel.add(scene.getScenePanel(), BorderLayout.CENTER);
        }

        return panel;
    }
}

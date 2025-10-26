package com.isy.gui.scene;

import com.isy.game.GameType;
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
    private GameType currentGameType = null;
    private JPanel scenePresenter;

    public SceneManager(Window window){
        this.window = window;
        this.scenes = new ArrayList<>();
    }

    public void addScene(Scene scene, boolean autoShow){
        this.scenes.add(scene);
        if (this.scenePresenter != null) {
            this.scenePresenter.add(scene.getScenePanel(), scene.getName());
            CardLayout cl = (CardLayout) this.scenePresenter.getLayout();
            cl.show(this.scenePresenter, scene.getName());
        }

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
            JPanel panel = scene.getScenePanel().getParent() instanceof JPanel ? (JPanel) scene.getScenePanel().getParent() : null;
            if (panel != null) {
                CardLayout cl = (CardLayout) panel.getLayout();
                cl.show(panel, name);
            }
            currentScene = scene;
        }
    }

    public JPanel generatePanel(){
        JPanel panel = new JPanel(new CardLayout());

        for (Scene scene : this.scenes) {
            panel.add(scene.getScenePanel(), scene.getName());
        }

        ((CardLayout) panel.getLayout()).show(panel, currentScene.getName());

        this.scenePresenter = panel;
        return panel;
    }

    public GameType getCurrentGameType() {
        return currentGameType;
    }

    public void setCurrentGameType(GameType currentGameType) {
        this.currentGameType = currentGameType;
    }
}

package com.isy.gui;

import com.isy.gui.scene.*;
import com.isy.util.GameSettings;

import javax.swing.*;
import java.awt.*;

public class Window {
    private final SceneManager manager;

    public Window(){
        GameSettings.get();
        this.manager = new SceneManager();

        this.manager.addScene(new SettingsScene(this));
        this.manager.addScene(new WinScene(this));
        this.manager.addScene(new JoinGameServerMenuScene(this));
        this.manager.addScene(new LangSwitchScene(this));
        this.manager.addScene(new GameMenuScene(this));
        this.manager.addScene(new MainMenuScene(this), true);

        this.createAndShowGUI();
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout());
        frame.add(this.manager.generatePanel(), BorderLayout.CENTER);

        //Display the window.
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public SceneManager getManager() {
        return manager;
    }
}
package com.isy.gui;

import com.isy.gui.scene.*;
import com.isy.gui.scene.manager.SceneManager;
import com.isy.util.GameSettings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Window {
    private final SceneManager manager;

    public Window(){
        GameSettings.get();
        this.manager = new SceneManager();

        this.manager.addScene(new SettingsScene(this));
        this.manager.addScene(new JoinGameServerMenuScene(this));
        this.manager.addScene(new LangSwitchScene(this));
        this.manager.addScene(new MainMenuScene(this), true);

        this.createAndShowGUI();
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Games client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 880));

        try {
            Image img = ImageIO.read(getClass().getResource("/icon.png"));
            frame.setIconImage(img);
        } catch (Exception e) {
            System.out.println("Frame icon not found");
        }

        frame.setLayout(new BorderLayout());
        frame.add(this.manager.generatePanel(), BorderLayout.CENTER);

        //Display the window.
        frame.setSize(1000, 880); // Groter gemaakt voor othello
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public SceneManager getManager() {
        return manager;
    }
}
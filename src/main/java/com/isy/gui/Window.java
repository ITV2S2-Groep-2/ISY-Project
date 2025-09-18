package com.isy.gui;

import com.isy.Board;
import com.isy.Game;
import com.isy.gui.scene.Scene;
import com.isy.gui.scene.SceneManager;
import com.isy.gui.scene.TicTacToeMainMenuScene;
import com.isy.gui.scene.TicTacToeScene;

import javax.swing.*;
import java.awt.*;

public class Window {
    private final Game game;
//    private Board board;
    private final SceneManager manager;

    public Window(){
        this.game = new Game();

        this.manager = new SceneManager(this);
        this.manager.addScene(new TicTacToeScene(this), false);
        this.manager.addScene(new TicTacToeMainMenuScene(this), true);

        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout());
        frame.add(this.manager.generatePanel(), BorderLayout.CENTER);

        //Display the window.
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Board getBoard() {
        return this.game.getBoard();
    }

    public SceneManager getManager() {
        return manager;
    }
}

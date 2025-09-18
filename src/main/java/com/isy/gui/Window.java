package com.isy.gui;

import com.isy.Board;
import com.isy.gui.scene.Scene;
import com.isy.gui.scene.SceneManager;
import com.isy.gui.scene.TicTacToeMainMenuScene;
import com.isy.gui.scene.TicTacToeScene;

import javax.swing.*;
import java.awt.*;

public class Window {
    private Board board;
    private final SceneManager manager;

    public Window(){
        this.board = new Board();

        this.manager = new SceneManager(this);
        this.manager.addScene(new TicTacToeScene(this), true);
//        this.manager.addScene(new TicTacToeMainMenuScene(this), false);

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
        return board;
    }
}

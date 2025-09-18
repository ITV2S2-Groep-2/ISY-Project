package com.isy.gui.scene;

import com.isy.gui.Window;

import javax.swing.*;
import java.awt.*;

public class TicTacToeMainMenuScene extends MenuScene{
    public TicTacToeMainMenuScene(Window window) {
        super("ticTacToeMainMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(new JButton("Player VS Player(Offline)"), getConstraints());
        panel.add(new JButton("Player VS AI(Offline)"), getConstraints());
        panel.add(new JButton("AI VS AI(Offline)"), getConstraints());
        panel.setBackground(new Color(255, 0 , 0));
    }
}

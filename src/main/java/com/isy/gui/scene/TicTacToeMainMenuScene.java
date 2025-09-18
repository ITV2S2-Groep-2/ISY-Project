package com.isy.gui.scene;

import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;

public class TicTacToeMainMenuScene extends MenuScene{
    public TicTacToeMainMenuScene(Window window) {
        super("ticTacToeMainMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(createDefaultButton("Player VS Player(Offline)"), getConstraints());
        panel.add(createDefaultButton("Player VS AI(Offline)"), getConstraints());
        panel.add(createDefaultButton("AI VS AI(Offline)"), getConstraints());

        panel.setBackground(Style.menuBackgroundColor);
    }
}

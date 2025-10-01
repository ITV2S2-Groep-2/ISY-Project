package com.isy.gui.scene;

import com.isy.game.ticTacToe.*;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MainMenuScene extends MenuScene{

    public MainMenuScene(Window window) {
        super("MainMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(createHeader("Selecteer je spel!"));
        panel.add(createDefaultButton("Tic-tac-toe", this::goToTicTacToeMainMenu), getConstraints());
        panel.add(createDefaultButton("Othello"), getConstraints());
        panel.setBackground(Style.menuBackgroundColor);
    }

    private void goToTicTacToeMainMenu(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("ticTacToeMainMenu");
    }


}

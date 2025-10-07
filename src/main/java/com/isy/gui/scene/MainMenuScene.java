package com.isy.gui.scene;

import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MainMenuScene extends MenuScene{

    public MainMenuScene(Window window) {
        super("MainMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(Header.createHeader("select.game.header"));
        panel.add(UIButton.createButton("Tic-tac-toe", this::goToTicTacToeMainMenu), getConstraints());
        panel.add(UIButton.createButton("Othello"), getConstraints());
    }

    private void goToTicTacToeMainMenu(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("ticTacToeMainMenu");
    }


}

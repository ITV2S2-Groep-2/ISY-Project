package com.isy.gui.scene;

import com.isy.game.ticTacToe.TicTacToeGame;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TicTacToeMainMenuScene extends MenuScene{
    public TicTacToeMainMenuScene(Window window) {
        super("ticTacToeMainMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(createHeader("TicTacToe"));
        panel.add(createDefaultButton("Player VS Player(Offline)"), getConstraints());
        panel.add(createDefaultButton("Player VS AI(Offline)", this::startTicTacToeGame), getConstraints());
        panel.add(createDefaultButton("AI VS AI(Offline)"), getConstraints());

        panel.setBackground(Style.menuBackgroundColor);
    }

    private void startTicTacToeGame(ActionEvent actionEvent) {
        TicTacToeGame ticTacToeGame = new TicTacToeGame();
        ticTacToeGame.setRenderScene(this.getWindow().getManager().getScene("ticTacToe"));
        this.getWindow().getManager().showScene("ticTacToe");
    }
}

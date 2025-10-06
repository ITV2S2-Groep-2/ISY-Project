package com.isy.gui.scene;

import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WinScene extends MenuScene{
    private JLabel title;

    public WinScene(Window window) {
        super("winScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        title = Header.createHeader("TicTacToe");

        panel.add(title, getConstraints());
        panel.add(UIButton.createButton("Go back to main menu", this::goBackToMainMenu), getConstraints());
    }

    private void goBackToMainMenu(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("ticTacToeMainMenu");
    }

    public void win(String playerName){
        this.getWindow().getManager().showScene(this.getName());
        title.setText(playerName + " Won!");
    }
}

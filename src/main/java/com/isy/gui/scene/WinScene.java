package com.isy.gui.scene;

import com.isy.Main;
import com.isy.gui.PlayerEventManager;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WinScene extends MenuScene{
    private JLabel title;
    private boolean online;

    public WinScene(Window window) {
        super("winScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        title = Header.createHeader("TicTacToe");

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                onSceneShown();
            }
        });

        panel.add(title, getConstraints());
        panel.add(UIButton.createButton("Go back to main menu", this::goBackToMainMenu), getConstraints());
    }

    private void onSceneShown() {
        SwingUtilities.invokeLater(() -> {
            TicTacToeMainMenuScene tttmms = (TicTacToeMainMenuScene) Main.window.getManager().getScene("ticTacToeMainMenu");
            System.out.println("heb hem op false gezet in winscene");
            tttmms.setiStart(false);
        });
    }

    private void goBackToMainMenu(ActionEvent actionEvent) {

        // Join game button terugzetten
        SwingUtilities.invokeLater(() -> {
            JoinGameServerMenuScene joinScene = (JoinGameServerMenuScene) Main.window.getManager().getScene("joinGameServerMenuScene");
            joinScene.resetJoinButton();
        });

        if(online){
            this.getWindow().getManager().showScene("joinGameServerMenuScene");
        } else {
            this.getWindow().getManager().showScene("ticTacToeMainMenu");
        }
    }

    public void win(String playerName, boolean online){
        this.online = online;
        this.getWindow().getManager().showScene(this.getName());
        title.setText(playerName + " Won!");
    }

    public void lost(String playername, boolean online){
        this.online = online;
        this.getWindow().getManager().showScene(this.getName());
        title.setText(playername +" Lost!");
    }}

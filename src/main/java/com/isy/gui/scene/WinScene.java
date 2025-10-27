package com.isy.gui.scene;

import com.isy.Main;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.UIButton;
import com.isy.gui.lang.LangHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WinScene extends MenuScene{
    private JLabel title;
    private JButton backButton;
    private boolean online;

    public WinScene(Window window) {
        super("winScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        title = Header.createHeader("tic.tac.toe.header");

        backButton = UIButton.createButton("win_scene.go_back_to_main_menu.button", this::goBackToMainMenu, 3);

        panel.add(title, getConstraints());
        panel.add(backButton, getConstraints());
    }

    private void goBackToMainMenu(ActionEvent actionEvent) {
        if(online){
            // Join game button terugzetten
            SwingUtilities.invokeLater(() -> {
                JoinGameServerMenuScene joinScene = (JoinGameServerMenuScene) Main.window.getManager().getScene("joinGameServerMenuScene");
                joinScene.resetJoinButton();
            });

            this.getWindow().getManager().showScene("joinGameServerMenuScene");
        } else {
            this.getWindow().getManager().showScene("gameMenu");
        }
    }

    @Override
    public void show() {
        super.show();

        new Thread(() -> {
            try {
                backButton.setText(LangHandler.get().translate("win_scene.go_back_to_main_menu.button", 3));
                Thread.sleep(1000);
                backButton.setText(LangHandler.get().translate("win_scene.go_back_to_main_menu.button", 2));
                Thread.sleep(1000);
                backButton.setText(LangHandler.get().translate("win_scene.go_back_to_main_menu.button", 1));
                Thread.sleep(1000);
                goBackToMainMenu(null);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
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
    }
}

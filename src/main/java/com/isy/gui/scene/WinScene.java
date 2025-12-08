package com.isy.gui.scene;

import com.isy.Main;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.UIButton;
import com.isy.util.lang.LangHandler;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WinScene extends Scene{
    private JLabel title;
    private JButton backButton;
    private boolean online;

    private final GridBagConstraints constraints;

    public WinScene(Window window) {
        super("winScene", window);
        this.constraints = generateConstrains();
        this.getScenePanel().setLayout(new GridBagLayout());
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
                joinScene.setGameCreator(GameCreator.getCurrentInstance());
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
                if (this.getWindow().getManager().getCurrentScene() == this) {
                    goBackToMainMenu(null);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void win(String playerName, boolean online){
        this.online = online;
        this.getWindow().getManager().showScene(this.getName());
        title.setText(LangHandler.get().translate("win_scene.win_label", playerName));
    }

    public void lost(String playerName, boolean online){
        this.online = online;
        this.getWindow().getManager().showScene(this.getName());
        title.setText(LangHandler.get().translate("win_scene.lose_label", playerName));
    }

    public GridBagConstraints generateConstrains(){
        GridBagConstraints gd = new GridBagConstraints();
        gd.gridx = 0;
        gd.fill = GridBagConstraints.NONE;
        gd.anchor = GridBagConstraints.CENTER;
        gd.insets = new Insets(5, 0, 5, 0);

        return gd;
    }

    public GridBagConstraints getConstraints() {
        return constraints;
    }
}

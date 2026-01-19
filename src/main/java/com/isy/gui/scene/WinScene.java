package com.isy.gui.scene;

import com.isy.Main;
import com.isy.game.player.Player;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.input.UIButton;
import com.isy.gui.components.layout.ContentBox;
import com.isy.gui.scene.manager.Scene;
import com.isy.util.lang.LangHandler;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WinScene extends Scene {
    private JLabel title;
    private JButton backButton;
    private boolean online;

    public WinScene(Window window) {
        super("winScene", window);
        this.getScenePanel().setLayout(new GridBagLayout());
    }

    @Override
    public void init() {
        ContentBox content = new ContentBox(getScenePanel(), BoxLayout.Y_AXIS);

        title = Header.createHeader("game." + GameCreator.getCurrentInstance().getGameType().label + ".header");
        backButton = UIButton.createButton("win_scene.go_back_to_main_menu.button", this::goBackToMainMenu, 3);

        content.add(title, 20);
        content.add(backButton, 20);
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

    public void win(Player<?> player, boolean online){
        this.online = online;
        this.getWindow().getManager().showScene(this.getName());

        title.setIcon(null);

        if (player == null){
            title.setText(LangHandler.get().translate("win_scene.draw"));
        } else{
            title.setText(LangHandler.get().translate("win_scene.win_label", player.getName()));
            player.getSymbol().createDisplayIcon(title);
        }
    }
}

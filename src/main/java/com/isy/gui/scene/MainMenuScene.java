package com.isy.gui.scene;

import com.isy.Main;
import com.isy.game.GameType;
import com.isy.gui.Window;
import com.isy.gui.components.*;
import com.isy.gui.components.input.UIButton;
import com.isy.gui.components.layout.ContentBox;
import com.isy.gui.scene.manager.Scene;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MainMenuScene extends Scene {

    public MainMenuScene(Window window) {
        super("mainMenuScene", window);
    }

    @Override
    public void init() {
        getScenePanel().setBackgroundImage("/back2.jpg");
        SoundUtils.playBackgroundMusic("tttBack.wav");

        ContentBox content = new ContentBox(getScenePanel(), BoxLayout.Y_AXIS);

        content.add(Header.createHeader("select.game.header"), 20);

        content.add(UIButton.createButton("game." + GameType.TICTACTOE.label + ".select_button",
                e -> goToGameMenuSceneWithSelectedGame(GameType.TICTACTOE)), 20);

        content.add(UIButton.createButton("game." + GameType.OTHELLO.label + ".select_button",
                e -> goToGameMenuSceneWithSelectedGame(GameType.OTHELLO)), 20);

        content.add(UIButton.createButton(this::goToSettings, 48, 48,
                "/settings.png", "settings.menu.button"), 0);
    }

    private void goToGameMenuSceneWithSelectedGame(GameType game) {
        GameCreator.createNewInstance(game);
        Main.window.getManager().addScene(new GameMenuScene(Main.window), true);
        this.getWindow().getManager().showScene("gameMenu");
    }

    private void goToSettings(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("settingsScene");
    }
}

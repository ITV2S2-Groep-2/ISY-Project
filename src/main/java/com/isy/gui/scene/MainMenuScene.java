package com.isy.gui.scene;

import com.isy.game.GameType;
import com.isy.gui.Window;
import com.isy.gui.components.*;
import com.isy.util.GameCreator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.isy.gui.GridBagConstrainsUtil.*;

public class MainMenuScene extends Scene{

    public MainMenuScene(Window window) {
        super("mainMenuScene", window);
    }

    @Override
    public void init() {
        ScenePanel panel = this.getScenePanel();
        panel.setBackgroundImage("/back2.jpg"); // dit zorgt ervoor dat alleen deze scene deze background heeft
        SoundUtils.playBackgroundMusic("tttBack.wav");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        initConstraints(gbc, 1);

        panel.add(Header.createHeader("select.game.header"), next(gbc));
        panel.add(UIButton.createButton("tic.tac.toe.game.button", e -> goToGameMenuSceneWithSelectedGame(GameType.TICTACTOE)), next(row(gbc)));
        panel.add(UIButton.createButton("othello.game.button", e -> goToGameMenuSceneWithSelectedGame(GameType.OTHELLO)), next(row(gbc)));


        JButton settingsButton = UIButton.createButton(this::goToSettings);
        settingsButton.setPreferredSize(new Dimension(48, 48));
        try{
            Image img = ImageIO.read(getClass().getResource("/settings.png"));
            Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            settingsButton.setIcon(new ImageIcon(scaledImg));
        } catch(Exception ex){
            settingsButton.setText("settings.menu.button");
            System.out.println(ex);
        }
        panel.add(settingsButton, next(row(gbc)));
    }

    private void goToGameMenuSceneWithSelectedGame(GameType game) {
        GameCreator.createNewInstance(game);
        this.getWindow().getManager().showScene("gameMenu");
    }

    private void goToSettings(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("settingsScene");
    }
}

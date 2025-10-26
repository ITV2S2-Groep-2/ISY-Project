package com.isy.gui.scene;

import com.isy.Main;
import com.isy.game.GameType;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.UIButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainMenuScene extends MenuScene{

    public MainMenuScene(Window window) {
        super("mainMenuScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        panel.add(Header.createHeader("select.game.header"));
        panel.add(UIButton.createButton("tic.tac.toe.game.button", e -> goToGameMenuSceneWithSelectedGame(GameType.TICTACTOE)), getConstraints());
        panel.add(UIButton.createButton("othello.game.button", e -> goToGameMenuSceneWithSelectedGame(GameType.OTHELLO)), getConstraints());

        JButton settingsButton = UIButton.createButton(this::goToSettings);
        settingsButton.setPreferredSize(new Dimension(48, 48));
        gbc.gridy = 3;
        gbc.gridx = 0;
        try{
            Image img = ImageIO.read(getClass().getResource("/settings.png"));
            Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            settingsButton.setIcon(new ImageIcon(scaledImg));
        } catch(Exception ex){
            settingsButton.setText("settings.menu.button");
            System.out.println(ex);
        }
        panel.add(settingsButton, gbc);
    }

    private void goToGameMenuSceneWithSelectedGame(GameType game) {
        System.out.println("selected game: " + game.label);
        this.getWindow().getManager().setCurrentGameType(game);
        this.getWindow().getManager().addScene(new GameMenuScene(this.getWindow()), true);
    }

    private void goToSettings(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("settingsScene");
    }
}

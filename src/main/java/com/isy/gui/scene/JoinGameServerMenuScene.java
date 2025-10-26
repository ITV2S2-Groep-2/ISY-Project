package com.isy.gui.scene;

import com.isy.Main;
import com.isy.game.GameServer;
import com.isy.game.ticTacToe.GameState;
import com.isy.gui.PlayerEventManager;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.Label;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JoinGameServerMenuScene extends MenuScene {
    private GameServer client;
    private String ownName;
    private JButton joinButton;
    private JLabel waitingLabel;
    private JLabel errorLabel;

    public JoinGameServerMenuScene(Window window) {
        super("joinGameServerMenuScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        // TODO: Maak dit mooi
        panel.add(Header.createHeader("Wachten op tournament...."));
        panel.add(Box.createVerticalStrut(10));

        JLabel info = Label.createLabel("In plaats daarvan subscriben voor een direct potje!");
        panel.add(info);

        panel.add(UIButton.createButton("Leave server", this::goLeaveServer), getConstraints());

        joinButton = UIButton.createButton("Subscribe!");
        panel.add(joinButton, getConstraints());

        waitingLabel = Label.createLabel("Wachten op match...");
        waitingLabel.setVisible(false);
        panel.add(waitingLabel, getConstraints());

        errorLabel = Label.createLabel("Error message placeholder");
        errorLabel.setForeground(Color.RED);
        errorLabel.hide();
        panel.add(errorLabel);

        joinButton.addActionListener(this::onJoinButtonClicked);
    }

    public void setClient(GameServer client, String ownName) {
        this.client = client;
        this.ownName = ownName;
    }

    private void onJoinButtonClicked(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            TicTacToeMainMenuScene tttmms = (TicTacToeMainMenuScene) Main.window.getManager().getScene("ticTacToeMainMenu");
            tttmms.setiStart(false);
        });
        if (client == null) return;

        if(client != null){
            client.addListener(line -> {
                if(line.contains("ERR Player is not in a match currently")){
                    return;
                }
                if (line.contains("ERR")) {
                    errorLabel.setText(line);
                    errorLabel.show();
                }
            });
        }
        client.sendCommand("subscribe tic-tac-toe");

        joinButton.setVisible(false);
        waitingLabel.setVisible(true);

    }

    private void goLeaveServer(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("ticTacToeMainMenu");
        errorLabel.hide();
        this.resetJoinButton();
        client.shutdown();
    }

    public void resetJoinButton() {
        SwingUtilities.invokeLater(() -> {
            joinButton.setVisible(true);
            waitingLabel.setVisible(false);
        });
    }
}


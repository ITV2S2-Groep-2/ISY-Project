package com.isy.gui.scene;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.ticTacToe.*;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class JoinGameServerMenuScene extends MenuScene {
    private GameServer client;
    private String ownName;
    private JButton joinButton;
    private JLabel waitingLabel;

    public JoinGameServerMenuScene(Window window) {
        super("joinGameServerMenuScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(createHeader("Wachten op tournament...."));
        panel.add(Box.createVerticalStrut(10));

        JLabel info = new JLabel("In plaats daarvan subscriben voor een direct potje!");
        panel.add(info);

        joinButton = createDefaultButton("Subscribe!", null);
        panel.add(joinButton, getConstraints());

        waitingLabel = new JLabel("Wachten op match...");
        waitingLabel.setVisible(false);
        panel.add(waitingLabel, getConstraints());

        panel.setBackground(Style.menuBackgroundColor);

        joinButton.addActionListener(this::onJoinButtonClicked);
    }

    public void setClient(GameServer client, String ownName) {
        this.client = client;
        this.ownName = ownName;
    }

    private void onJoinButtonClicked(ActionEvent e) {
        if (client == null) return;
        client.sendCommand("subscribe tic-tac-toe");

        joinButton.setVisible(false);
        waitingLabel.setVisible(true);

    }

    public void resetJoinButton() {
        SwingUtilities.invokeLater(() -> {
            joinButton.setVisible(true);
            waitingLabel.setVisible(false);
        });
    }
}


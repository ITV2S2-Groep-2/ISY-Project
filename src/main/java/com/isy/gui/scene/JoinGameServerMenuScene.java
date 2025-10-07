package com.isy.gui.scene;

import com.isy.game.GameServer;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.Label;
import com.isy.gui.components.UIButton;

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

        panel.add(Header.createHeader("waiting.tournament.header"));
        panel.add(Box.createVerticalStrut(10));

        JLabel info = Label.createLabel("directly.subscribe.label");
        panel.add(info);

        panel.add(UIButton.createButton("Leave server", this::goLeaveServer), getConstraints());

        joinButton = UIButton.createButton("Subscribe!");
        panel.add(joinButton, getConstraints());

        waitingLabel = Label.createLabel("waiting.match.label");
        waitingLabel.setVisible(false);
        panel.add(waitingLabel, getConstraints());

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

    private void goLeaveServer(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("ticTacToeMainMenu");
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


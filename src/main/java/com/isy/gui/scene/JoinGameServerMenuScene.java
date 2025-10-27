package com.isy.gui.scene;

import com.isy.server.await.Promise;
import com.isy.server.Server;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.Label;
import com.isy.gui.components.UIButton;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.isy.server.ServerUtils.playerToMovePattern;
import static com.isy.server.await.Await.asyncAwait;
import static com.isy.server.await.Await.await;

public class JoinGameServerMenuScene extends MenuScene {
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

        panel.add(Header.createHeader("waiting.tournament.header"));
        panel.add(Box.createVerticalStrut(10));

        JLabel info = Label.createLabel("directly.subscribe.label");
        panel.add(info);

        panel.add(UIButton.createButton("leave.server.button", this::goLeaveServer), getConstraints());

        joinButton = UIButton.createButton("subscribe.server.button");
        panel.add(joinButton, getConstraints());

        waitingLabel = Label.createLabel("waiting.match.label");
        waitingLabel.setVisible(false);
        panel.add(waitingLabel, getConstraints());

        errorLabel = Label.createLabel("error.message.server.label", "null");
        errorLabel.setForeground(Color.RED);
        errorLabel.hide();
        panel.add(errorLabel);

        joinButton.addActionListener(this::onJoinButtonClicked);
    }

    public void setGameCreator(GameCreator creator) {
        this.ownName = creator.getPlayer1Name();

        asyncAwait(new Promise("^(SVR GAME MATCH|ERR).*"), (result) -> {
            boolean iStart;

            Matcher matcher = playerToMovePattern.matcher(result);

            if (result.toLowerCase().contains("err")){
                throw new RuntimeException("AAAAAAAAAAAAAAAAAAAAAAAA");
            }

            if (matcher.find()){
                String playerToMove = matcher.group(1);
                iStart = Objects.equals(playerToMove, ownName);

                if (iStart)
                    await(new Promise("^(SVR GAME YOURTURN).*"));
            } else {
                iStart = false;
            }

            SwingUtilities.invokeLater(() -> creator.startRemoteGame(iStart));
        });
    }

    private void onJoinButtonClicked(ActionEvent e) {
        if (Server.getInstance() == null) return;

        // TODO: based on gametype label
        await(new Promise().setCommand("subscribe tic-tac-toe"));

        joinButton.setVisible(false);
        waitingLabel.setVisible(true);

    }

    private void goLeaveServer(ActionEvent actionEvent) {
        this.getWindow().getManager().showScene("gameMenu");
        errorLabel.hide();
        this.resetJoinButton();
        Server.getInstance().shutdown();
    }

    public void resetJoinButton() {
        setGameCreator(GameCreator.getCurrentInstance());

        SwingUtilities.invokeLater(() -> {
            joinButton.setVisible(true);
            waitingLabel.setVisible(false);
        });
    }
}


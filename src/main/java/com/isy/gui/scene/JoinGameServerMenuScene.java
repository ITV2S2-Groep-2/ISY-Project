package com.isy.gui.scene;

import com.isy.game.GameType;
import com.isy.gui.components.layout.ContentBox;
import com.isy.gui.scene.manager.Scene;
import com.isy.server.await.Promise;
import com.isy.server.Server;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.Label;
import com.isy.gui.components.input.UIButton;
import com.isy.util.GameCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.isy.server.ServerUtils.opponentPattern;
import static com.isy.server.ServerUtils.playerToMovePattern;
import static com.isy.server.ServerUtils.asyncAwait;
import static com.isy.server.ServerUtils.await;

public class JoinGameServerMenuScene extends Scene {
    private String ownName;
    private JButton joinButton;
    private JLabel waitingLabel;
    private JLabel errorLabel;

    public JoinGameServerMenuScene(Window window) {
        super("joinGameServerMenuScene", window);
        this.getScenePanel().setLayout(new GridBagLayout());
    }

    @Override
    public void init() {
        ContentBox content = new ContentBox(getScenePanel(), BoxLayout.Y_AXIS);

        content.add(Header.createHeader("waiting.tournament.header"), 10);

        JLabel info = Label.createLabel("directly.subscribe.label");
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(info, 10);

        content.add(UIButton.createButton("leave.server.button", this::goLeaveServer), 10);

        joinButton = UIButton.createButton("subscribe.server.button");
        content.add(joinButton, 10);

        waitingLabel = Label.createLabel("waiting.match.label");
        waitingLabel.setVisible(false);
        waitingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(waitingLabel, 10);

        errorLabel = Label.createLabel("error.message.server.label", "null");
        errorLabel.setForeground(Color.RED);
        errorLabel.hide();
        content.add(errorLabel, 10);

        joinButton.addActionListener(this::onJoinButtonClicked);
    }

    public void setGameCreator(GameCreator creator) {
        this.ownName = creator.getPlayer1Name();

        asyncAwait(new Promise("^(SVR GAME MATCH).*"), (result) -> {
            boolean iStart;

            Matcher matcher = playerToMovePattern.matcher(result);
            Matcher playerRemoteName = opponentPattern.matcher(result);

            if (playerRemoteName.find())
                creator.setPlayer2Name(playerRemoteName.group(1));

            if (result.toLowerCase().contains("err")){
                return;
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

        GameType gameType = GameCreator.getCurrentInstance().getGameType();
        await(new Promise().setCommand("subscribe " + gameType.label));

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
        SwingUtilities.invokeLater(() -> {
            joinButton.setVisible(true);
            waitingLabel.setVisible(false);
        });
    }
}


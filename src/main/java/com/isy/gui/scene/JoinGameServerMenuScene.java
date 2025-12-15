package com.isy.gui.scene;

import com.isy.game.GameType;
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

import static com.isy.server.ServerUtils.opponentPattern;
import static com.isy.server.ServerUtils.playerToMovePattern;
import static com.isy.server.ServerUtils.asyncAwait;
import static com.isy.server.ServerUtils.await;

public class JoinGameServerMenuScene extends Scene {
    private String ownName;
    private JButton joinButton;
    private JLabel waitingLabel;
    private JLabel errorLabel;

    private final GridBagConstraints constraints;

    public JoinGameServerMenuScene(Window window) {
        super("joinGameServerMenuScene", window);
        this.constraints = generateConstrains();
        this.getScenePanel().setLayout(new GridBagLayout());
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


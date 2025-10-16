package com.isy.gui.scene;

import com.isy.Main;
import com.isy.await.Promise;
import com.isy.game.GameServer;
import com.isy.game.ticTacToe.GameState;
import com.isy.gui.PlayerEventManager;
import com.isy.gui.Window;
import com.isy.gui.components.Header;
import com.isy.gui.components.Label;
import com.isy.gui.components.UIButton;
import com.isy.gui.lang.LangHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static com.isy.await.Await.await;

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

    public void setClient(GameServer client, String ownName) {
        this.client = client;
        this.ownName = ownName;
    }

    private void onJoinButtonClicked(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            TicTacToeMainMenuScene tttmms = (TicTacToeMainMenuScene) Main.window.getManager().getScene("ticTacToeMainMenu");
            tttmms.setiStart(false);
            System.out.println("heb m via setistart op false gezet");
        });
        if (client == null) return;

//        if(client != null){
//            client.addListener(line -> {
//                if(line.contains("ERR Player is not in a match currently")){
//                    return;
//                }
//                if (line.contains("ERR")) {
//                    errorLabel.setText(LangHandler.get().translate("error.message.server.label", line));
//                    errorLabel.show();
//                }
//            });
//        }

        await(new Promise().setCommand("subscribe tic-tac-toe"));

//        client.sendCommand("subscribe tic-tac-toe");

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


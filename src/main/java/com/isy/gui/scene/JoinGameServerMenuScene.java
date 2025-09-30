package com.isy.gui.scene;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.ticTacToe.*;
import com.isy.gui.Style;
import com.isy.gui.Window;
import java.util.UUID;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class JoinGameServerMenuScene extends MenuScene {
    public JoinGameServerMenuScene(Window window) {
        super("joinGameServerMenuScene", window);
    }
    private JButton joinButton;
    private JLabel waitingLabel;

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(createHeader("Join een online game"));

        joinButton = createDefaultButton("Join game", null);
        panel.add(joinButton, getConstraints());

        waitingLabel = new JLabel("Wachten op match...");
        waitingLabel.setVisible(false);
        panel.add(waitingLabel, getConstraints());

        panel.setBackground(Style.menuBackgroundColor);

        joinButton.addActionListener(e -> {
            joinButton.setVisible(false);
            waitingLabel.setVisible(true);
            startTicTacToeGame(e, waitingLabel);
        });
    }

    public void resetJoinButton() {
        SwingUtilities.invokeLater(() -> {
            joinButton.setVisible(true);
            waitingLabel.setVisible(false);
        });
    }

    private void startTicTacToeGame(ActionEvent actionEvent, JLabel waitingLabel) {
        GameServer client = new GameServer("127.0.0.1", 7789);
        new Thread(client).start();

        String ownName = "speler" + UUID.randomUUID().toString().substring(0, 8);
        client.sendCommand("login " + ownName);
        client.sendCommand("subscribe tic-tac-toe");

        client.addListener(line -> {
            if (line.startsWith("SVR GAME MATCH")) {
                boolean iStart = line.toLowerCase().contains(ownName);
                SwingUtilities.invokeLater(() -> {
                    HumanPlayer human = new HumanPlayer(ownName, Tile.X, client);
                    RemotePlayer remotePlayer = new RemotePlayer("Tegenstander", Tile.O, client);

                    TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{
                            iStart ? human : remotePlayer,
                            iStart ? remotePlayer : human
                    });
                    ticTacToeGame.setClient(client);
                    TicTacToeScene ttts = (TicTacToeScene) this.getWindow().getManager().getScene("ticTacToe");
                    ticTacToeGame.setRenderScene(ttts);
                    ttts.setPlayerName(ownName);
                    new Thread(ticTacToeGame).start();
                    this.getWindow().getManager().showScene("ticTacToe");

                    waitingLabel.setVisible(false);
                });
            }
        });
    }

}


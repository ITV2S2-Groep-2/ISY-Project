package com.isy.gui.scene;

import com.isy.game.GameServer;
import com.isy.game.ticTacToe.TicTacToeGame;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class JoinGameServerMenuScene extends MenuScene {
    public JoinGameServerMenuScene(Window window) {
        super("joinGameServerMenuScene", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(createHeader("Join een online game"));

        JButton joinButton = createDefaultButton("Join game", null);
        panel.add(joinButton, getConstraints());

        JLabel waitingLabel = new JLabel("Wachten op match...");
        waitingLabel.setVisible(false);
        panel.add(waitingLabel, getConstraints());

        panel.setBackground(Style.menuBackgroundColor);

        joinButton.addActionListener(e -> {
            joinButton.setVisible(false);
            waitingLabel.setVisible(true);
            startTicTacToeGame(e, waitingLabel);
        });
    }

    private void startTicTacToeGame(ActionEvent actionEvent, JLabel waitingLabel) {
        GameServer client = new GameServer("127.0.0.1", 7789);
        new Thread(client).start();

        client.sendCommand("login speler" + System.currentTimeMillis() % 1000);
        client.sendCommand("subscribe tic-tac-toe");

        // Wacht tot server een match toewijst
        new Thread(() -> {
            try {
                String line;
                while ((line = client.readServerLine()) != null) {
                    if (line.startsWith("SVR GAME MATCH")) {
                        SwingUtilities.invokeLater(() -> {
                            TicTacToeGame ticTacToeGame = new TicTacToeGame();
                            ticTacToeGame.setClient(client);
                            ticTacToeGame.setRenderScene(
                                    this.getWindow().getManager().getScene("ticTacToe")
                            );
                            new Thread(ticTacToeGame).start();
                            this.getWindow().getManager().showScene("ticTacToe");

                            waitingLabel.setVisible(false);
                        });
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}


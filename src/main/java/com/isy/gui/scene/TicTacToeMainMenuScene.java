package com.isy.gui.scene;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.ticTacToe.*;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;

public class TicTacToeMainMenuScene extends MenuScene{
    static JComboBox dropdown1, dropdown2;
    private GameServer client;
    private String ownName;

    public TicTacToeMainMenuScene(Window window) {
        super("ticTacToeMainMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        String options[] = { "Player", "AI", "Remote" };

        dropdown1 = new JComboBox(options);
        dropdown2 = new JComboBox(options);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(dropdown1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(dropdown2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createHeader("TicTacToe"), gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(createDefaultButton("Start Game", this::startGame), gbc);

    }

    private void startGame(ActionEvent e){
        String player1Type = (String) dropdown1.getSelectedItem();
        String player2Type = (String) dropdown2.getSelectedItem();

        // Basis opties nu gedaan, zoals player tegen AI, later nog bijv. AI tegen AI afvangen.

        if (player1Type.equals("Player") && player2Type.equals("Player")) {
            startTicTacToeGameAgainstPlayer();
        } else if (player1Type.equals("Player") && player2Type.equals("AI") ||
                player1Type.equals("AI") && player2Type.equals("Player")) {
            startTicTacToeGameAgainstAi();
        } else if (player1Type.equals("AI") && player2Type.equals("AI")) {
            startTicTacToeGameAIAgainstAi();
        } else if (player1Type.equals("Player") || player2Type.equals("Remote")) {
            goToJoinGameServer();
        } else {
            // fallback
            JOptionPane.showMessageDialog(this.getScenePanel(),
                    "Ongeldige combinatie gekozen!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startTicTacToeGameAgainstPlayer() {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{new HumanPlayer("1", Tile.X, null), new HumanPlayer("2", Tile.O, null)});
        ticTacToeGame.setRenderScene(this.getWindow().getManager().getScene("ticTacToe"));
        new Thread(ticTacToeGame).start();
        this.getWindow().getManager().showScene("ticTacToe");
    }

    private void startTicTacToeGameAgainstAi() {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{new HumanPlayer("1", Tile.X, null), new AiPlayer("2", Tile.O, null)});
        ticTacToeGame.setRenderScene(this.getWindow().getManager().getScene("ticTacToe"));
        new Thread(ticTacToeGame).start();
        this.getWindow().getManager().showScene("ticTacToe");
    }

    private void startTicTacToeGameAIAgainstAi() {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{new AiPlayer("1", Tile.X, null), new AiPlayer("2", Tile.O, null)});
        ticTacToeGame.setRenderScene(this.getWindow().getManager().getScene("ticTacToe"));
        new Thread(ticTacToeGame).start();
        this.getWindow().getManager().showScene("ticTacToe");
    }

    private void goToJoinGameServer() {
        client = new GameServer("127.0.0.1", 7789);
        new Thread(client).start();

        new Thread(() -> {
            while (!client.isConnected()) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }

            ownName = "speler" + UUID.randomUUID().toString().substring(0, 8);
            client.sendCommand("login " + ownName);

            JoinGameServerMenuScene joinScene = (JoinGameServerMenuScene) this.getWindow()
                    .getManager().getScene("joinGameServerMenuScene");
            joinScene.setClient(client, ownName);

            // Clear alle listeners voor nieuwe login
            client.getListeners().clear();
            client.addListener(line -> {
                if (line.startsWith("SVR GAME MATCH")) {
                    boolean iStart = line.toLowerCase().contains(ownName);
                    SwingUtilities.invokeLater(() -> startRemoteTicTacToe(iStart));
                }
            });
        }).start();

        this.getWindow().getManager().showScene("joinGameServerMenuScene");
    }

    private void startRemoteTicTacToe(boolean iStart) {
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
    }
}

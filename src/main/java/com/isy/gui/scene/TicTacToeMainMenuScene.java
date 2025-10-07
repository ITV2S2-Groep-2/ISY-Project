package com.isy.gui.scene;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.PlayerType;
import com.isy.game.ticTacToe.*;
import com.isy.gui.Window;
import com.isy.gui.components.ComboBox;
import com.isy.gui.components.Header;
import com.isy.gui.components.TextField;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.UUID;

public class TicTacToeMainMenuScene extends MenuScene{
    static JComboBox dropdown1, dropdown2;
    static JTextField textField1, textField2;
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

        dropdown1 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .filter(val -> !val.equals(PlayerType.REMOTE))
                .map(val -> val.label)
                .toArray());
        dropdown2 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .map(val -> val.label)
                .toArray());

        textField1 = TextField.createTextField("Player 1");
        textField2 = TextField.createTextField("Player 2");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(Header.createHeader("TicTacToe"), gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(dropdown1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(dropdown2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(textField1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(textField2, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(UIButton.createButton("Start Game", this::startGame), gbc);
    }

    private void startGame(ActionEvent e){
        PlayerType player1Type = PlayerType.fromLabel((String) dropdown1.getSelectedItem());
        PlayerType player2Type = PlayerType.fromLabel((String) dropdown2.getSelectedItem());

        String player1Name = textField1.getText();
        String player2Name = textField2.getText();

        if (player1Type.equals(PlayerType.REMOTE) || player2Type.equals(PlayerType.REMOTE)) {
            //TODO: change player name management for remote funcionality
            goToJoinGameServer();
        } else {
            Player player1 = createPlayerByType(player1Type, player1Name, Tile.X);
            Player player2 = createPlayerByType(player2Type, player2Name, Tile.O);
            startLocalGame(player1, player2);
        }

    }

    private Player createPlayerByType(PlayerType type, String name, Tile tile) {
        return switch (type) {
            case PlayerType.HUMAN -> new HumanPlayer(name, tile, null);
            case PlayerType.AI -> new AiPlayer(name, tile, null);
            case PlayerType.REMOTE -> new RemotePlayer(name, tile, null);
            default -> null;
        };
    }

    private void startLocalGame(Player player1, Player player2) {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{player1, player2});
        ticTacToeGame.setRenderScene(this.getWindow().getManager().getScene("ticTacToe"));
        new Thread(ticTacToeGame).start();
        this.getWindow().getManager().showScene("ticTacToe");
    }

    private void goToJoinGameServer() {
        // client bestaat al, maak nieuwe aan en log nieuwe uit.
        if(client != null){
            client.shutdown();
        }
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

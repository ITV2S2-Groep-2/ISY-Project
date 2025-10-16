package com.isy.gui.scene;

import com.isy.await.Promise;
import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.PlayerType;
import com.isy.game.ticTacToe.*;
import com.isy.gui.GameSettings;
import com.isy.gui.Window;
import com.isy.gui.components.ComboBox;
import com.isy.gui.components.Header;
import com.isy.gui.components.TextField;
import com.isy.gui.components.UIButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.isy.await.Await.asyncAwait;
import static com.isy.await.Await.await;

public class TicTacToeMainMenuScene extends MenuScene{
    static JComboBox dropdown1, dropdown2;
    static JTextField textField1, textField2;
    private GameServer client;
    private GameSettings settings = GameSettings.get();
    private String ownName;
    private volatile boolean iStart;
    Player localPlayer;

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

        textField1 = TextField.createTextField("ttt.game.player1.text_field.placeholder");
        textField2 = TextField.createTextField("ttt.game.player2.text_field.placeholder");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(Header.createHeader("tic.tac.toe.header"), gbc);

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
        panel.add(UIButton.createButton("ttt.game.start_game.button", this::startGame), gbc);
    }

    private void startGame(ActionEvent e){
        PlayerType player1Type = PlayerType.fromLabel((String) dropdown1.getSelectedItem());
        PlayerType player2Type = PlayerType.fromLabel((String) dropdown2.getSelectedItem());

        String player1Name = textField1.getText();
        String player2Name = textField2.getText();

        if (player1Type.equals(PlayerType.HUMAN) && player2Type.equals(PlayerType.REMOTE)) {
            goToJoinGameServer(PlayerType.HUMAN, player1Name);
        }
        else if(player1Type.equals(PlayerType.AI) && player2Type.equals(PlayerType.REMOTE)) {
            goToJoinGameServer(PlayerType.AI, player1Name);
        } else {
            Player player1 = createPlayerByType(player1Type, player1Name, Tile.X);
            Player player2 = createPlayerByType(player2Type, player2Name, Tile.O);
            startLocalGame(player1, player2);
        }

    }

    public void setiStart(boolean input){
        this.iStart = input;
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

    private void goToJoinGameServer(PlayerType playerType, String playerName) {
        // client bestaat al, maak nieuwe aan en log nieuwe uit.
        if(client != null){
            client.shutdown();
        }
        client = new GameServer(settings.getHostName(), settings.getPortNumber());
        ownName = "speler" + UUID.randomUUID().toString().substring(0, 8);

        String accept = await(new Promise("^(OK|ERR).*").setCommand("login " + ownName));

        if (accept.toLowerCase().contains("err"))
            throw new RuntimeException("Uncaught exception: " + accept);

        JoinGameServerMenuScene joinScene = (JoinGameServerMenuScene) this.getWindow()
                .getManager().getScene("joinGameServerMenuScene");
        joinScene.setClient(client, ownName);

        Pattern playerToMovePattern = Pattern.compile("PLAYERTOMOVE:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

        asyncAwait(new Promise("^(SVR GAME MATCH|ERR).*"), (result) -> {
            Matcher matcher = playerToMovePattern.matcher(result);

            if (result.toLowerCase().contains("err")){
                throw new RuntimeException("AAAAAAAAAAAAAAAAAAAAAAAA");
            }

            if (matcher.find()){
                String playerToMove = matcher.group(1);
                iStart = Objects.equals(playerToMove, ownName);
            }

            SwingUtilities.invokeLater(() -> startRemoteTicTacToe(iStart, playerType));
        });


//        new Thread(client).start();
//
//        new Thread(() -> {
//            while (!client.isConnected()) {
//                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
//            }
//
//            if(playerName == null || playerName.equals("")){
//                ownName = "speler" + UUID.randomUUID().toString().substring(0, 8);
//            } else {
//                ownName = playerName;
//            }
//            client.sendCommand("login " + ownName);
//
//            JoinGameServerMenuScene joinScene = (JoinGameServerMenuScene) this.getWindow()
//                    .getManager().getScene("joinGameServerMenuScene");
//            joinScene.setClient(client, ownName);
//
//            // Clear alle listeners voor nieuwe login
//            client.getListeners().clear();
//            client.addListener(line -> {
//                    new Thread(() -> {
//                        if (line.startsWith("SVR GAME MATCH")) {
//                            // Geef de server een klein momentje om iStart eventueel op true te zetten wanneer YOURTURN gegeven is.
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                            SwingUtilities.invokeLater(() -> startRemoteTicTacToe(iStart, playerType));
//                        }
//                    }).start();
//
//                if (line.startsWith("SVR GAME YOURTURN")) {
//                    iStart = true;
//                }
//            });
//        }).start();

        this.getWindow().getManager().showScene("joinGameServerMenuScene");
    }

    private void startRemoteTicTacToe(boolean iStart, PlayerType playerType) {
        if(playerType == PlayerType.HUMAN){
            localPlayer = new HumanPlayer(ownName, Tile.X, client);
        } else if (playerType == PlayerType.AI) {
            localPlayer = new AiPlayer(ownName, Tile.X, client);
        }
        
        RemotePlayer remotePlayer = new RemotePlayer("Tegenstander", Tile.O, client);

        TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{
                iStart ? localPlayer : remotePlayer,
                iStart ? remotePlayer : localPlayer
        });
        ticTacToeGame.setClient(client);
        remotePlayer.setGame(ticTacToeGame);

        TicTacToeScene ttts = (TicTacToeScene) this.getWindow().getManager().getScene("ticTacToe");
        ticTacToeGame.setRenderScene(ttts);
        ttts.setPlayerName(ownName);

        new Thread(ticTacToeGame).start();
        this.getWindow().getManager().showScene("ticTacToe");
    }
}

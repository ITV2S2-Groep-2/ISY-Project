package com.isy.gui.scene;

import com.isy.await.Promise;
import com.isy.game.*;
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
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.isy.await.Await.asyncAwait;
import static com.isy.await.Await.await;

public class GameMenuScene extends MenuScene{
    static JComboBox dropdown1, dropdown2;
    static JTextField textField1, textField2;
    private GameServer client;
    private GameSettings settings = GameSettings.get();
    private String ownName;
    private volatile boolean iStart;
    Player localPlayer;

    public GameMenuScene(Window window) {
        super("gameMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        //TODO: make combobox translated
        dropdown1 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .filter(val -> !val.equals(PlayerType.REMOTE))
                .map(val -> val.label)
                .toArray());
        dropdown2 = ComboBox.createComboBox(Arrays.stream(PlayerType.values())
                .map(val -> val.label)
                .toArray());

        textField1 = TextField.createTextField("ttt.game.player.text_field.placeholder", Math.round(Math.random() * 1000));
        textField2 = TextField.createTextField("ttt.game.player.text_field.placeholder", Math.round(Math.random() * 1000));

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

        if (player2Type.equals(PlayerType.REMOTE)) {
            goToJoinGameServer(player1Type, player1Name);
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
        GameType gameType = this.getWindow().getManager().getCurrentGameType();
        Class<? extends Game> gameClass = GameType.getClass(gameType);
        Game game;
        try {
            game = gameClass.getDeclaredConstructor(Player[].class).newInstance((Object)new Player[]{player1, player2});
        } catch (Exception e) {
            throw new RuntimeException("invalid game constructor", e);
        }

        this.getWindow().getManager().addScene(new GameScene(this.getWindow()), true);

        game.setRenderScene(this.getWindow().getManager().getScene("game"));
        new Thread(game).start();
        this.getWindow().getManager().showScene("game");
    }

    private void goToJoinGameServer(PlayerType playerType, String playerName) {
        // client bestaat al, maak nieuwe aan en log nieuwe uit.
        if(client != null){
            client.shutdown();
        }
        client = new GameServer(settings.getHostName(), settings.getPortNumber());
        ownName = playerName;

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

                if (iStart)
                    await(new Promise("^(SVR GAME YOURTURN).*"));
            }

            SwingUtilities.invokeLater(() -> startRemoteGame(iStart, playerType));
        });


        this.getWindow().getManager().showScene("joinGameServerMenuScene");
    }

    private void startRemoteGame(boolean iStart, PlayerType playerType) {
        if(playerType == PlayerType.HUMAN){
            localPlayer = new HumanPlayer(ownName, Tile.X, client);
        } else if (playerType == PlayerType.AI) {
            localPlayer = new AiPlayer(ownName, Tile.X, client);
        }
        
        RemotePlayer remotePlayer = new RemotePlayer("Tegenstander", Tile.O, client);

        GameType gameType = this.getWindow().getManager().getCurrentGameType();
        Class<? extends Game> gameClass = GameType.getClass(gameType);
        Game game;
        try {
            game = gameClass.getDeclaredConstructor(Player[].class)
                    .newInstance((Object)new Player[]{iStart ? localPlayer : remotePlayer,
                    iStart ? remotePlayer : localPlayer});
        } catch (Exception e) {
            throw new RuntimeException("invalid game constructor", e);
        }

        game.setClient(client);
        remotePlayer.setGame(game);

        this.getWindow().getManager().addScene(new GameScene(this.getWindow()), true);
        GameScene gs = (GameScene) this.getWindow().getManager().getScene("game");
        game.setRenderScene(gs);
        gs.setPlayerName(ownName);

        new Thread(game).start();
        this.getWindow().getManager().showScene("game");
    }
}

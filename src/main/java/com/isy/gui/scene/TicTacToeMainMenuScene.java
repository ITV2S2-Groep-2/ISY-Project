package com.isy.gui.scene;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.ticTacToe.AiPlayer;
import com.isy.game.ticTacToe.HumanPlayer;
import com.isy.game.ticTacToe.TicTacToeGame;
import com.isy.game.ticTacToe.Tile;
import com.isy.gui.Style;
import com.isy.gui.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.UUID;

public class TicTacToeMainMenuScene extends MenuScene{
    private GameServer client;

    public TicTacToeMainMenuScene(Window window) {
        super("ticTacToeMainMenu", window);
    }

    @Override
    public void init() {
        JPanel panel = this.getScenePanel();

        panel.add(createHeader("TicTacToe"));
        panel.add(createDefaultButton("Player VS Player(Offline)", this::startTicTacToeGameAgainstPlayer), getConstraints());
        panel.add(createDefaultButton("Player VS AI(Offline)", this::startTicTacToeGameAgainstAi), getConstraints());
        panel.add(createDefaultButton("AI VS AI(Offline)"), getConstraints());
        panel.add(createDefaultButton("Join game server (temp)", this::goToJoinGameServer), getConstraints());

        panel.setBackground(Style.menuBackgroundColor);
    }

    private void startTicTacToeGameAgainstPlayer(ActionEvent actionEvent) {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{new HumanPlayer("1", Tile.X, null), new HumanPlayer("2", Tile.O, null)});
        ticTacToeGame.setRenderScene(this.getWindow().getManager().getScene("ticTacToe"));
        new Thread(ticTacToeGame).start();
        this.getWindow().getManager().showScene("ticTacToe");
    }

    private void startTicTacToeGameAgainstAi(ActionEvent actionEvent) {
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new Player[]{new HumanPlayer("1", Tile.X, null), new AiPlayer("2", Tile.O, null)});
        ticTacToeGame.setRenderScene(this.getWindow().getManager().getScene("ticTacToe"));
        new Thread(ticTacToeGame).start();
        this.getWindow().getManager().showScene("ticTacToe");
    }

    private void goToJoinGameServer(ActionEvent actionEvent){
        client = new GameServer("127.0.0.1", 7789);
        new Thread(client).start();

        new Thread(() -> {
            while (!client.isConnected()) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }

            String ownName = "speler" + UUID.randomUUID().toString().substring(0, 8);
            client.sendCommand("login " + ownName);

            // client + naam doorgeven aan JoinGameServerMenuScene
            JoinGameServerMenuScene joinScene = (JoinGameServerMenuScene) this.getWindow()
                    .getManager().getScene("joinGameServerMenuScene");
            joinScene.setClient(client, ownName);
        }).start();

        this.getWindow().getManager().showScene("joinGameServerMenuScene");
    }
}

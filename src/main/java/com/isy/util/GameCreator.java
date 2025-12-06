package com.isy.util;

import com.isy.Main;
import com.isy.game.Game;
import com.isy.game.GameType;
import com.isy.game.player.Player;
import com.isy.game.PlayerType;
import com.isy.game.ticTacToe.TicTacToeAiPlayer;
import com.isy.game.ticTacToe.TicTacToeHumanPlayer;
import com.isy.game.ticTacToe.TicTacToeRemotePlayer;
import com.isy.game.ticTacToe.TicTacToeTile;
import com.isy.gui.scene.GameScene;
import com.isy.gui.scene.JoinGameServerMenuScene;
import com.isy.server.Server;
import com.isy.server.await.Promise;

import java.lang.reflect.Constructor;

import static com.isy.gui.scene.GameMenuScene.joinError;
import static com.isy.server.ServerUtils.await;

public class GameCreator {
    private PlayerType player1, player2;
    private String player1Name, player2Name;
    private GameType gameType;
    private static GameCreator instance;

    public static GameCreator getCurrentInstance(){
        return instance;
    }

    public static GameCreator createNewInstance(GameType gameType){
        instance = new GameCreator(gameType);

        return getCurrentInstance();
    }

    private GameCreator(GameType gameType){
        this.gameType = gameType;
    }

    public void setPlayers(PlayerType player1, PlayerType player2, String player1Name, String player2Name){
        this.player1 = player1;
        this.player2 = player2;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    public PlayerType getPlayer1() {
        return player1;
    }

    public PlayerType getPlayer2() {
        return player2;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public void startLocalGame() {
        Class<? extends Player<?>> player1Class = GameType.getPlayerClass(getPlayer1(), gameType);
        Class<? extends Player<?>> player2Class = GameType.getPlayerClass(getPlayer2(), gameType);
        Player<?> player1 = null;
        Player<?> player2 = null;
        try {
            Constructor<?>[] player1Constructors = player1Class.getDeclaredConstructors();
            player1 = (Player<?>) player1Constructors[0].newInstance(player1Name, GameType.getPlayerTileValue(0, gameType), null);

            Constructor<?>[] player2Constructors = player2Class.getDeclaredConstructors();
            player2 = (Player<?>) player2Constructors[0].newInstance(player2Name, GameType.getPlayerTileValue(1, gameType), null);

        } catch (Exception e) {
            throw new RuntimeException("invalid player constructor", e);
        }


        GameType gameType = getGameType();
        Class<? extends Game<?>> gameClass = GameType.getClass(gameType);
        Game<?> game;
        try {
            game = gameClass.getDeclaredConstructor(Player[].class).newInstance((Object)new Player[]{player1, player2});
        } catch (Exception e) {
            throw new RuntimeException("invalid game constructor", e);
        }

        Main.window.getManager().addScene(new GameScene(Main.window), true);

        game.setRenderScene(Main.window.getManager().getScene("game"));
        new Thread(game).start();
        Main.window.getManager().showScene("game");
    }

    //TODO: make use of generic players
    public void startRemoteGame(boolean iStart) {
        Player localPlayer;
        
        if(player1 == PlayerType.HUMAN){
            localPlayer = new TicTacToeHumanPlayer(player1Name, iStart ? TicTacToeTile.X : TicTacToeTile.O, Server.getInstance());
        } else if (player1 == PlayerType.AI) {
            localPlayer = new TicTacToeAiPlayer(player1Name, iStart ? TicTacToeTile.X : TicTacToeTile.O, Server.getInstance());
        }else localPlayer = null;

        TicTacToeRemotePlayer remotePlayer = new TicTacToeRemotePlayer(player2Name, iStart ? TicTacToeTile.O : TicTacToeTile.X, Server.getInstance());

        Class<? extends Game> gameClass = GameType.getClass(gameType);
        Game game;
        try {
            game = gameClass.getDeclaredConstructor(Player[].class)
                    .newInstance((Object)new Player[]{iStart ? localPlayer : remotePlayer,
                            iStart ? remotePlayer : localPlayer});
        } catch (Exception e) {
            throw new RuntimeException("invalid game constructor", e);
        }

        remotePlayer.setGame(game);

        Main.window.getManager().addScene(new GameScene(Main.window), true);
        GameScene gs = (GameScene) Main.window.getManager().getScene("game");
        game.setRenderScene(gs);
        gs.setPlayerNames(player1Name, player2Name, iStart);

        new Thread(game).start();
        Main.window.getManager().showScene("game");
    }

    public void goToJoinGameServer() {
        // client bestaat al, maak nieuwe aan en log oude uit.
        Server.resetServer(GameSettings.get().getHostName(), GameSettings.get().getPortNumber());

        System.out.println(this.player1Name);

        String accept = await(new Promise("^(OK|ERR).*").setCommand("login " + this.getPlayer1Name()));

        if (accept.toLowerCase().contains("err")){
            joinError("error.used_name");
            return;
        }

        JoinGameServerMenuScene joinScene = (JoinGameServerMenuScene) Main.window
                .getManager().getScene("joinGameServerMenuScene");
        joinScene.setGameCreator(this);

        Main.window.getManager().showScene("joinGameServerMenuScene");
    }

    private Player createPlayerByType(PlayerType type, String name, TicTacToeTile tile) {
        return switch (type) {
            case PlayerType.HUMAN -> new TicTacToeHumanPlayer(name, tile, null);
            case PlayerType.AI -> new TicTacToeAiPlayer(name, tile, null);
            case PlayerType.REMOTE -> new TicTacToeRemotePlayer(name, tile, null);
        };
    }
}

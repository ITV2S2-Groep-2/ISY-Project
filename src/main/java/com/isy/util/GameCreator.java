package com.isy.util;

import com.isy.Main;
import com.isy.game.Game;
import com.isy.game.GameType;
import com.isy.game.Player;
import com.isy.game.PlayerType;
import com.isy.game.ticTacToe.AiPlayer;
import com.isy.game.ticTacToe.HumanPlayer;
import com.isy.game.ticTacToe.RemotePlayer;
import com.isy.game.ticTacToe.Tile;
import com.isy.gui.GameSettings;
import com.isy.gui.scene.GameScene;
import com.isy.gui.scene.JoinGameServerMenuScene;
import com.isy.server.Server;
import com.isy.server.await.Promise;

import static com.isy.gui.scene.GameMenuScene.joinError;
import static com.isy.server.await.Await.await;

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

    public void startLocalGame() {
        Player player1 = createPlayerByType(getPlayer1(), player1Name, Tile.X);
        Player player2 = createPlayerByType(getPlayer2(), player2Name, Tile.O);

        GameType gameType = getGameType();
        Class<? extends Game> gameClass = GameType.getClass(gameType);
        Game game;
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

    public void startRemoteGame(boolean iStart) {
        Player localPlayer;

        if(player1 == PlayerType.HUMAN){
            localPlayer = new HumanPlayer(player1Name, iStart ? Tile.X : Tile.O, Server.getInstance());
        } else if (player1 == PlayerType.AI) {
            localPlayer = new AiPlayer(player1Name, iStart ? Tile.X : Tile.O, Server.getInstance());
        }else localPlayer = null;

        RemotePlayer remotePlayer = new RemotePlayer(player2Name, iStart ? Tile.O : Tile.X, Server.getInstance());

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
        gs.setPlayerName(player1Name, iStart);

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

    private Player createPlayerByType(PlayerType type, String name, Tile tile) {
        return switch (type) {
            case PlayerType.HUMAN -> new HumanPlayer(name, tile, null);
            case PlayerType.AI -> new AiPlayer(name, tile, null);
            case PlayerType.REMOTE -> new RemotePlayer(name, tile, null);
        };
    }
}

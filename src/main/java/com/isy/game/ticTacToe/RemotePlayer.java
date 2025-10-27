package com.isy.game.ticTacToe;

import com.isy.server.await.Promise;
import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.server.Server;
import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.isy.server.await.Await.await;

public class RemotePlayer extends Player{
    private Game game;

    public RemotePlayer(String name, Tile symbol, Server client){
        super(name, symbol, client);


    }

    public void setGame(Game game){
        this.game = game;
    }

    @Override
    public int[] getMove(Board board) {
        if(client == null){
            throw new IllegalStateException("RemotePlayer needs a server client");
        }

        String serverMove = await(new Promise("^(?:ERR|SVR GAME (?:WIN|MOVE|LOSS)).*"));

        System.out.println("REMOTE MOVE: " + serverMove);

        if(serverMove.toUpperCase().contains("ERR")){
            throw new RuntimeException(serverMove);
        }else if(serverMove.toUpperCase().contains("MOVE")){
            int moveIndex = serverMove.indexOf("MOVE:");
            if (moveIndex != -1) {
                int startQuote = serverMove.indexOf("\"", moveIndex);
                int endQuote = serverMove.indexOf("\"", startQuote + 1);
                if (startQuote != -1 && endQuote != -1) {
                    String move = serverMove.substring(startQuote + 1, endQuote);
                    int moveInt = Integer.parseInt(move);
                    return formatServerMove(moveInt);
                }
            }
        }else if(serverMove.toUpperCase().contains("WIN")){
            this.game.setState(GameState.WON);
            PlayerEventManager.get().stop();
        }else if(serverMove.toUpperCase().contains("LOSS")){
            this.game.setState(GameState.LOST);
            PlayerEventManager.get().stop();
        }

        return null;
    }
}

package com.isy.game.player;

import com.isy.game.Game;
import com.isy.game.ITile;
import com.isy.server.Server;
import com.isy.server.await.Promise;

import static com.isy.server.ServerUtils.await;

public abstract class RemotePlayer<T extends Enum<T> & ITile> extends Player<T> {
    private Game<T> game;

    public RemotePlayer(String name, T symbol, Server client) {
        super(name, symbol, client);
    }

    public void setGame(Game<T> game){
        this.game = game;
    }

    @Override
    public int[] getMove(Game<T> game) {
        if(client == null){
            throw new IllegalStateException("RemotePlayer needs a server client");
        }

        String serverMove = await(new Promise("^(?:ERR|SVR GAME MOVE).*"));

        System.out.println("REMOTE PLAYER MOVE: " + serverMove);

        if(serverMove.toUpperCase().contains("ERR")){
            return null;
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
        }

        return null;
    }
}

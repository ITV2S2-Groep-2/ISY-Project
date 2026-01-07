package com.isy.game.othello;

import com.isy.game.Game;
import com.isy.game.player.RemotePlayer;
import com.isy.server.Server;
import com.isy.server.await.Promise;

import static com.isy.server.ServerUtils.await;

public class OthelloRemotePlayer extends RemotePlayer<OthelloTile> {

    public OthelloRemotePlayer(String name, OthelloTile symbol, Server client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Game<OthelloTile> game) {
        if(client == null){
            throw new IllegalStateException("RemotePlayer needs a server client");
        }

        String serverMove = await(new Promise("^(?:ERR|SVR GAME MOVE).*"));

        System.out.println("REMOTE MOVE: " + serverMove);

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

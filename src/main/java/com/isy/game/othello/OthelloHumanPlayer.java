package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.player.Player;
import com.isy.server.Server;
import com.isy.util.PlayerEventManager;

import java.util.Arrays;

import static com.isy.server.ServerUtils.await;

public class OthelloHumanPlayer extends Player<OthelloTile> {

    public OthelloHumanPlayer(String name, OthelloTile symbol, Server client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Board<OthelloTile> board) {
        int[] move = await(PlayerEventManager.get());

        //TODO: remove remote to probably game class
        if(move != null){
            if(board.getTile(move[0], move[1]) == OthelloTile.EMPTY){
                System.out.println(Arrays.toString(move) + "SEND SERVER DATA NOW!!!");

                sendServerData(move);
            }
        }

        return move;
    }
}

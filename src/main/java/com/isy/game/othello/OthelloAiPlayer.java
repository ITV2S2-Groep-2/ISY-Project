package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.player.Player;
import com.isy.server.Server;

public class OthelloAiPlayer extends Player<OthelloTile> {

    public OthelloAiPlayer(String name, OthelloTile symbol, Server client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Board<OthelloTile> board) {
        return new int[0];
    }

}

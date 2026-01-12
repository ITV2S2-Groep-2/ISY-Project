package com.isy.game.othello;

import com.isy.game.player.HumanPlayer;
import com.isy.server.Server;


public class OthelloHumanPlayer extends HumanPlayer<OthelloTile> {

    public OthelloHumanPlayer(String name, OthelloTile symbol, Server client){
        super(name, symbol, client);
    }

}

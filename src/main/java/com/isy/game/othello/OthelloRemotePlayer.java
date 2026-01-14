package com.isy.game.othello;

import com.isy.game.player.RemotePlayer;
import com.isy.server.Server;

public class OthelloRemotePlayer extends RemotePlayer<OthelloTile> {

    public OthelloRemotePlayer(String name, OthelloTile symbol, Server client){
        super(name, symbol, client);
    }
}

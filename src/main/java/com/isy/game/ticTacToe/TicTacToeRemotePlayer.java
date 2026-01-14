package com.isy.game.ticTacToe;

import com.isy.game.player.RemotePlayer;
import com.isy.server.Server;

public class TicTacToeRemotePlayer extends RemotePlayer<TicTacToeTile> {

    public TicTacToeRemotePlayer(String name, TicTacToeTile symbol, Server client){
        super(name, symbol, client);
    }

}
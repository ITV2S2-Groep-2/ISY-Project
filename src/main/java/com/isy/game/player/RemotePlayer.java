package com.isy.game.player;

import com.isy.game.Game;
import com.isy.game.ITile;
import com.isy.server.Server;

public abstract class RemotePlayer<T extends Enum<T> & ITile> extends Player<T> {
    private Game<T> game;

    public RemotePlayer(String name, T symbol, Server client) {
        super(name, symbol, client);
    }

    public void setGame(Game<T> game){
        this.game = game;
    }
}

package com.isy.game.player;

import com.isy.game.Game;
import com.isy.game.ITile;
import com.isy.server.Server;
import com.isy.util.PlayerEventManager;

import static com.isy.server.ServerUtils.await;

public abstract class HumanPlayer<T extends Enum<T> & ITile> extends Player<T> {

    public HumanPlayer(String name, T symbol, Server client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Game<T> game) {
        int[] move = await(PlayerEventManager.get());

        return move;
    }

}

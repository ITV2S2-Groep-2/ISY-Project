package com.isy.game.ticTacToe;

import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import static com.isy.await.Await.await;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Tile symbol){
        super(name, symbol);
    }

    @Override
    public int[] getMove(Board board) {
        return await(PlayerEventManager.get());
    }
}

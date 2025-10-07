package com.isy.game.ticTacToe;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import static com.isy.await.Await.await;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Tile symbol, GameServer client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Board board) {
        int[] move = await(PlayerEventManager.get());

        if(this.client != null && board.getTile(move[0], move[1]) == Tile.EMPTY){
            sendServerData(move);
        }
        return move;
    }
}

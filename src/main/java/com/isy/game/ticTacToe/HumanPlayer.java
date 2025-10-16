package com.isy.game.ticTacToe;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import java.util.Arrays;

import static com.isy.await.Await.await;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Tile symbol, GameServer client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Board board) {
        int[] move = await(PlayerEventManager.get());

        if(move != null){
            if(board.getTile(move[0], move[1]) == Tile.EMPTY){
                System.out.println(Arrays.toString(move) + "SEND SERVER DATA NOW!!!");

                sendServerData(move);
            }
        }

        return move;
    }
}

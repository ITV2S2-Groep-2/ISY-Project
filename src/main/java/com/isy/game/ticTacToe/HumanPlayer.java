package com.isy.game.ticTacToe;

import com.isy.game.Board;
import com.isy.server.Server;
import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import java.util.Arrays;

import static com.isy.server.await.Await.await;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, Tile symbol, Server client){
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

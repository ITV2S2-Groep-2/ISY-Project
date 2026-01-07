package com.isy.game.ticTacToe;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.server.Server;
import com.isy.util.PlayerEventManager;

import java.util.Arrays;

import static com.isy.server.ServerUtils.await;

public class TicTacToeHumanPlayer extends Player<TicTacToeTile> {

    public TicTacToeHumanPlayer(String name, TicTacToeTile symbol, Server client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Game<TicTacToeTile> game) {
        Board<TicTacToeTile> board = game.getBoard();

        int[] move = await(PlayerEventManager.get());

        if(move != null){
            if(board.getTile(move[0], move[1]) == TicTacToeTile.EMPTY){
                System.out.println(Arrays.toString(move) + "SEND SERVER DATA NOW!!!");

                sendServerData(move);
            }
        }

        return move;
    }
}

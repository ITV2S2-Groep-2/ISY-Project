package com.isy.game.ticTacToe;

import com.isy.game.player.HumanPlayer;
import com.isy.server.Server;


public class TicTacToeHumanPlayer extends HumanPlayer<TicTacToeTile> {

    public TicTacToeHumanPlayer(String name, TicTacToeTile symbol, Server client){
        super(name, symbol, client);
    }

}

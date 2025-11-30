package com.isy.game.ticTacToe;

import com.isy.game.player.Player;
import com.isy.game.Game;

public class TicTacToeGame extends Game {

    public TicTacToeGame(Player[] players) {
        super(new TicTacToeBoard(), players);
    }

}

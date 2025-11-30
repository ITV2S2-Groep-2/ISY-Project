package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.player.Player;

public class OthelloBoard extends Board {

    public OthelloBoard() {
        super(8, 8);
        //TODO: set default tiles -> how does server handle this?
    }

    public boolean checkWin(int x, int y, Player p) {
        return false;
    }
}

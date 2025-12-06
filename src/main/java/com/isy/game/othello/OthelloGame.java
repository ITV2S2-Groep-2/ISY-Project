package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;

public class OthelloGame extends Game<OthelloTile> {

    public OthelloGame(Player<OthelloTile>[] players) {
        super(new Board<>(8, 8, OthelloTile.EMPTY, OthelloTile::createBoard), players);
        //TODO: set default board values
    }


    @Override
    public boolean checkWin(int x, int y, Player<OthelloTile> p) {
        return false;
    }
}

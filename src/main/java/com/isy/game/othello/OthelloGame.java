package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;

public class OthelloGame extends Game<OthelloTile> {

    public OthelloGame(Player<OthelloTile>[] players) {
        super(new Board<>(8, 8, OthelloTile.EMPTY, OthelloTile::createBoard), players);
        OthelloTile[][] tiles = this.getBoard().getTiles();
        tiles[3][3] = OthelloTile.PLAYER_2;
        tiles[4][4] = OthelloTile.PLAYER_2;
        tiles[3][4] = OthelloTile.PLAYER_1;
        tiles[4][3] = OthelloTile.PLAYER_1;
    }


    @Override
    public boolean checkWin(int x, int y, Player<OthelloTile> p) {
        return false;
    }
}

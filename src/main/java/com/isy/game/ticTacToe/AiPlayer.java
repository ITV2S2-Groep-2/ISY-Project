package com.isy.game.ticTacToe;

import com.isy.game.Player;

public class AiPlayer extends Player {
    public AiPlayer(String name, Tile symbol){
        super(name, symbol);
    }

    // getMove() etc.

    @Override
    public int[] getMove(Board board) {
        //TEMP
        Tile[][] tiles = board.getTiles();
        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                if (tiles[r][c] == Tile.EMPTY) {
                    return new int[] {r,c};
                }
            }
        }
        return new int[]{1,1};
    }
}

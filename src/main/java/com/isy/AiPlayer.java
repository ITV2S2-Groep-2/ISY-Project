package com.isy;

public class AiPlayer extends Player {
    public AiPlayer(String name, Tile symbol){
        super(name, symbol);
    }

    // getMove() etc.

    @Override
    public int[] getMove(Board board) {
        return new int[0];
    }
}

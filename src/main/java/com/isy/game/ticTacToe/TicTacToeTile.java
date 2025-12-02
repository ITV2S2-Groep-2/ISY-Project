package com.isy.game.ticTacToe;

public enum TicTacToeTile {
    EMPTY, X, O;

    @Override
    public String toString() {
        if (this == EMPTY)
            return " ";

        return this.name();
    }

    public static TicTacToeTile[][] createBoard(int width, int height){
        return new TicTacToeTile[width][height];
    }
}

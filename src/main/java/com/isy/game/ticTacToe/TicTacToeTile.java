package com.isy.game.ticTacToe;

import com.isy.game.ITile;

import javax.swing.*;

public enum TicTacToeTile implements ITile {
    EMPTY, X, O;

    @Override
    public String toString() {
        if (this == EMPTY)
            return " ";

        return this.name();
    }


    @Override
    public void updateOnBoard(JButton jButton) {
        jButton.setText(this.toString());
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int index() {
        return 0;
    }

    public static TicTacToeTile[] createBoard(int width, int height){
        return new TicTacToeTile[width * height];
    }
}

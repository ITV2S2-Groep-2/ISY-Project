package com.isy.game.othello;

import com.isy.game.ITile;

import javax.swing.*;

//TODO: ACTUALLY CREATE ICONS AND MAKE SURE THINGS ARE RENDERING CORRECTLY
public enum OthelloTile implements ITile {
    EMPTY("icon.png"), POSSIBLE_MOVE("icon.png"),
    PLAYER_1("icon.png"), PLAYER_2("icon.png");

    private final Icon icon;

    OthelloTile(String imagePath){
        this.icon = new ImageIcon(imagePath);
    }

    //TODO: TEMP
    @Override
    public String toString() {
        if (this == EMPTY)
            return " ";

        return this.name();
    }

    @Override
    public void updateOnBoard(JButton jButton) {
//        jButton.setIcon(this.icon);
        jButton.setText(this.toString());
    }

    public static OthelloTile[][] createBoard(int width, int height){
        return new OthelloTile[width][height];
    }
}

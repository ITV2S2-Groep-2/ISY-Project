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

    @Override
    public void updateOnBoard(JButton jButton) {
        jButton.setIcon(this.icon);
    }
}

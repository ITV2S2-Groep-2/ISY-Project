package com.isy.game;

import javax.swing.*;

/**
 * This interface is used for all Tile types, this makes it so that the game scene can be generic
 */
public interface ITile {
    void updateOnBoard(JButton jButton);
}

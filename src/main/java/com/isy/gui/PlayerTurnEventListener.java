package com.isy.gui;

import com.isy.game.ticTacToe.TicTacToeGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerTurnEventListener implements ActionListener {
    private final TicTacToeGame ticTacToeGame;
    private final int x;
    private final int y;

    public PlayerTurnEventListener(TicTacToeGame ticTacToeGame, int x, int y) {
        this.ticTacToeGame = ticTacToeGame;
        this.x = x;
        this.y = y;
    }

    public void actionPerformed(ActionEvent e) {
        PlayerEventManager.get().newClick(this.x, this.y);
    }
}

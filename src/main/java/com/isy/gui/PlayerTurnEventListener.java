package com.isy.gui;

import com.isy.game.Game;
import com.isy.game.GameServer;
import com.isy.game.ticTacToe.TicTacToeGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerTurnEventListener implements ActionListener {
    private final Game game;
    private final int x;
    private final int y;
    private GameServer client;

    public PlayerTurnEventListener(Game game, int x, int y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    public void actionPerformed(ActionEvent e) {
        PlayerEventManager.get().newClick(this.x, this.y);
    }
}

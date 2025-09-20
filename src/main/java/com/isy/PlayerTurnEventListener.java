package com.isy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class PlayerTurnEventListener implements ActionListener {
    private final Game game;
    private final int x;
    private final int y;

    public PlayerTurnEventListener(Game game, int x, int y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    public void actionPerformed(ActionEvent e) {
        this.game.handleGameTurn(Optional.of(new int[]{this.x, this.y}));
    }
}

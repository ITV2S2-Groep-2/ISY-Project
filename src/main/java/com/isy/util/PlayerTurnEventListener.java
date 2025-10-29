package com.isy.util;

import com.isy.game.Game;
import com.isy.server.Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerTurnEventListener implements ActionListener {
    private final Game game;
    private final int x;
    private final int y;
    private Server client;

    public PlayerTurnEventListener(Game game, int x, int y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    public void actionPerformed(ActionEvent e) {
        PlayerEventManager.get().newClick(this.x, this.y);
    }
}

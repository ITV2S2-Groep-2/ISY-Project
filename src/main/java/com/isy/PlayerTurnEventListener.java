package com.isy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerTurnEventListener implements ActionListener {
    private int x;
    private int y;

    public PlayerTurnEventListener(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void actionPerformed(ActionEvent e) {
//        call player turn with coords
        System.out.println(this.x + " " + this.y);
    }
}

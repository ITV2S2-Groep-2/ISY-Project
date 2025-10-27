package com.isy;

import com.isy.game.Game;
import com.isy.gui.Window;

public class Main {
    public static Window window;
    public static Game runningGame = null;

    public static void main(String[] args) {
        window = new Window();
    }
}
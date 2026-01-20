package com.isy;

import com.isy.game.othello.OthelloDebugger;
import com.isy.gui.Window;

public class Main {
    public static Window window;
    public static OthelloDebugger debugger;

    public static void main(String[] args) {
        debugger = new OthelloDebugger();

        new Thread(debugger).start();
        window = new Window();
    }
}
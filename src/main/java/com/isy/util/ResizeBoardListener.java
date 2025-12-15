package com.isy.util;

import com.isy.gui.scene.GameScene;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ResizeBoardListener implements ComponentListener {
    GameScene gs;

    public ResizeBoardListener(GameScene gs) {
        this.gs = gs;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        gs.reloadBoardValues();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}

package com.isy.gui;

import com.isy.await.IWaitable;

import static com.isy.await.Await.waitTime;

public class PlayerEventManager implements IWaitable<int[]> {
    private static PlayerEventManager instance;
    private long lastClick;
    private int[] clickedButton;

    private PlayerEventManager(){
        lastClick = 0;
        clickedButton = new int[2];
    }

    public static PlayerEventManager get(){
        if (instance == null)
            instance = new PlayerEventManager();

        return instance;
    }

    public void newClick(int x, int y){
        this.lastClick = System.currentTimeMillis();
        this.clickedButton[0] = x;
        this.clickedButton[1] = y;
    }

    @Override
    public boolean hasData() {
        return (System.currentTimeMillis() - this.lastClick) < waitTime + 10;
    }

    @Override
    public int[] getData() {
        this.lastClick = 0;
        return this.clickedButton;
    }
}

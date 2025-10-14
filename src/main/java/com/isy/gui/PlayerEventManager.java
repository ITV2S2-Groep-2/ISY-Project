package com.isy.gui;

import com.isy.await.IWaitable;

import static com.isy.await.Await.waitTime;

public class PlayerEventManager implements IWaitable<int[]> {
    private static PlayerEventManager instance;
    private long lastClick;
    private int[] clickedButton;
    private boolean stop = false;

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

    public void stop(){
        this.stop = true;
    }

    @Override
    public boolean hasData() {
        return ((System.currentTimeMillis() - this.lastClick) < waitTime + 10) || this.stop;
    }

    @Override
    public int[] getData() {
        if(this.stop){
            this.clickedButton = null;
        }
        this.stop = false;
        lastClick = 0;
        return this.clickedButton;
    }
}

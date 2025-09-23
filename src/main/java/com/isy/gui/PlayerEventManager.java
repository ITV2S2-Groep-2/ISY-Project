package com.isy.gui;

import com.isy.await.IWaitable;

public class PlayerEventManager implements IWaitable<int[]> {
    private static PlayerEventManager instance;
    private long lastClick;
    private int[] clickedButton;
}

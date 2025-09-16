package com.isy;

public class Player {
    private final String name;
    private final Tile symbol;

    public Player(String name, Tile symbol){
        this.name = name;
        this.symbol = symbol;
    }

    public Tile getSymbol(){
        return symbol;
    }
}

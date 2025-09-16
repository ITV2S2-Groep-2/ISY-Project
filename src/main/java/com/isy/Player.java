package com.isy;
import java.util.Scanner;

public abstract class Player {
    private final String name;
    private final Tile symbol;

    public Player(String name, Tile symbol){
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public Tile getSymbol(){
        return symbol;
    }

    public abstract int[] getMove(Board board);
}

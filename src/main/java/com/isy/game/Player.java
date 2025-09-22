package com.isy.game;
import com.isy.game.ticTacToe.Board;
import com.isy.game.ticTacToe.Tile;

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

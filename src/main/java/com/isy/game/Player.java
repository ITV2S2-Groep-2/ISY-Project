package com.isy.game;
import com.isy.game.ticTacToe.Board;
import com.isy.game.ticTacToe.Tile;

public abstract class Player {
    private final String name;
    private final Tile symbol;
    protected GameServer client;

    public Player(String name, Tile symbol, GameServer client){
        this.name = name;
        this.symbol = symbol;
        this.client = client;
    }

    public void setPlayerClient(GameServer client){
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public Tile getSymbol(){
        return symbol;
    }

    public abstract int[] getMove(Board board);

    public void sendServerData(int[] move){
        if(client != null){
            System.out.println("Stuurt command move nu: (debug)" + move[0] + "en " + move[1]);
            client.sendCommand("move " + move[0] + move[1]);
        }
    }
}

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
            System.out.println("(DEBUG)Stuurt nu command: move " + formatMove(move));
            client.sendCommand("move " + formatMove(move));
        }
    }

    /**
     * Formats move for game server's regular input
     * @param move int array with the x and y coords of the clicked tile.
     * @return a single int which was converted by calculating x + (y * 3)
     */
    public int formatMove(int[] move){
        // TODO: columns parameter meegeven (8 voor othello) i.p.v. hard coded 3.
        return move[0] + (move[1] * 3);
    }
}

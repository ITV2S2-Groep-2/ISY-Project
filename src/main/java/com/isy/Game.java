package com.isy;

import java.util.Scanner;

public class Game {
    private Board board;
    private Player[] players;
    private Player activeTurn;

    public Game() {
        this.board = new Board();
        this.players = new Player[]{new HumanPlayer("1", Tile.X), new AiPlayer("2", Tile.O)};
        this.activeTurn = players[0];
    }

    public Board getBoard() {
        return board;
    }

    public Player getActiveTurn()  { return this.activeTurn; }

}

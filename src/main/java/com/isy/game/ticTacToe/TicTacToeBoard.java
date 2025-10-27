package com.isy.game.ticTacToe;

import com.isy.game.Board;
import com.isy.game.Tile;
import com.isy.game.player.Player;

public class TicTacToeBoard extends Board {

    public TicTacToeBoard() {
        super(3,3);
    }

    //TODO: discuss to move check win function to game class, might remove the entire need for this extended class
    public boolean checkWin(int x, int y, Player p) {
        Tile symbol = p.getSymbol();

        boolean rowWin = true;
        for (int i = 0; i < 3; i++) {
            if (this.tiles[i][y] != symbol) {
                rowWin = false;
                break;
            }
        }
        if (rowWin) return true;


        boolean colWin = true;
        for (int i = 0; i < 3; i++) {
            if (this.tiles[x][i] != symbol) {
                colWin = false;
                break;
            }
        }
        if (colWin) return true;


        if (x == y) {
            boolean diagWin = true;
            for (int i = 0; i < 3; i++) {
                if (this.tiles[i][i] != symbol) {
                    diagWin = false;
                    break;
                }
            }
            if (diagWin) return true;
        }


        if (x + y == 2) {
            boolean antiDiagWin = true;
            for (int i = 0; i < 3; i++) {
                if (this.tiles[i][2 - i] != symbol) {
                    antiDiagWin = false;
                    break;
                }
            }
            return antiDiagWin;
        }

        return false;
    }

}

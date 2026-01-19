package com.isy.game.ticTacToe;

import com.isy.game.Board;
import com.isy.game.player.Player;
import com.isy.game.Game;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeGame extends Game<TicTacToeTile> {

    public TicTacToeGame(Player<TicTacToeTile>[] players) {
        super(new Board<>(3, 3, TicTacToeTile.EMPTY, TicTacToeTile::createBoard), players);
    }


    @Override
    public List<int[]> getAvailableMoves(Board<TicTacToeTile> board, TicTacToeTile playerSymbol, TicTacToeTile opponentSymbol) {
        List<int[]> am = new ArrayList<>();

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (board.getTile(x, y) == TicTacToeTile.EMPTY) {
                    am.add(new int[]{x, y});
                }
            }
        }
        return am;
    }

    @Override
    public boolean checkWin(int x, int y, Player<TicTacToeTile> p) {
        TicTacToeTile symbol = p.getSymbol();

        boolean rowWin = true;
        for (int i = 0; i < 3; i++) {
            if (this.getBoard().getTile(i, y) != symbol) {
                rowWin = false;
                break;
            }
        }
        if (rowWin) return true;


        boolean colWin = true;
        for (int i = 0; i < 3; i++) {
            if (this.board.getTile(x, i) != symbol) {
                colWin = false;
                break;
            }
        }
        if (colWin) return true;


        if (x == y) {
            boolean diagWin = true;
            for (int i = 0; i < 3; i++) {
                if (this.board.getTile(i, i) != symbol) {
                    diagWin = false;
                    break;
                }
            }
            if (diagWin) return true;
        }


        if (x + y == 2) {
            boolean antiDiagWin = true;
            for (int i = 0; i < 3; i++) {
                if (this.board.getTile(i, 2 - i) != symbol) {
                    antiDiagWin = false;
                    break;
                }
            }
            return antiDiagWin;
        }

        return false;
    }
}

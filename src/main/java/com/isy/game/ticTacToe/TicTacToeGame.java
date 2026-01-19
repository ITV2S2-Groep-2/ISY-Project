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
    public GameState checkWin(Player<TicTacToeTile> p, Player<TicTacToeTile> o) {
        if (this.hasVerticalWin(p) || this.hasHorizontalWin(p) || this.hasDiagonalWin(p)) {
            this.state = GameState.WON;
            return GameState.WON;
        }

        if (this.hasVerticalWin(o) || this.hasHorizontalWin(o) || this.hasDiagonalWin(o)) {
            this.state = GameState.LOST;
            return GameState.LOST;
        }

        if (this.board.isBoardFull()) {
            this.state = GameState.DRAW;
            return GameState.DRAW;
        }

        return GameState.ONGOING;
    }

    private boolean hasHorizontalWin(Player<TicTacToeTile> p) {
        boolean horizontalWin = false;
        for (int row = 0; row < this.getBoard().getWidth(); row++) {
            horizontalWin = true;
            for (int col = 0; col < this.getBoard().getHeight(); col++) {
                if (this.getBoard().getTile(row, col) != p.getSymbol()) {
                    horizontalWin = false;
                    break;
                }
            }
            if (horizontalWin) return true;
        }
        return false;
    }

    private boolean hasVerticalWin(Player<TicTacToeTile> p) {
        boolean verticalWin = false;
        for (int col = 0; col < this.getBoard().getHeight(); col++) {
            verticalWin = true;
            for (int row = 0; row < this.getBoard().getWidth(); row++) {
                if (this.getBoard().getTile(row, col) != p.getSymbol()) {
                    verticalWin = false;
                    break;
                }
            }
            if (verticalWin) return true;
        }
        return false;
    }

    private boolean hasDiagonalWin(Player<TicTacToeTile> p) {
        TicTacToeTile symbol = p.getSymbol();
        boolean diagonalWin = false;
        if (this.getBoard().getTile(0,0) == symbol
                && this.getBoard().getTile(1,1) == symbol
                && this.getBoard().getTile(2,2) == symbol
        ) {
            diagonalWin = true;
        }

        if (this.getBoard().getTile(2,0) == symbol
                && this.getBoard().getTile(1,1) == symbol
                && this.getBoard().getTile(0,2) == symbol
        ) {
            diagonalWin = true;
        }

        return diagonalWin;
    }
}

package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.game.ticTacToe.GameState;

import java.util.Arrays;
import java.util.List;

public class OthelloGame extends Game<OthelloTile> {

    public OthelloGame(Player<OthelloTile>[] players) {
        super(new Board<>(8, 8, OthelloTile.EMPTY, OthelloTile::createBoard), players);
        OthelloTile[][] tiles = this.getBoard().getTiles();
        tiles[3][3] = OthelloTile.PLAYER_2;
        tiles[4][4] = OthelloTile.PLAYER_2;
        tiles[3][4] = OthelloTile.PLAYER_1;
        tiles[4][3] = OthelloTile.PLAYER_1;
    }

    @Override
    public boolean handleSingleTurn() {
        int[] move = null;

        List<int[]> availableMoves = OthelloUtils.getAvailableMoves2(getBoard(), this.activeTurnPlayer.getSymbol(), (OthelloTile) this.getOpponent().getSymbol());
        for (int[] availableMove : availableMoves) {
            System.out.println("available move: " + availableMove[0] + ", " + availableMove[1]);
        }

        this.renderBoard();

        move = this.activeTurnPlayer.getMove(this.getBoard());
        if (move == null) {
            return false;
        }
        System.out.println(Arrays.toString(move));
        System.out.println(this.activeTurnPlayer.getSymbol());

        int[] finalMove = move;
        boolean isAvailable = availableMoves.stream().anyMatch(val -> {
            return val[0] == finalMove[0] && val[1] == finalMove[1];
        });

        boolean correctMove = isAvailable ? this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol()) : false;

        if (correctMove) {
            if (this.checkWin(move[0], move[1], this.activeTurnPlayer)){
                this.state = GameState.WON;
                return false;
            } else if (this.board.isBoardFull()) {
                return true;
            }

            this.giveTurnOver();
        }

        return false;
    }


    @Override
    public boolean checkWin(int x, int y, Player<OthelloTile> p) {
        return false;
    }
}

package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.game.ticTacToe.GameState;

import java.util.ArrayList;
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
            flipTiles(move);
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

    public void flipTiles(int[] move) {
        ArrayList<Integer[]> tilesToFlip = new ArrayList<>();
        int[][] dirs = {
                {1, 0},   // down
                {-1, 0},  // up
                {0, 1},   // right
                {0, -1},  // left
                {-1, -1}, // up-left
                {-1, 1},  // up-right
                {1, -1},  // down-left
                {1, 1}    // down-right
        };

        for (int[] d : dirs) {
            int x = move[0];
            int y = move[1];

            while (true) {
                x += d[0];
                y += d[1];

                if (x < 0 || x >= board.getHeight() || y < 0 || y >= board.getWidth())
                    break;

                OthelloTile current = this.getBoard().getTile(x, y);

                if (current == OthelloTile.EMPTY){
                    tilesToFlip.clear();
                    break;
                }

                if (current != OthelloTile.EMPTY && current != activeTurnPlayer.getSymbol()){
                    tilesToFlip.add(new Integer[]{x,y});
                }

                if (current == activeTurnPlayer.getSymbol()) {
                    for(Integer[] t : tilesToFlip){
                        this.getBoard().setTile(t[0], t[1], activeTurnPlayer.getSymbol());
                    }
                    tilesToFlip.clear();
                    break;
                }
            }
        }
    }

    @Override
    public boolean checkWin(int x, int y, Player<OthelloTile> p) {
        return false;
    }
}

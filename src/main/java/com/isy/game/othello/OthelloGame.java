package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.game.ticTacToe.GameState;
import com.isy.util.GameSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OthelloGame extends Game<OthelloTile> {
    private boolean useReversiRules = false;

    public OthelloGame(Player<OthelloTile>[] players) {
        super(new Board<>(8, 8, OthelloTile.EMPTY, OthelloTile::createBoard), players);

        this.useReversiRules = GameSettings.get().getUseReversiRules() && this.client == null;

        if (!this.useReversiRules) {
            this.getBoard().setTile(3, 3, this.players[1].getSymbol());
            this.getBoard().setTile(4, 4, this.players[1].getSymbol());
            this.getBoard().setTile(3, 4, this.players[0].getSymbol());
            this.getBoard().setTile(4, 3, this.players[0].getSymbol());
        }
    }

    @Override
    public boolean handleSingleTurn() {
        int[] move = null;

        List<int[]> availableMoves = new ArrayList<>();
        if (this.useReversiRules && this.turnCounter <= 4) {
            availableMoves = this.openingAvailableMoves();
        } else {
            availableMoves = OthelloUtils.getAvailableMoves2(getBoard(), this.activeTurnPlayer.getSymbol(), (OthelloTile) this.getOpponent().getSymbol());

        }

        for (int[] availableMove : availableMoves) {
            System.out.println("available move: " + availableMove[0] + ", " + availableMove[1]);
        }
        if (this.activeTurnPlayer instanceof OthelloHumanPlayer) this.addAvailableMovesToBoard(availableMoves);

        this.renderBoard();

        if (availableMoves.isEmpty()) {
            if (this.useReversiRules) {
                this.state = GameState.LOST;
                return true;
            }
            this.giveTurnOver();
            return false;
        }

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

        this.removeAvailableMovesFromBoard();
        boolean correctMove = isAvailable ? this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol()) : false;

        if (this.useReversiRules && this.turnCounter <= 4) {
            this.giveTurnOver();
            return false;
        }

        if (correctMove) {
            flipTiles(move);
            if (this.checkWin(move[0], move[1], this.activeTurnPlayer)){
                this.state = GameState.WON;
                return false;
            } else if (this.board.isBoardFull()) {
                this.giveTurnOver();
                if (this.checkWin(0,0, this.activeTurnPlayer)) {
                    this.state = GameState.WON;
                }
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

                if (x < 0 || x >= board.getHeight() || y < 0 || y >= board.getWidth()) {
                    tilesToFlip.clear();
                    break;
                }

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
                        this.getBoard().setTile(t[0], t[1], activeTurnPlayer.getSymbol(), true);
                    }
                    tilesToFlip.clear();
                    break;
                }
            }
        }
    }

    private List<int[]> openingAvailableMoves() {
        List<int[]> moves = new ArrayList<>();
        int[][] centerTiles = new int[][]{ new int[]{3, 3}, new int[]{3, 4}, new int[]{4, 3}, new int[]{4, 4}};

        for (int[] coord : centerTiles) {
            OthelloTile tile = this.getBoard().getTile(coord[0], coord[1]);
            if (tile != OthelloTile.PLAYER_1 && tile != OthelloTile.PLAYER_2) {
                moves.add(coord);
            }
        }

        return moves;
    }

    private void addAvailableMovesToBoard(List<int[]> availableMoves) {
        for (int[] move : availableMoves) {
            this.getBoard().setTile(move[0], move[1], OthelloTile.POSSIBLE_MOVE);
        }
    }

    private void removeAvailableMovesFromBoard() {
        for (int row = 0; row < this.getBoard().getHeight(); row++) {
            for (int col = 0; col < this.getBoard().getWidth(); col++) {
                if (this.getBoard().getTile(col, row) == OthelloTile.POSSIBLE_MOVE) {
                    this.getBoard().setTile(col, row, OthelloTile.EMPTY, true);
                }
            }
        }
    }


    @Override
    public boolean checkWin(int x, int y, Player<OthelloTile> p) {
        int currentPlayerCount = 0;
        int opponentPlayerCount = 0;

        OthelloTile opponentTile = (OthelloTile) this.getOpponent().getSymbol();

        for (OthelloTile[] row : this.getBoard().getTiles()) {
            for (OthelloTile tile : row) {
                if (tile == p.getSymbol()) {
                    currentPlayerCount++;
                } else if (tile == opponentTile){
                    opponentPlayerCount++;
                }
            }
        }

        System.out.println("currentplayer count = " + currentPlayerCount + p.getSymbol().toString());
        System.out.println("opponentPlayer count = " + opponentPlayerCount + opponentTile.toString());

        if (opponentPlayerCount == 0) {
            return true;
        }

        if (this.getBoard().isBoardFull()) {
            return currentPlayerCount > opponentPlayerCount;
        }

        return false;
    }
}

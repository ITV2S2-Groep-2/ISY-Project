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

        List<int[]> availableMoves = OthelloUtils.getAvailableMoves(getBoard(), this.activeTurnPlayer.getSymbol(), (OthelloTile) this.getOpponent().getSymbol(), useReversiRules && this.getTurnCounter() <= 4);
//        for (int[] availableMove : availableMoves) {
//            System.out.println("available move: " + availableMove[0] + ", " + availableMove[1]);
//        }
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

        move = this.activeTurnPlayer.getMove(this);
        if (move == null) {
            return false;
        }
//        System.out.println(Arrays.toString(move));
//        System.out.println(this.activeTurnPlayer.getSymbol());

        int[] finalMove = move;
        boolean isAvailable = availableMoves.stream().anyMatch(val -> val[0] == finalMove[0] && val[1] == finalMove[1]);

        this.removeAvailableMovesFromBoard();
        boolean correctMove = isAvailable && this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol());

        if (this.useReversiRules && this.turnCounter <= 4) {
            this.giveTurnOver();
            return false;
        }

        if (correctMove) {
            flipTiles(move);

            /*
                wincheck
                return true to end game
                board is full -> count stones -> edit state -> end game
                board is not full but both players have no moves left -> count stones -> end game
             */
            if (this.board.isBoardFull()) {
                int state = this.hasMoreTiles(this.activeTurnPlayer.getSymbol());
//                System.out.println(state);
                switch (state) {
                    case 0:
                        return true;
                    case 1:
                        this.state = GameState.WON;
                        return true;
                    case 2:
                        this.state = GameState.LOST;
                        return true;
                }
            } else {
                List<int[]> availableMovesUpcoming = OthelloUtils.getAvailableMoves(getBoard(), this.activeTurnPlayer.getSymbol(), (OthelloTile) this.getOpponent().getSymbol(), useReversiRules && this.getTurnCounter() <= 4);
                List<int[]> availableMovesOpponent = OthelloUtils.getAvailableMoves(getBoard(), (OthelloTile) this.getOpponent().getSymbol(), this.activeTurnPlayer.getSymbol(), useReversiRules && this.getTurnCounter() <= 4);

                if (availableMovesUpcoming.isEmpty() && availableMovesOpponent.isEmpty()) {
                    int state = this.hasMoreTiles(this.activeTurnPlayer.getSymbol());
//                    System.out.println(state);
                    switch (state) {
                        case 0:
                            return true;
                        case 1:
                            this.state = GameState.WON;
                            return true;
                        case 2:
                            this.state = GameState.LOST;
                            return true;
                    }
                }
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
                        this.getBoard().setTile(t[0], t[1], activeTurnPlayer.getSymbol());
                    }
                    tilesToFlip.clear();
                    break;
                }
            }
        }
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
                    this.getBoard().setTile(col, row, OthelloTile.EMPTY);
                }
            }
        }
    }


    @Override
    public boolean checkWin(int x, int y, Player<OthelloTile> p) {
        int currentPlayerCount = 0;
        int opponentPlayerCount = 0;

        OthelloTile opponentTile = (OthelloTile) this.getOpponent().getSymbol();

        for (int indexX = 0; indexX < this.getBoard().getWidth(); indexX++) {
            for (int indexY = 0; indexY < this.board.getHeight(); indexY++) {
                OthelloTile tile = this.board.getTile(indexX, indexY);

                if (tile == p.getSymbol()) {
                    currentPlayerCount++;
                } else if (tile == opponentTile){
                    opponentPlayerCount++;
                }
            }
        }

        if (opponentPlayerCount == 0) {
            return true;
        }

        if (this.getBoard().isBoardFull()) {
            return currentPlayerCount > opponentPlayerCount;
        }

        return false;
    }

    /*
        counts tiles from type of symbol and compares to count of opponent
        returns 0 for equal, 1 for more, 2 for less
     */
    public int hasMoreTiles(OthelloTile p) {
        int currentPlayerCount = 0;
        int opponentPlayerCount = 0;

        OthelloTile opponentTile = (OthelloTile) this.getOpponent().getSymbol();

        for (int indexX = 0; indexX < this.getBoard().getWidth(); indexX++) {
            for (int indexY = 0; indexY < this.board.getHeight(); indexY++) {
                OthelloTile tile = this.board.getTile(indexX, indexY);

                if (tile == p) {
                    currentPlayerCount++;
                } else if (tile == opponentTile){
                    opponentPlayerCount++;
                }
            }
        }

        if (currentPlayerCount > opponentPlayerCount) {
            return 1;
        }
        if (opponentPlayerCount >  currentPlayerCount) {
            return 2;
        }

        return 0;
    }

    public boolean getUseReversiRules() {
        return this.useReversiRules;
    }
}

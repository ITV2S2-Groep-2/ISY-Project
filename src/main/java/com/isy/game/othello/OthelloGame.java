package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.game.player.RemotePlayer;
import com.isy.game.ticTacToe.GameState;
import com.isy.util.GameSettings;

import java.util.ArrayList;
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

        List<int[]> availableMoves = this.getAvailableMoves(getBoard(), this.activeTurnPlayer.getSymbol(), this.getOpponent().getSymbol());

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
        this.removeAvailableMovesFromBoard();

        if (move == null) {
            return false;
        }

        int[] finalMove = move;
        boolean isAvailable = availableMoves.stream().anyMatch(val -> {
            return val[0] == finalMove[0] && val[1] == finalMove[1];
        });

        this.removeAvailableMovesFromBoard();
        boolean correctMove = isAvailable && this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol());

        if (correctMove && this.client != null && !(this.activeTurnPlayer instanceof RemotePlayer<?>)) {
            this.activeTurnPlayer.sendServerData(move);
        }

        if (this.useReversiRules && this.turnCounter <= 4) {
            this.giveTurnOver();
            return false;
        }

        if (correctMove) {
            OthelloUtils.flipTiles(this.getBoard(), move[0], move[1], this.activeTurnPlayer.getSymbol());
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

    @Override
    public List<int[]> getAvailableMoves(Board<OthelloTile> board, OthelloTile playerSymbol, OthelloTile opponentSymbol) {
        return OthelloUtils.getAvailableMoves(board, playerSymbol, opponentSymbol, this.useReversiRules && this.getTurnCounter() <= 4);
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

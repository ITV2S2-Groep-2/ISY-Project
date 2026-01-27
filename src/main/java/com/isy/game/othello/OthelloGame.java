package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.game.player.RemotePlayer;
import com.isy.game.ticTacToe.GameState;
import com.isy.gui.scene.GameScene;
import com.isy.util.GameSettings;

import java.util.Arrays;
import java.util.List;

public class OthelloGame extends Game<OthelloTile> {
    private boolean useReversiRules = false;
    private final boolean[][] updatedTiles;
    private final int[] newTile;
    public static String[][] aiMoves = new String[8][8];
    public static final boolean DEBUG = false;

    public OthelloGame(Player<OthelloTile>[] players) {
        super(new Board<>(8, 8, OthelloTile.EMPTY, OthelloTile::createBoard), players);

        this.useReversiRules = GameSettings.get().getUseReversiRules() && this.client == null;

        if (!this.useReversiRules) {
            this.getBoard().setTile(3, 3, this.players[1].getSymbol());
            this.getBoard().setTile(4, 4, this.players[1].getSymbol());
            this.getBoard().setTile(3, 4, this.players[0].getSymbol());
            this.getBoard().setTile(4, 3, this.players[0].getSymbol());
        }

        this.updatedTiles = new boolean[this.getBoard().getWidth()][this.getBoard().getHeight()];
        this.newTile = new int[2];

        for (boolean[] booleans : this.updatedTiles) {
            Arrays.fill(booleans, false);
        }
    }

    @Override
    public boolean handleSingleTurn() {
        int[] move = null;

        List<int[]> availableMoves = this.getAvailableMoves(getBoard(), this.activeTurnPlayer.getSymbol(), this.getOpponent().getSymbol());

        if (availableMoves.isEmpty()) {
            if (this.useReversiRules) {
                this.state = GameState.LOST;
                return true;
            }
            if (this.checkWin(this.activeTurnPlayer, this.getOpponent()) != GameState.ONGOING) return true;
            this.giveTurnOver();
            return false;
        }

        if (this.activeTurnPlayer instanceof OthelloHumanPlayer) this.addAvailableMovesToBoard(availableMoves);
        this.renderBoard();

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

        if (correctMove) {
            if (this.client != null && !(this.activeTurnPlayer instanceof RemotePlayer<?>))
                this.activeTurnPlayer.sendServerData(move);

            newTile[0] = move[0];
            newTile[1] = move[1];
            OthelloUtils.flipTiles(this.getBoard(), move[0], move[1], this.activeTurnPlayer.getSymbol(), this.updatedTiles);

            this.checkWin(this.activeTurnPlayer, this.getOpponent());

            if (this.state == GameState.ONGOING) this.giveTurnOver();
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
    public GameState checkWin(Player<OthelloTile> p, Player<OthelloTile> o) {
        if (this.getBoard().isBoardFull()
                ||
                (OthelloUtils.getAvailableMoves(getBoard(), players[0].getSymbol(), players[1].getSymbol(), useReversiRules && this.getTurnCounter() <= 3).isEmpty()
                        && OthelloUtils.getAvailableMoves(getBoard(), players[1].getSymbol(), players[0].getSymbol(), useReversiRules && this.getTurnCounter() <= 3).isEmpty()
                )
        ) {
            int state = this.hasMoreTiles(players[0].getSymbol());
            switch (state) {
                case 0:
                    this.state = GameState.DRAW;
                    return GameState.DRAW;
                case 1:
                    this.state = GameState.WON;
                    return GameState.WON;
                case 2:
                    this.state = GameState.LOST;
                    return GameState.LOST;
            }
        }
        return GameState.ONGOING;
    }

    /**
     * counts tiles from type of symbol and compares to count of opponent
     * returns 0 for equal, 1 for more, 2 for less
     */
    public int hasMoreTiles(OthelloTile p) {
        int currentPlayerCount = 0;
        int opponentPlayerCount = 0;

        OthelloTile opponentTile = players[1].getSymbol();

        for (int indexX = 0; indexX < this.getBoard().getWidth(); indexX++) {
            for (int indexY = 0; indexY < this.board.getHeight(); indexY++) {
                OthelloTile tile = this.board.getTile(indexX, indexY);

                if (tile == p) {
                    currentPlayerCount++;
                } else if (tile == opponentTile) {
                    opponentPlayerCount++;
                }
            }
        }

        if (currentPlayerCount > opponentPlayerCount) {
            return 1;
        }
        if (opponentPlayerCount > currentPlayerCount) {
            return 2;
        }

        return 0;
    }

    @Override
    public void renderBoard() {
        super.renderBoard();
        if (this.getRenderScene() != null && this.getRenderScene() instanceof GameScene gs) {
            gs.setHighLights(this.updatedTiles, this.newTile);
        }

        if (DEBUG) {
            for (int row = 0; row < this.getBoard().getHeight(); row++) {
                for (int col = 0; col < this.getBoard().getWidth(); col++) {
                    if (aiMoves[row][col] != null) {
                        String aiMove = aiMoves[row][col];
                        ((GameScene) this.getRenderScene()).setValue(row, col, aiMove);
                    }
                }
            }
        }
    }

    public boolean getUseReversiRules() {
        return this.useReversiRules;
    }

    public void cleanUp() {
        for (Player p : this.players) {
            if (p instanceof OthelloAIPlayer ai) {
                ai.cleanup();
            }
        }
    }
}

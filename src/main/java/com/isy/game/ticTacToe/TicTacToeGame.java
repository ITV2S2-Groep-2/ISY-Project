package com.isy.game.ticTacToe;

import com.isy.game.Player;
import com.isy.game.Game;
import com.isy.gui.scene.TicTacToeScene;
import org.jetbrains.annotations.Nullable;

public class TicTacToeGame extends Game {
    private final Board board;
    private final Player[] players;
    private Player activeTurnPlayer;
    private GameState state;

    public TicTacToeGame() {
        this.board = new Board();
        this.players = new Player[]{new HumanPlayer("1", Tile.X), new AiPlayer("2", Tile.O)};
        this.activeTurnPlayer = players[0];
        this.state = GameState.ONGOING;
    }

    public void handleGameTurn(int @Nullable [] move) {
        if (this.state == GameState.WON) { return; }

        switch (this.activeTurnPlayer) {
            case AiPlayer p -> {
                System.out.println("ai turn");
                int[] aiMove = p.getMove(this.getBoard());
                boolean correctMove = this.getBoard().setTile(aiMove[0], aiMove[1], p.getSymbol());
                if (correctMove) {
                    this.giveTurnOver();
                }
                this.handleGameTurn(null);
            }
            case HumanPlayer p -> {
                System.out.println("human turn");
                if (move == null) {
                    break;
                }
                boolean correctMove = this.getBoard().setTile(move[0], move[1], p.getSymbol());
                if (correctMove) {
                    this.giveTurnOver();
                }
                this.handleGameTurn(null);
            }
            default -> {}
        }

        if (this.getRenderScene() != null && this.getRenderScene() instanceof TicTacToeScene) {
            ((TicTacToeScene) this.getRenderScene()).reloadBoardValues(this);
        }
        //TODO: check for winstate
    }

    public void giveTurnOver() {
        if (this.activeTurnPlayer.equals(this.players[0])) {
            this.activeTurnPlayer = this.players[1];
        } else {
            this.activeTurnPlayer = this.players[0];
        }
    }

    public Board getBoard() {
        return board;
    }
}

package com.isy;

import com.isy.gui.scene.Scene;
import com.isy.gui.scene.TicTacToeScene;

import java.util.Optional;

public class Game {
    final private Board board;
    final private Player[] players;
    private Player activeTurnPlayer;
    private GameState state;
    private Scene renderScene;

    public Game() {
        this.board = new Board();
        this.players = new Player[]{new HumanPlayer("1", Tile.X), new AiPlayer("2", Tile.O)};
        this.activeTurnPlayer = players[0];
        this.state = GameState.ONGOING;
    }

    public void handleGameTurn(Optional<int[]> move) {
        if (this.state == GameState.WON) { return; }

        switch (this.activeTurnPlayer) {
            case AiPlayer p -> {
                System.out.println("ai turn");
                int[] aiMove = p.getMove(this.getBoard());
                boolean correctMove = this.getBoard().setTile(aiMove[0], aiMove[1], p.getSymbol());
                if (correctMove) {
                    this.giveTurnOver();
                }
                this.handleGameTurn(Optional.empty());
            }
            case HumanPlayer p -> {
                System.out.println("human turn");
                if (move.isEmpty()) {
                    break;
                }
                int[] humanMove = move.get();
                boolean correctMove = this.getBoard().setTile(humanMove[0], humanMove[1], p.getSymbol());
                if (correctMove) {
                    this.giveTurnOver();
                }
                this.handleGameTurn(Optional.empty());
            }
            default -> {}
        }

        if (this.renderScene != null && this.renderScene instanceof TicTacToeScene) {
            ((TicTacToeScene) this.renderScene).reloadBoardValues(this);
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

    public void setRenderScene(Scene scene) {
        this.renderScene = scene;
        scene.initGame(this);
    }
}

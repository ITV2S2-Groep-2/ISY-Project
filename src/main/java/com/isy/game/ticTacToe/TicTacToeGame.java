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

        if (this.activeTurnPlayer instanceof AiPlayer aiPlayer){
            move = aiPlayer.getMove(this.getBoard());
        }

        if (move != null) {
            boolean correctMove = this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol());
            if (correctMove) {
                if(this.board.checkWin(move[0], move[1], this.activeTurnPlayer)){
                    this.state = GameState.WON;
                    if (this.getRenderScene() != null && this.getRenderScene() instanceof TicTacToeScene ttts) {
                        ttts.reloadBoardValues(this);
                    }
                    return;
                }
                this.giveTurnOver();
            }
            this.handleGameTurn(null);
            return;
        }

        if (this.getRenderScene() != null && this.getRenderScene() instanceof TicTacToeScene ttts) {
            ttts.reloadBoardValues(this);
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

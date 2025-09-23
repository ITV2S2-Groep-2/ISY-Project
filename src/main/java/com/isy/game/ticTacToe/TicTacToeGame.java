package com.isy.game.ticTacToe;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.game.Game;
import com.isy.gui.PlayerEventManager;
import com.isy.gui.scene.TicTacToeScene;

import static com.isy.await.Await.await;

public class TicTacToeGame extends Game implements Runnable {
    private final Board board;
    private final Player[] players;
    private Player activeTurnPlayer;
    private GameState state;
    private GameServer client;

    public TicTacToeGame() {
        this.board = new Board();
        this.players = new Player[]{new HumanPlayer("1", Tile.X), new AiPlayer("2", Tile.O)};
        this.activeTurnPlayer = players[0];
        this.state = GameState.ONGOING;
        this.client = null;
    }

    public void setClient(GameServer client){
        this.client = client;
    }

    public void gameLoop() {
        while (this.state != GameState.WON){
            int[] move = null;

            if (this.getRenderScene() != null && this.getRenderScene() instanceof TicTacToeScene ttts) {
                ttts.reloadBoardValues(this);
            }

            move = this.activeTurnPlayer.getMove(this.getBoard());
            boolean correctMove = this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol());
            if (correctMove) {
                if(this.board.checkWin(move[0], move[1], this.activeTurnPlayer)){
                    this.state = GameState.WON;
                    continue;
                }
                this.giveTurnOver();
            }
        }

        if (this.getRenderScene() != null && this.getRenderScene() instanceof TicTacToeScene ttts) {
            ttts.reloadBoardValues(this);
        }
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

    @Override
    public void run() {
        gameLoop();
    }
}

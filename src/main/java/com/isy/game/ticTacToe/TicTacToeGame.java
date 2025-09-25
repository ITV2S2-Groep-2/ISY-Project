package com.isy.game.ticTacToe;

import com.isy.Main;
import com.isy.await.IWaitable;
import com.isy.game.Player;
import com.isy.game.Game;
import com.isy.gui.PlayerEventManager;
import com.isy.gui.scene.TicTacToeScene;
import com.isy.gui.scene.WinScene;

import java.util.Arrays;

import static com.isy.await.Await.await;

public class TicTacToeGame extends Game implements Runnable {
    private final Board board;
    private final Player[] players;
    private Player activeTurnPlayer;
    private GameState state;

    public TicTacToeGame(Player[] players) {
        this.board = new Board();
        this.players = players;
        this.activeTurnPlayer = players[0];
        this.state = GameState.ONGOING;
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
                System.out.println(this.board.isBoardFull());

                if(this.board.checkWin(move[0], move[1], this.activeTurnPlayer)){
                    this.state = GameState.WON;
                    continue;
                } else if (this.board.isBoardFull()) {
                    break;
                }

                this.giveTurnOver();
            }
        }

        if (this.getRenderScene() != null && this.getRenderScene() instanceof TicTacToeScene ttts) {
            ttts.reloadBoardValues(this);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (this.state == GameState.WON){
            ((WinScene) Main.window.getManager().getScene("winScene")).win(this.activeTurnPlayer.getName());
        }else{
            ((WinScene) Main.window.getManager().getScene("winScene")).win("Nobody");
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

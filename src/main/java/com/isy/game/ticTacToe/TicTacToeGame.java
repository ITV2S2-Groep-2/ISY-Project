package com.isy.game.ticTacToe;

import com.isy.Main;
import com.isy.game.Board;
import com.isy.game.player.Player;
import com.isy.game.Game;
import com.isy.gui.scene.GameScene;
import com.isy.gui.scene.WinScene;
import com.isy.server.Server;
import com.isy.server.await.Promise;
import com.isy.util.PlayerEventManager;
import com.isy.util.lang.LangHandler;

import static com.isy.server.ServerUtils.asyncAwait;

public class TicTacToeGame extends Game<TicTacToeTile> {

    public TicTacToeGame(Player<TicTacToeTile>[] players) {
        super(new Board<>(3, 3, TicTacToeTile.EMPTY, TicTacToeTile::createBoard), players);
    }

    //TODO: ADD A CHECK OUTSIDE REMOTE PLAYER FOR SERVER FORFEITS(THIS IS NOT WORKING AS INTENDED AT THE MOMENT!
    public void gameLoop() {
        boolean isOnline = this.client != null;

        if (isOnline){
            asyncAwait(new Promise("^SVR GAME (?:WIN|LOSS).*"), (result) -> {
                System.out.println(result);

                if(result.toUpperCase().contains("ERR")){

                }else if(result.toUpperCase().contains("WIN")){
                    Server.getInstance().addFakeMessage("ERR GAME STOPPED");
                    this.setState(GameState.WON);
                    PlayerEventManager.get().stop();
                }else if(result.toUpperCase().contains("LOSS")) {
                    Server.getInstance().addFakeMessage("ERR GAME STOPPED");
                    this.setState(GameState.LOST);
                    PlayerEventManager.get().stop();
                }
            });
        }

        while (this.state == GameState.ONGOING) {
            int[] move = null;

            if (this.getRenderScene() != null && this.getRenderScene() instanceof GameScene gs) {
                gs.reloadBoardValues(this);
            }

            move = this.activeTurnPlayer.getMove(this.getBoard());
            if(move == null){
                continue;
            }
            boolean correctMove = this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol());
            if (correctMove) {
                if(this.checkWin(move[0], move[1], this.activeTurnPlayer)){
                    this.state = GameState.WON;
                    continue;
                } else if (this.board.isBoardFull()) {
                    break;
                }

                this.giveTurnOver();
            }
        }

        if (this.getRenderScene() != null && this.getRenderScene() instanceof GameScene gs) {
            gs.reloadBoardValues(this);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (this.state == GameState.WON){
            String playerName = this.activeTurnPlayer.getName();
            ((WinScene) Main.window.getManager().getScene("winScene")).win(playerName, isOnline);
        }else if(this.state == GameState.LOST){
            ((WinScene) Main.window.getManager().getScene("winScene")).lost(LangHandler.get().translate("win_scene.person.you"), isOnline);
        } else {
            ((WinScene) Main.window.getManager().getScene("winScene")).win(LangHandler.get().translate("win_scene.person.nobody"), isOnline);
        }
    }

    @Override
    public boolean checkWin(int x, int y, Player<TicTacToeTile> p) {
        TicTacToeTile symbol = p.getSymbol();

        boolean rowWin = true;
        for (int i = 0; i < 3; i++) {
            if (this.getBoard().getTile(i, y) != symbol) {
                rowWin = false;
                break;
            }
        }
        if (rowWin) return true;


        boolean colWin = true;
        for (int i = 0; i < 3; i++) {
            if (this.board.getTile(x, i) != symbol) {
                colWin = false;
                break;
            }
        }
        if (colWin) return true;


        if (x == y) {
            boolean diagWin = true;
            for (int i = 0; i < 3; i++) {
                if (this.board.getTile(i, i) != symbol) {
                    diagWin = false;
                    break;
                }
            }
            if (diagWin) return true;
        }


        if (x + y == 2) {
            boolean antiDiagWin = true;
            for (int i = 0; i < 3; i++) {
                if (this.board.getTile(i, 2 - i) != symbol) {
                    antiDiagWin = false;
                    break;
                }
            }
            return antiDiagWin;
        }

        return false;
    }
}

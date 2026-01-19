package com.isy.game;

import com.isy.Main;
import com.isy.game.player.Player;
import com.isy.game.player.RemotePlayer;
import com.isy.game.ticTacToe.GameState;
import com.isy.gui.scene.GameScene;
import com.isy.gui.scene.manager.Scene;
import com.isy.gui.scene.WinScene;
import com.isy.server.Server;
import com.isy.server.await.Promise;
import com.isy.util.PlayerEventManager;
import com.isy.util.lang.LangHandler;

import static com.isy.server.ServerUtils.asyncAwait;

public abstract class Game<T extends Enum<T> & ITile> implements Runnable {
    private Scene renderScene;
    protected final Board<T> board;
    protected final Player<T>[] players;
    protected Player<T> activeTurnPlayer;
    protected GameState state;
    protected Server client;

    protected int turnCounter = 0;

    public Game(Board<T> board, Player<T>[] players) {
        this.board = board;
        this.players = players;
        this.activeTurnPlayer = players[0];
        this.state = GameState.ONGOING;
        this.client = Server.getInstance();
    }

    public void setState(GameState state){
        this.state = state;
    }

    //TODO: ADD A CHECK OUTSIDE REMOTE PLAYER FOR SERVER FORFEITS(THIS IS NOT WORKING AS INTENDED AT THE MOMENT!
    public void gameLoop() {
        boolean isOnline = this.client != null;

        /*
            online game state check
         */
        if (isOnline){
            asyncAwait(new Promise("^SVR GAME (?:WIN|LOSS).*"), (result) -> {
//                System.out.println(result);

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


        /*
            turn handler
         */
        while (this.state == GameState.ONGOING) {
            this.turnCounter++;
            boolean cancel = this.handleSingleTurn();
            if (cancel) {
                break;
            }
        }


        this.renderBoard();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*
            winscene handler
         */
        Main.window.getManager().addScene(new WinScene(Main.window));
        if (this.state == GameState.WON){
            String playerName = this.activeTurnPlayer.getName();
            ((WinScene) Main.window.getManager().getScene("winScene")).win(playerName, isOnline);
        }else if(this.state == GameState.LOST){
            ((WinScene) Main.window.getManager().getScene("winScene")).lost(LangHandler.get().translate("win_scene.person.you"), isOnline);
        } else {
            ((WinScene) Main.window.getManager().getScene("winScene")).win(LangHandler.get().translate("win_scene.person.nobody"), isOnline);
        }
    };

    public boolean handleSingleTurn() {
        int[] move = null;

        this.renderBoard();

        move = this.activeTurnPlayer.getMove(this);
        if(move == null){
            return false;
        }

        boolean correctMove = this.getBoard().setTile(move[0], move[1], this.activeTurnPlayer.getSymbol());

        if (correctMove && this.client != null && !(this.activeTurnPlayer instanceof RemotePlayer<?>)) {
            this.activeTurnPlayer.sendServerData(move);
        }

        if (correctMove) {
            if(this.checkWin(move[0], move[1], this.activeTurnPlayer)){
                this.state = GameState.WON;
                return false;
            } else if (this.board.isBoardFull()) {
                return true;
            }

            this.giveTurnOver();
        }

        return false;
    }

    public void renderBoard() {
        if (this.getRenderScene() != null && this.getRenderScene() instanceof GameScene gs) {
            gs.reloadBoardValues();
        }
    }


    public abstract boolean checkWin(int x, int y, Player<T> p);

    public void setRenderScene(Scene scene){
        this.renderScene = scene;
        scene.initGame(this);
    }

    public Scene getRenderScene(){
        return this.renderScene;
    }

    public void giveTurnOver() {
        if (this.activeTurnPlayer.equals(this.players[0])) {
            this.activeTurnPlayer = this.players[1];
        } else {
            this.activeTurnPlayer = this.players[0];
        }
    }

    public Board<T> getBoard() {
        return board;
    }

    public Player<T> getActiveTurnPlayer() {
        return this.activeTurnPlayer;
    }

    public Player<T> getOpponent() {
        if (this.players[0] == this.activeTurnPlayer) {
            return this.players[1];
        } else {
            return this.players[0];
        }
    }

    public int getTurnCounter() {
        return this.turnCounter;
    }

    @Override
    public void run() {
        gameLoop();
    }

}

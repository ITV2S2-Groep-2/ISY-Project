package com.isy.game;

import com.isy.game.ticTacToe.GameState;
import com.isy.gui.scene.Scene;

public abstract class Game implements Runnable {
    private Scene renderScene;
    protected final Board board;
    protected final Player[] players;
    protected Player activeTurnPlayer;
    protected GameState state;
    protected GameServer client;

    public Game(Board board, Player[] players) {
        this.board = board;
        this.players = players;
        this.activeTurnPlayer = players[0];
        this.state = GameState.ONGOING;
        this.client = null;
    }

    public GameServer getClient(){
        return this.client;
    }

    public void setClient(GameServer client){
        this.client = client;
    }
    public void setState(GameState state){
        this.state = state;
    }

    public abstract void gameLoop();

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

    public Board getBoard() {
        return board;
    }

    @Override
    public void run() {
        gameLoop();
    }

}

package com.isy.game.ticTacToe;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RemotePlayer extends Player{
    private final BlockingQueue<Integer> moveQueue = new LinkedBlockingQueue<>();
    private TicTacToeGame game;

    public RemotePlayer(String name, Tile symbol, GameServer client){
        super(name, symbol, client);


        // Listener
        client.addListener(line -> {
            if (line.contains("SVR GAME MOVE")) {
                int moveIndex = line.indexOf("MOVE:");
                if (moveIndex != -1) {
                    int startQuote = line.indexOf("\"", moveIndex);
                    int endQuote = line.indexOf("\"", startQuote + 1);
                    if (startQuote != -1 && endQuote != -1) {
                        String move = line.substring(startQuote + 1, endQuote);
                        int moveInt = Integer.parseInt(move);
                        moveQueue.offer(moveInt);
                    }
                }
            }

            if (line.contains("SVR GAME LOSS")) {
                this.game.setState(GameState.LOST);
                System.out.println(moveQueue.offer(-1));
                PlayerEventManager.get().stop();
            }

            if (line.contains("SVR GAME WIN")){
                this.game.setState(GameState.WON);
                System.out.println(moveQueue.offer(-1));
                PlayerEventManager.get().stop();
            }
        });
    }

    public void setGame(TicTacToeGame game){
        this.game = game;
    }

    @Override
    public int[] getMove(Board board) {
        if(client == null){
            throw new IllegalStateException("RemotePlayer needs a server client");
        }

        try {
            int move = moveQueue.take();
            if (move == -1) {
                return null;
            }
            return formatServerMove(move);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

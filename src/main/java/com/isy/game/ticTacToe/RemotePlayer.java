package com.isy.game.ticTacToe;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.isy.await.Await.await;

public class RemotePlayer extends Player{
    private final BlockingQueue<int[]> moveQueue = new LinkedBlockingQueue<>();

    public RemotePlayer(String name, Tile symbol, GameServer client){
        super(name, symbol, client);

        client.addListener(line -> {
            if (line.startsWith("SVR GAME MOVE")) {
                String coords = line.substring(line.indexOf("MOVE:") + 5).replaceAll("[^0-9,]", "");
                String[] parts = coords.split(",");
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());
                moveQueue.offer(new int[]{row, col});
            }
        });
    }

    @Override
    public int[] getMove(Board board) {
        if(client == null){
            throw new IllegalStateException("RemotePlayer needs a server client");
        }

        try {
            return moveQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

package com.isy.game.ticTacToe;

import com.isy.game.GameServer;
import com.isy.game.Player;
import com.isy.gui.PlayerEventManager;

import static com.isy.await.Await.await;

public class RemotePlayer extends Player{
    public RemotePlayer(String name, Tile symbol, GameServer client){
        super(name, symbol, client);
    }

    @Override
    public int[] getMove(Board board) {
        if(client == null){
            throw new IllegalStateException("RemotePlayer needs a server client");
        }

        while(true){
            String line = client.readServerLine();
            if(line != null && line.startsWith("MOVE")){
                String[] parts = line.split(" ");
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                return new int[]{row, col};
            }
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        }
    }
}

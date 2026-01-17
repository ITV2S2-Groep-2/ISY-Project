package com.isy.util;

import com.isy.game.Game;
import com.isy.game.othello.OthelloGame;
import com.isy.game.othello.OthelloTile;
import com.isy.game.othello.OthelloUtils;
import com.isy.game.player.Player;
import com.isy.server.Server;

import java.util.List;
import java.util.Random;

public class OthelloRandomAI extends Player<OthelloTile> {

    public OthelloRandomAI(String name, OthelloTile symbol, Server client){
        super(name, symbol, client);
    }


    @Override
    public int[] getMove(Game<OthelloTile> game) {
        OthelloGame othelloGame = (OthelloGame) game;
        boolean useReversiRules = othelloGame.getUseReversiRules();
        List<int[]> availableMoves = OthelloUtils.getAvailableMoves(game.getBoard(),
                game.getActiveTurnPlayer().getSymbol(), game.getOpponent().getSymbol(), useReversiRules && game.getTurnCounter() <= 4);

        if (availableMoves.isEmpty()) {
            return null;
        }

        Random rand = new Random();
        //        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        return availableMoves.get(rand.nextInt(availableMoves.size()));
    }

}

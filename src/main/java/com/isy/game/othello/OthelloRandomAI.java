package com.isy.game.othello;

import com.isy.game.Game;
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
        return availableMoves.get(rand.nextInt(availableMoves.size()));
    }

}

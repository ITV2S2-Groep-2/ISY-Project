package com.isy.game.othello;

import com.isy.game.Game;
import com.isy.game.othello.utils.OthelloUtilities;
import com.isy.game.player.HumanPlayer;
import com.isy.game.player.Player;

import java.util.List;

public class OthelloGame extends Game {

    public OthelloGame(Player[] players) {
        super(new OthelloBoard(), players);
    }

    @Override
    public void renderBoard() {
        super.renderBoard();
        System.out.println("rerender othello");
        if (this.activeTurnPlayer instanceof HumanPlayer) {
            List<int[]> list = OthelloUtilities.getAvailableMoves(this.getBoard(), this.activeTurnPlayer);
            System.out.println("possible moves: ");
            for (int[] entry : list) {
                System.out.println(entry[0] + ", " + entry[1]);
            }
        }
    }

}

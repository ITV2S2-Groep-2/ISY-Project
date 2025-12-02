package com.isy.game.othello;

import com.isy.game.Board;

import java.util.ArrayList;
import java.util.List;

//TODO: MOVE THIS TO THE OTHELLO GAME CLASS WHEN IT IS MADE
public class OthelloUtils {
    public static List<int[]> getAvailableMoves(Board<OthelloTile> board, OthelloTile myTile, OthelloTile enemyTile){
        List<int[]> moves = new ArrayList<>();

        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (isPossible(board, myTile, enemyTile, x, y))
                    moves.add(new int[]{x, y});
            }
        }

        return moves;
    }

    private static final List<int[]> directions = List.of(new int[]{1, 0}, new int[]{-1, 0},
            new int[]{0, 1}, new int[]{0, -1},
            new int[]{1, 1}, new int[]{-1, 1},
            new int[]{1, -1}, new int[]{-1, -1});

    private static boolean isPossible(Board<OthelloTile> board, OthelloTile myTile, OthelloTile enemyTile, int x, int y){
        if (board.getTile(x, y) != OthelloTile.EMPTY) return false;

        for (int[] direction : directions) {
            int cX = x + direction[0];
            int cY = y + direction[1];

            if (board.getTile(cX, cY) != enemyTile) continue;

            boolean possible = false;
            while (cX >= 0 && cX < board.getWidth() && cY >= 0 && cY < board.getHeight()){
                if (board.getTile(cX, cY) == OthelloTile.EMPTY) break;
                if (board.getTile(cX, cY) == enemyTile) continue;
                if (board.getTile(cX, cY) == myTile){
                    possible = true;
                    break;
                }

                cX += direction[0];
                cY += direction[1];
            }

            if (possible) return true;
        }

        return false;
    }
}

package com.isy.game.othello;

import com.isy.game.Board;

import java.util.ArrayList;
import java.util.List;

import static com.isy.util.OthelloAI.getAvailableMoves2;

public class OthelloUtils {

    private static final List<int[]> directions = List.of(new int[]{1, 0}, new int[]{-1, 0},
            new int[]{0, 1}, new int[]{0, -1},
            new int[]{1, 1}, new int[]{-1, 1},
            new int[]{1, -1}, new int[]{-1, -1});


    public static List<int[]> getAvailableMoves(Board<OthelloTile> board, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour) {
        ArrayList<int[]> availableMoves = new ArrayList<>();

        if (reversiFirstFour) {
            return openingAvailableMoves(board);
        }

        /*
            loop over each tile of given player
            go over each direction (horizontal, vertical, diagonal) from given tiles
            find empty space directly behind opponents tile.
         */

        byte[] moves = getAvailableMoves2(board, playerSymbol, opponentSymbol, reversiFirstFour);
        for (byte move : moves) {
            if (move == 0) break;

            int x = ((move >> 4) & 0b00001111) - 1;
            int y = (move & 0b00001111) - 1;

            availableMoves.add(new int[]{x, y});
        }

        return availableMoves;
    }

    private static void addUniqueCoords(List<int[]> list, int[] newCoords) {
        boolean duplicate = false;
        for (int[] coords : list) {
            if (coords[0] == newCoords[0] && coords[1] == newCoords[1]) {
                duplicate = true;
                break;
            }
        }
        if (!duplicate) {
            list.add(newCoords);
        }
    }

    public static List<int[]> openingAvailableMoves(Board<OthelloTile> board) {
        List<int[]> moves = new ArrayList<>();
        int[][] centerTiles = new int[][]{ new int[]{3, 3}, new int[]{3, 4}, new int[]{4, 3}, new int[]{4, 4}};

        for (int[] coord : centerTiles) {
            OthelloTile tile = board.getTile(coord[0], coord[1]);
            if (tile != OthelloTile.PLAYER_1 && tile != OthelloTile.PLAYER_2) {
                moves.add(coord);
            }
        }

        return moves;
    }

}

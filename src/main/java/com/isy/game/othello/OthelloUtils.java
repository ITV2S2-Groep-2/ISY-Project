package com.isy.game.othello;

import com.isy.game.Board;

import java.util.ArrayList;
import java.util.List;

public class OthelloUtils {
    public static int boardSize = 8;
    public static int BOARD_SIZED_SQUARED = boardSize * boardSize;

    private static final List<int[]> directions = List.of(
            new int[]{1, 0}, new int[]{-1, 0},
            new int[]{0, 1}, new int[]{0, -1},
            new int[]{1, 1}, new int[]{-1, 1},
            new int[]{1, -1}, new int[]{-1, -1}
    );

    public static List<int[]> getAvailableMoves(Board<OthelloTile> board, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour) {
        ArrayList<int[]> availableMoves = new ArrayList<>();

        /*
            loop over each tile of given player
            go over each direction (horizontal, vertical, diagonal) from given tiles
            find empty space directly behind opponents tile.
         */
        byte[] moves = getAvailableMovesBytes(board, playerSymbol, opponentSymbol, reversiFirstFour);
        for (byte move : moves) {
            if (move == 0) break;

            int x = ((move >> 4) & 0b00001111) - 1;
            int y = (move & 0b00001111) - 1;

            availableMoves.add(new int[]{x, y});
        }

        return availableMoves;
    }

    public static byte[] getAvailableMovesBytes(Board<OthelloTile> board, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour) {
        byte[] availableMoves = new byte[BOARD_SIZED_SQUARED];
        return getAvailableMovesCore(board, playerSymbol, opponentSymbol, availableMoves, reversiFirstFour);
    }

    private static final int[][] centerTiles = new int[][]{
            {3, 3}, {3, 4}, {4, 3}, {4, 4}
    };

    public static byte[] getAvailableMovesCore(Board<OthelloTile> board, OthelloTile playerSymbol, OthelloTile opponentSymbol, byte[] availableMoves, boolean reversiFirstFour) {
        int index = 0;

        if (reversiFirstFour){
            for (int[] centerTile : centerTiles) {
                if (board.getTile(centerTile[0], centerTile[1]) != OthelloTile.EMPTY && board.getTile(centerTile[0], centerTile[1]) != OthelloTile.POSSIBLE_MOVE) continue;
                availableMoves[index] = (byte) (((centerTile[0] + 1) << 4) | (centerTile[1] + 1));
                index++;
            }
            availableMoves[index] = 0;
            return availableMoves;
        }

        long addedCoords = 0;

        for (int row = 0; row < board.getWidth(); row++) {
            for (int col = 0; col < board.getHeight(); col++) {
                if (board.getTile(row, col) == playerSymbol) {
                    for (int[] direction : directions) {
                        int cX = row + direction[0];
                        int cY = col + direction[1];

                        boolean foundOpponentSymbol = false;
                        while (cX >= 0 && cX < board.getWidth() && cY >= 0 && cY < board.getHeight()) {
                            OthelloTile tile = board.getTile(cX, cY);

                            if (tile == playerSymbol) {
                                break;
                            } else if (tile == opponentSymbol) {
                                foundOpponentSymbol = true;
                            } else if (tile == OthelloTile.EMPTY) {
                                if (foundOpponentSymbol) {
                                    int moveIndex = cX * board.getWidth() + cY;
                                    long r = 1L << moveIndex;
                                    if ((r & addedCoords) == 0) {
                                        addedCoords |= r;
                                        availableMoves[index] = (byte) (((cX + 1) << 4) | (cY + 1));

                                        index++;
                                    }
                                }
                                break;
                            }
                            cX += direction[0];
                            cY += direction[1];
                        }
                    }
                }
            }
        }

        availableMoves[index] = 0;
        return availableMoves;
    }

    public static void flipTiles(Board<OthelloTile> board, int xO, int yO, OthelloTile symbol) {
        flipTiles(board, xO, yO, symbol, null);
    }


    public static void flipTiles(Board<OthelloTile> board, int xO, int yO, OthelloTile symbol, boolean[][] updatedTiles) {
        ArrayList<Integer[]> tilesToFlip = new ArrayList<>();
        if (updatedTiles != null) updatedTiles[xO][yO] = true;

        for (int[] d : directions) {
            int x = xO;
            int y = yO;

            while (true) {
                x += d[0];
                y += d[1];

                if (x < 0 || x >= board.getWidth() || y < 0 || y >= board.getHeight()) {
                    tilesToFlip.clear();
                    break;
                }

                OthelloTile current = board.getTile(x, y);

                if (current == OthelloTile.EMPTY) {
                    tilesToFlip.clear();
                    break;
                }

                if (current != symbol) {
                    tilesToFlip.add(new Integer[]{x, y});
                }

                if (current == symbol) {
                    for (Integer[] t : tilesToFlip) {
                        board.setTile(t[0], t[1], symbol);
                        if (updatedTiles != null) updatedTiles[t[0]][t[1]] = true;
                    }
                    tilesToFlip.clear();
                    break;
                }
            }
        }
    }

}

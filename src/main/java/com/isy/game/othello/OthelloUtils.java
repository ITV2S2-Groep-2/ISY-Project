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


    public static List<int[]> getAvailableMoves2(Board board, OthelloTile playerSymbol, OthelloTile opponentSymbol) {
        //  TODO: idea: create a Set instead of checking if coords are duplicate. Using a Set would require to create a coordinates object with an overridden equals function.
        ArrayList<int[]> availableMoves = new ArrayList<>();

        /*
            loop over each tile of given player
            go over each direction (horizontal, vertical, diagonal) from given tiles
            find empty space directly behind opponents tile.
         */
        Enum<?>[][] tiles = board.getTiles();

        for (int row = 0; row < board.getWidth(); row++) {
            for (int col = 0; col < board.getHeight(); col++) {
                if (tiles[row][col] == playerSymbol) {

                    /*
                        vertical
                     */
                    boolean foundOpponentSymbol = false;
                    for (int vertical = col + 1; vertical < board.getHeight(); vertical++) {
                        if (tiles[row][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[row][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[row][vertical] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{row, vertical});
                            }
                            break;
                        }
                    }

                    foundOpponentSymbol = false;
                    for (int vertical = col - 1; vertical >= 0; vertical--) {
                        if (tiles[row][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[row][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[row][vertical] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{row, vertical});
                            }
                            break;
                        }
                    }


                    /*
                        horizontal
                     */
                    foundOpponentSymbol = false;
                    for (int horizontal = row + 1; horizontal < board.getWidth(); horizontal++) {
                        if (tiles[horizontal][col] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][col] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][col] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, col});
                            }
                            break;

                        }
                    }

                    foundOpponentSymbol = false;
                    for (int horizontal = row - 1; horizontal >= 0; horizontal--) {
                        if (tiles[horizontal][col] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][col] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][col] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, col});
                            }
                            break;
                        }
                    }


                    /*
                        diagonal
                     */
                    // up right
                    foundOpponentSymbol = false;
                    for (int horizontal = row + 1, vertical = col + 1; horizontal < board.getWidth() && vertical < board.getHeight(); horizontal++, vertical++) {
                        if (tiles[horizontal][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][vertical] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, vertical});
                            }
                            break;
                        }
                    }

                    // up left
                    foundOpponentSymbol = false;
                    for (int horizontal = row - 1, vertical = col + 1; horizontal >= 0 && vertical < board.getHeight(); horizontal--, vertical++) {
                        if (tiles[horizontal][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][vertical] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, vertical});
                            }
                            break;
                        }
                    }

                    // down left
                    foundOpponentSymbol = false;
                    for (int horizontal = row - 1, vertical = col - 1; horizontal >= 0 && vertical >= 0; horizontal--, vertical--) {
                        if (tiles[horizontal][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][vertical] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, vertical});
                            }
                            break;
                        }
                    }

                    // down right
                    foundOpponentSymbol = false;
                    for (int horizontal = row + 1, vertical = col - 1; horizontal < board.getWidth() && vertical >= 0; horizontal++, vertical--) {
                        if (tiles[horizontal][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][vertical] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, vertical});
                            }
                            break;
                        }
                    }

                }
            }
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

}

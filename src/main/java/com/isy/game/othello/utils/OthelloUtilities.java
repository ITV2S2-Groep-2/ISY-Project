package com.isy.game.othello.utils;

import com.isy.game.Board;
import com.isy.game.Tile;
import com.isy.game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class OthelloUtilities {

    public static List<int[]> getAvailableMoves(Board board, Player player) {
        //  TODO: idea: create a Set instead of checking if coords are duplicate. Using a Set would require to create a coordinates object with an overridden equals function.
        ArrayList<int[]> availableMoves = new ArrayList<>();

        // TODO: othello tile
        Tile playerSymbol = player.getSymbol();
        Tile opponentSymbol = playerSymbol == Tile.O ? Tile.X : Tile.O;

        /*
            loop over each tile of given player
            go over each direction (horizontal, vertical, diagonal) from given tiles
            find empty space directly behind opponents tile.
         */
        Tile[][] tiles = board.getTiles();

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
                        if (tiles[row][vertical] == Tile.EMPTY) {
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
                        if (tiles[row][vertical] == Tile.EMPTY) {
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
                        if (tiles[horizontal][col] == Tile.EMPTY) {
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
                        if (tiles[horizontal][col] == Tile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, col});
                            }
                            break;
                        }
                    }


                    /*
                        diagonal
                     */
                    foundOpponentSymbol = false;
                    for (int horizontal = row + 1, vertical = col + 1; horizontal < board.getWidth() && vertical < board.getHeight(); horizontal++, vertical++) {
                        if (tiles[horizontal][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][vertical] == Tile.EMPTY) {
                            if (foundOpponentSymbol) {
                                addUniqueCoords(availableMoves, new int[]{horizontal, vertical});
                            }
                            break;
                        }
                    }

                    foundOpponentSymbol = false;
                    for (int horizontal = row - 1, vertical = col - 1; horizontal >= 0 && vertical >= 0; horizontal--, vertical--) {
                        if (tiles[horizontal][vertical] == playerSymbol) {
                            break;
                        }
                        if (tiles[horizontal][vertical] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        }
                        if (tiles[horizontal][vertical] == Tile.EMPTY) {
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

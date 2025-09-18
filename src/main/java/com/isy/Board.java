package com.isy;

import java.util.Arrays;

public class Board {
    private final Tile[][] tiles;

    public Board(){
        this.tiles = new Tile[3][3];

        this.resetBoard();
    }

    /**
     * Returns the Tile on the board
     * @param x x location of tile, min of 0 and max of 2
     * @param y y location of tile, min of 0 and max of 2
     * @return The given tile at the location specified
     */
    public Tile getTile(int x, int y){
        return this.tiles[x][y];
    }

    /**
     * Set Tile at given location
     * @param x x location of tile, min of 0 and max of 2
     * @param y y location of tile, min of 0 and max of 2
     * @param tile the Tile you want the location to be
     * @return True if tile has been successfully set, returns false when the tile at the location specified was not empty(can't override tiles in tic tac toe)
     */
    public boolean setTile(int x, int y, Tile tile){
        if (getTile(x, y) != Tile.EMPTY)
            return false;

        this.tiles[x][y] = tile;

        return true;
    }

    /**
     * Reset the boards to EMPTY
     */
    public void resetBoard(){
        for (Tile[] tile : this.tiles) {
            Arrays.fill(tile, Tile.EMPTY);
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    /**
     * Converts board to String, usefully for debugging
     * @return A string containing an ascii layout of the board
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                stringBuilder.append(getTile(x, y).toString());
                if(x < 2)
                    stringBuilder.append("|");
            }

            if(y < 2)
                stringBuilder.append("\n-+-+-\n");
        }

        return stringBuilder.toString();
    }
}

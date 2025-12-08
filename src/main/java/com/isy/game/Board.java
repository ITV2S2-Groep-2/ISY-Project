package com.isy.game;

import java.util.Arrays;

public class Board<T extends Enum<T> & ITile> {
    private final T[][] tiles;
    private final T emptyTile;
    private final int height;
    private final int width;

    public Board(int height, int width, T emptyTile, getConstructor<T> constructor){
        this.height = height;
        this.width = width;
        this.emptyTile = emptyTile;
        this.tiles = constructor.newInstance(this.width, this.height);

        this.resetBoard();
    }

    /**
     * Returns the Tile on the board
     * @param x x location of tile, min of 0 and max of 2
     * @param y y location of tile, min of 0 and max of 2
     * @return The given tile at the location specified
     */
    public T getTile(int x, int y){
        return this.tiles[x][y];
    }

    /**
     * Set Tile at given location
     * @param x x location of tile, min of 0 and max of 2
     * @param y y location of tile, min of 0 and max of 2
     * @param tile the Tile you want the location to be
     * @return True if tile has been successfully set, returns false when the tile at the location specified was not empty(can't override tiles in tic tac toe)
     */
    public boolean setTile(int x, int y, T tile){
//        if (getTile(x, y) != this.emptyTile)
//            return false;
        // eff uitgezet voor othello, dit wordt nog gecheckt.

        this.tiles[x][y] = tile;

        return true;
    }

    public boolean isBoardFull(){
        boolean isFull = true;

        for (T[] tiles : this.tiles) {
            for (T tile : tiles) {
                if (tile == this.emptyTile) {
                    isFull = false;
                    break;
                }
            }
        }

        return isFull;
    }

    /**
     * Reset the boards to EMPTY
     */
    public void resetBoard(){
        for (T[] tile : this.tiles) {
            Arrays.fill(tile, this.emptyTile);
        }
    }

    public T[][] getTiles() {
        return tiles;
    }

    public int getHeight() {
        return this.height ;
    }

    public int getWidth() {
        return this.width ;
    }

    public interface getConstructor<T>{
        T[][] newInstance(int width, int height);
    }
}

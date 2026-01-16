package com.isy.game;

import com.isy.game.othello.OthelloTile;

import java.util.Arrays;

public class Board<T extends Enum<T> & ITile> {
    private final T[] tiles;
    private final T emptyTile;
    private final int height;
    private final int width;
    private final int[] amounts;

    public Board(int height, int width, T emptyTile, getConstructor<T> constructor){
        this.height = height;
        this.width = width;
        this.emptyTile = emptyTile;
        this.tiles = constructor.newInstance(this.width, this.height);
        this.amounts = new int[emptyTile.size()];

        this.resetBoard();
    }

    public Board(int height, int width, T emptyTile, T[] tiles, int[] amounts){
        this.height = height;
        this.width = width;
        this.emptyTile = emptyTile;
        this.tiles = tiles;
        this.amounts = amounts;
    }

    private static final int SHIFT = 3;
    private int getIndex(int x, int y){
        return (x << SHIFT) | y;
    }

    /**
     * Returns the Tile on the board
     * @param x x location of tile, min of 0 and max of 2
     * @param y y location of tile, min of 0 and max of 2
     * @return The given tile at the location specified
     */
    public T getTile(int x, int y){
        return this.tiles[getIndex(x, y)];
    }

    public T getTile(int index){
        return this.tiles[index];
    }


    /**
     * Set Tile at given location
     * @param x x location of tile, min of 0 and max of 2
     * @param y y location of tile, min of 0 and max of 2
     * @param tile the Tile you want the location to be
     * @return True if tile has been successfully set, returns false when the tile at the location specified was not empty(can't override tiles in tic tac toe)
     */
    public boolean setTile(int x, int y, T tile){
        int index = getIndex(x, y);

        this.amounts[tile.index()]++;
        this.amounts[getTile(index).index()]--;

        this.tiles[index] = tile;

        return true;
    }

    public boolean isBoardFull(){
        for (T tile : this.tiles) {
            if (tile == this.emptyTile) {
                return false;
            }
        }

        return true;
    }

    /**
     * Reset the boards to EMPTY
     */
    public void resetBoard(){
        Arrays.fill(this.tiles, this.emptyTile);
        Arrays.fill(this.amounts, 0);
        this.amounts[this.emptyTile.index()] = this.width * this.height;
    }

    public int getAmount(int index){
        return this.amounts[index];
    }

    public Board<OthelloTile> copyBoard(){
        OthelloTile[] copy = new OthelloTile[this.tiles.length];
        System.arraycopy((OthelloTile[]) this.tiles, 0, copy, 0, this.tiles.length);
        int[] amountCopy = new int[this.amounts.length];
        System.arraycopy(this.amounts, 0, amountCopy, 0, this.amounts.length);
        return new Board<>(this.height, this.width, OthelloTile.EMPTY, copy, amountCopy);
    }

    public int getHeight() {
        return this.height ;
    }

    public int getWidth() {
        return this.width ;
    }

    public interface getConstructor<T>{
        T[] newInstance(int width, int height);
    }
}

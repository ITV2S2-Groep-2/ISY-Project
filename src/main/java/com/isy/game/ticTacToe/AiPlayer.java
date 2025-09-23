package com.isy.game.ticTacToe;

import com.isy.game.Player;

public class AiPlayer extends Player {
    public AiPlayer(String name, Tile symbol){
        super(name, symbol);
    }

    int boardSize = 3;
    int maxDepth = 12;
    // getMove() etc.

    @Override
    public int[] getMove(Board board) {
        //TEMP
        Tile[][] tiles = board.getTiles();
        return getBestMove(tiles);

//        for (int r = 0; r < board.getHeight(); r++) {
//            for (int c = 0; c < board.getWidth(); c++) {
//                if (tiles[r][c] == Tile.EMPTY) {
//                    return new int[] {r,c};
//                }
//            }
//        }
//        return new int[]{1,1};
    }

    public int[] getBestMove(Tile[][] tiles){
        int[] bestMove = new int[]{-1, -1};
        int bestValue = Integer.MIN_VALUE;
        Tile symbol = getSymbol();
        Tile otherSymbol = (symbol == Tile.X) ? Tile.O : Tile.X;

        for(int col = 0; col < boardSize; col++){
            for(int row = 0; row < boardSize; row++){
                if(tiles[col][row] == Tile.EMPTY){
                    tiles[col][row] = symbol;
                    int moveValue = Minimax(tiles, maxDepth, false);
                    tiles[col][row] = Tile.EMPTY;
                    if (moveValue > bestValue) {
                        bestMove[0] = row;
                        bestMove[1] = col;
                        bestValue = moveValue;
                    }
                }
            }
        }

        return bestMove;
    }

    public int evaluateBoard(Tile[][] tiles){
        Tile symbol = getSymbol();
        Tile otherSymbol = (symbol == Tile.X) ? Tile.O : Tile.X;
        int win = 3;
        int sumSymbol = 0;
        int sumOther = 0;

        for(int col = 0; col < boardSize; col++){
            for(int row = 0; row < boardSize; row++){
                if(tiles[col][row] == symbol) { sumSymbol++; }
                else if(tiles[col][row] == otherSymbol){ sumOther++; }
            }

            if(sumSymbol == win){ return 10; }
            else if(sumOther == win) {return -10;}

            sumSymbol = 0;
            sumOther = 0;
        }

        for(int row = 0; row < boardSize; row++){
            for(int col = 0; col < boardSize; col++){
                if(tiles[col][row] == symbol) { sumSymbol++; }
                else if(tiles[col][row] == otherSymbol){ sumOther++; }
            }

            if(sumSymbol == win){ return 10; }
            else if(sumOther == win) {return -10;}

            sumSymbol = 0;
            sumOther = 0;
        }

        for(int i = 0; i < boardSize; i++){
            if(tiles[i][i] == symbol) { sumSymbol++; }
            else if(tiles[i][i] == otherSymbol){ sumOther++; }
        }

        if(sumSymbol == win){ return 10; }
        else if(sumOther == win) {return -10;}

        sumSymbol = 0;
        sumOther = 0;

        int indexMax = boardSize - 1;
        for(int i = 0; i < indexMax; i++){
            if(tiles[i][indexMax - i] == symbol) { sumSymbol++; }
            else if(tiles[i][indexMax - i] == otherSymbol){ sumOther++; }
        }

        if(sumSymbol == win){ return 10; }
        else if(sumOther == win) {return -10;}

        return 0;
    }

    boolean boardFull(Tile[][] tiles) {
        for(int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize; row++) {
                if (tiles[col][row] == Tile.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public int Minimax(Tile[][] tiles, int depth, boolean isMax){
        int boardValue = evaluateBoard(tiles);
        Tile symbol = getSymbol();
        Tile otherSymbol = (symbol == Tile.X) ? Tile.O : Tile.X;

        // Terminal node (win/lose/draw) or max depth reached.
        if (Math.abs(boardValue) == 10 || depth == 0
                || !boardFull(tiles)) {
            return boardValue;
        }

        if(isMax){
            int highestVal = -10000;
            for(int col = 0; col < boardSize; col++){
                for(int row = 0; row < boardSize; row++){
                    if(tiles[col][row] == Tile.EMPTY){
                        tiles[col][row] = symbol;
                        highestVal = Math.max(highestVal, Minimax(tiles, depth-1, false));
                        tiles[col][row] = Tile.EMPTY;
                    }
                }
            }
            return highestVal;
        }
        else{
            int lowestVal = 10000;
            for(int col = 0; col < boardSize; col++){
                for(int row = 0; row < boardSize; row++){
                    if(tiles[col][row] == Tile.EMPTY){
                        tiles[col][row] = otherSymbol;
                        lowestVal = Math.max(lowestVal, Minimax(tiles, depth-1, true));
                        tiles[col][row] = Tile.EMPTY;
                    }
                }
            }
            return lowestVal;
        }
    }

}

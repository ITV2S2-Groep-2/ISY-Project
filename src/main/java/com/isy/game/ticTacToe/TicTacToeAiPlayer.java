package com.isy.game.ticTacToe;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.server.Server;

public class TicTacToeAiPlayer extends Player<TicTacToeTile> {
    public TicTacToeAiPlayer(String name, TicTacToeTile symbol, Server client){
        super(name, symbol, client);
    }

    int boardSize = 3;
    int maxDepth = 9;

    TicTacToeTile symbol = getSymbol();
    TicTacToeTile otherSymbol = (symbol == TicTacToeTile.X) ? TicTacToeTile.O : TicTacToeTile.X;

    @Override
    public int[] getMove(Game<TicTacToeTile> game) {
        Board<TicTacToeTile> board = game.getBoard();

        TicTacToeTile[][] tiles = board.getTiles();

        // Simuleer wachttijd omdat anders AI soms sneller zet dan de server je in de lobby kan zetten.
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int[] move = getBestMove(tiles);

        return move;
    }

    public int[] getBestMove(TicTacToeTile[][] tiles){
        int[] bestMove = new int[]{-1, -1};
        int bestValue = Integer.MIN_VALUE;

        for(int col = 0; col < boardSize; col++){
            for(int row = 0; row < boardSize; row++){
                if(tiles[col][row] == TicTacToeTile.EMPTY){
                    tiles[col][row] = symbol;
                    int moveValue = Minimax(tiles, maxDepth, false);
                    tiles[col][row] = TicTacToeTile.EMPTY;
                    if (moveValue > bestValue) {
                        bestMove[0] = col;
                        bestMove[1] = row;
                        bestValue = moveValue;
                    }
                }
            }
        }

        return bestMove;
    }

    public int evaluateBoard(TicTacToeTile[][] tiles){
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
        for (int i = 0; i < boardSize; i++) {
            if (tiles[i][indexMax - i] == symbol) sumSymbol++;
            else if (tiles[i][indexMax - i] == otherSymbol) sumOther++;
        }


        if(sumSymbol == win){ return 10; }
        else if(sumOther == win) {return -10;}

        return 0;
    }

    boolean boardFull(TicTacToeTile[][] tiles) {
        for(int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize; row++) {
                if (tiles[col][row] == TicTacToeTile.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public int Minimax(TicTacToeTile[][] tiles, int depth, boolean isMax){
        int boardValue = evaluateBoard(tiles);

        // Terminal node (win/lose/draw) or max depth reached.
        if (Math.abs(boardValue) == 10 || depth == 0
                || boardFull(tiles)) {
            return boardValue;
        }

        if(isMax){
            int highestVal = -10000;
            for(int col = 0; col < boardSize; col++){
                for(int row = 0; row < boardSize; row++){
                    if(tiles[col][row] == TicTacToeTile.EMPTY){
                        tiles[col][row] = symbol;
                        highestVal = Math.max(highestVal, Minimax(tiles, depth-1, false));
                        tiles[col][row] = TicTacToeTile.EMPTY;
                    }
                }
            }
            return highestVal;
        }
        else{
            int lowestVal = 10000;
            for(int col = 0; col < boardSize; col++){
                for(int row = 0; row < boardSize; row++){
                    if(tiles[col][row] == TicTacToeTile.EMPTY){
                        tiles[col][row] = otherSymbol;
                        lowestVal = Math.min(lowestVal, Minimax(tiles, depth-1, true));
                        tiles[col][row] = TicTacToeTile.EMPTY;
                    }
                }
            }
            return lowestVal;
        }
    }

}

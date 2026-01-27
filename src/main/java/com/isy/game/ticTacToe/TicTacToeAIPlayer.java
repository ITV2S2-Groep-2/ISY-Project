package com.isy.game.ticTacToe;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.server.Server;

public class TicTacToeAIPlayer extends Player<TicTacToeTile> {
    public TicTacToeAIPlayer(String name, TicTacToeTile symbol, Server client){
        super(name, symbol, client);
    }

    int boardSize = 3;
    int maxDepth = 9;

    TicTacToeTile symbol = getSymbol();
    TicTacToeTile otherSymbol = (symbol == TicTacToeTile.X) ? TicTacToeTile.O : TicTacToeTile.X;

    @Override
    public int[] getMove(Game<TicTacToeTile> game) {
        Board<TicTacToeTile> board = game.getBoard();

        // Simuleer wachttijd omdat anders AI soms sneller zet dan de server je in de lobby kan zetten.
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return getBestMove(board);
    }

    public int[] getBestMove(Board<TicTacToeTile> board){
        int[] bestMove = new int[]{-1, -1};
        int bestValue = Integer.MIN_VALUE;

        for(int col = 0; col < boardSize; col++){
            for(int row = 0; row < boardSize; row++){
                if(board.getTile(col, row) == TicTacToeTile.EMPTY){
                    board.setTile(col, row, symbol);
                    int moveValue = Minimax(board, maxDepth, false);
                    board.setTile(col, row, TicTacToeTile.EMPTY);
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

    public int evaluateBoard(Board<TicTacToeTile> board){
        int win = 3;
        int sumSymbol = 0;
        int sumOther = 0;

        for(int col = 0; col < boardSize; col++){
            for(int row = 0; row < boardSize; row++){
                if(board.getTile(col, row) == symbol) { sumSymbol++; }
                else if(board.getTile(col, row) == otherSymbol){ sumOther++; }
            }

            if(sumSymbol == win){ return 10; }
            else if(sumOther == win) {return -10;}

            sumSymbol = 0;
            sumOther = 0;
        }

        for(int row = 0; row < boardSize; row++){
            for(int col = 0; col < boardSize; col++){
                if(board.getTile(col, row) == symbol) { sumSymbol++; }
                else if(board.getTile(col, row) == otherSymbol){ sumOther++; }
            }

            if(sumSymbol == win){ return 10; }
            else if(sumOther == win) {return -10;}

            sumSymbol = 0;
            sumOther = 0;
        }

        for(int i = 0; i < boardSize; i++){
            if(board.getTile(i, i) == symbol) { sumSymbol++; }
            else if(board.getTile(i, i) == otherSymbol){ sumOther++; }
        }

        if(sumSymbol == win){ return 10; }
        else if(sumOther == win) {return -10;}

        sumSymbol = 0;
        sumOther = 0;

        int indexMax = boardSize - 1;
        for (int i = 0; i < boardSize; i++) {
            if (board.getTile(i, indexMax - i) == symbol) sumSymbol++;
            else if (board.getTile(i, indexMax - i) == otherSymbol) sumOther++;
        }


        if(sumSymbol == win){ return 10; }
        else if(sumOther == win) {return -10;}

        return 0;
    }

    boolean boardFull(Board<TicTacToeTile> board) {
        return board.isBoardFull();
    }

    public int Minimax(Board<TicTacToeTile> board, int depth, boolean isMax){
        int boardValue = evaluateBoard(board);

        // Terminal node (win/lose/draw) or max depth reached.
        if (Math.abs(boardValue) == 10 || depth == 0
                || boardFull(board)) {
            return boardValue;
        }

        if(isMax){
            int highestVal = -10000;
            for(int col = 0; col < boardSize; col++){
                for(int row = 0; row < boardSize; row++){
                    if(board.getTile(col, row) == TicTacToeTile.EMPTY){
                        board.setTile(col, row, symbol);
                        highestVal = Math.max(highestVal, Minimax(board, depth-1, false));
                        board.setTile(col, row, TicTacToeTile.EMPTY);
                    }
                }
            }
            return highestVal;
        }
        else{
            int lowestVal = 10000;
            for(int col = 0; col < boardSize; col++){
                for(int row = 0; row < boardSize; row++){
                    if(board.getTile(col, row) == TicTacToeTile.EMPTY){
                        board.setTile(col, row, otherSymbol);
                        lowestVal = Math.min(lowestVal, Minimax(board, depth-1, true));
                        board.setTile(col, row, TicTacToeTile.EMPTY);
                    }
                }
            }
            return lowestVal;
        }
    }

}

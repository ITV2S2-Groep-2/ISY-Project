package com.isy.game.othello;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.player.Player;
import com.isy.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.isy.game.othello.OthelloUtils.BOARD_SIZED_SQUARED;
import static com.isy.game.othello.OthelloUtils.boardSize;

public class OthelloAIPlayer extends Player<OthelloTile> {
    public static final double MOBILITY = 5, CORNER = 25, STABILITY = 10, DISC = 1;
    public static final int DEPTH = 4;

    String parentName = "BASEMODEL";
    double mobility_diffWeight = 5;
    double corner_diffWeight = 25;
    double stability_diffWeight = 10;
    double disc_diffWeight = 1;
    int maxDepth = 4;

    OthelloTile symbol = getSymbol();
    OthelloTile otherSymbol = (symbol == OthelloTile.PLAYER_1) ? OthelloTile.PLAYER_2 : OthelloTile.PLAYER_1;

    Game<OthelloTile> game = null;
    boolean useReversiRules = false;
    boolean reversiFirstFour = false;

    int[][] corners = {
            {0,0},
            {0, boardSize - 1},
            {boardSize - 1, 0},
            {boardSize -1, boardSize - 1}
    };

    final ExecutorService executor = Executors.newFixedThreadPool(16);

    public long totalMoveTime = 0;
    public long minMoveTime = Long.MAX_VALUE;
    public long maxMoveTime = Long.MIN_VALUE;
    public int moveCount = 0;

    /*
    debugging / testing
     */
    int evalCount = 0;
    double stableTime = 0;
    double beforeTime = 0;
    double afterTime = 0;

    public OthelloAIPlayer(String name, OthelloTile symbol, Server client){
        this(name, symbol, client, MOBILITY, CORNER, STABILITY, DISC, DEPTH, null);
    }

    private OthelloAIPlayer(String name, OthelloTile symbol, Server client, double mobWeight, double cornerWeight, double stabilityWeight, double discWeight, int maxDepth, String parentName){
        super(name, symbol, client);
        this.mobility_diffWeight = mobWeight;
        this.corner_diffWeight = cornerWeight;
        this.stability_diffWeight = stabilityWeight;
        this.disc_diffWeight = discWeight;
        this.maxDepth = maxDepth;
        this.parentName = parentName;
    }

    public String getParent() {
        return parentName;
    }

    public double getMobility_diffWeight() {
        return mobility_diffWeight;
    }

    public double getCorner_diffWeight() {
        return corner_diffWeight;
    }
    public double getStability_diffWeight() {
        return stability_diffWeight;
    }
    public double getDisc_diffWeight() {
        return disc_diffWeight;
    }
    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public int[] getMove(Game<OthelloTile> gameArg) {
        long startTime = System.nanoTime();
        game = gameArg;
        OthelloGame othelloGame = (OthelloGame) game;
        Board<OthelloTile> board = game.getBoard();

        useReversiRules = othelloGame.getUseReversiRules() && game.getTurnCounter() <= 4;
        reversiFirstFour = useReversiRules && game.getTurnCounter() <= 4;

        List<int[]> availableMoves = OthelloUtils.getAvailableMoves(game.getBoard(),
                game.getActiveTurnPlayer().getSymbol(), game.getOpponent().getSymbol(), reversiFirstFour);

        if (availableMoves.isEmpty()) {
            return null;
        }

        this.evalCount = 0;

        int[] move = getBestMove(board);

        long endTime = System.nanoTime();
        this.moveCount++;
        long currMoveTime = endTime - startTime;
        if (currMoveTime < this.minMoveTime) this.minMoveTime = currMoveTime;
        if (currMoveTime > this.maxMoveTime) this.maxMoveTime = currMoveTime;
        this.totalMoveTime += currMoveTime;

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return move;
    }

    public int[] getBestMove(Board<OthelloTile> board){
        int[] bestMove = new int[]{-1, -1};
        int bestValue = Integer.MIN_VALUE;

        this.beforeTime = 0;
        this.stableTime = 0;
        this.afterTime = 0;

        int tileCounter = 64;
        for(int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board.getTile(col, row) == OthelloTile.EMPTY) {
                    tileCounter--;
                }
            }
        }

        byte[] avm = OthelloUtils.getAvailableMovesBytes(board, this.symbol, this.otherSymbol, reversiFirstFour);

        List<Future<MoveEvaluation>> futures = new ArrayList<>();

        for (byte move : avm) {
            if (move == 0) break;

            int x = ((move >> 4) & 0b00001111) - 1;
            int y = (move & 0b00001111) - 1;

            tileCounter++;
            Board<OthelloTile> copiedBoard = board.copyBoard();
            copiedBoard.setTile(x, y, symbol);
            OthelloUtils.flipTiles(copiedBoard, x, y, symbol);

            final int tileCounterFinal = tileCounter;
            Callable<MoveEvaluation> task = () -> {
                int moveValue = minimax(copiedBoard, maxDepth, -100000, 100000, false, false, tileCounterFinal);
                return new MoveEvaluation(x, y, moveValue);
            };
            futures.add(executor.submit(task));
        }

        try {
            for (Future<MoveEvaluation> future : futures) {
                MoveEvaluation result = future.get(); // This blocks until the thread is done
                if (result.score > bestValue) {
                    bestValue = result.score;
                    bestMove = new int[]{result.x, result.y};
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return bestMove;
    }

    private long createBitMask(int x, int y){
        return 1L << (x | (y << 3));
    }

    public int evaluateBoard(Board<OthelloTile> board, int myMobility, int otherMobility){
        this.evalCount++;

        double startTimeBefore = System.nanoTime();

        int value = 0;

        if(useReversiRules){
            //als een pass een loss is:
            if(myMobility == 0){
                return -1000;
            }
        }

        int myTiles = board.getAmount(symbol.index());
        int otherTiles = board.getAmount(otherSymbol.index());

        int totalTiles = myTiles + otherTiles;

        double phase = (double) totalTiles / 64;

        int discDiff = myTiles - otherTiles;

        int myStableDiscs = 0;
        int otherStableDiscs = 0;


        //Check voor corners en geef ze een stable disc wanneer ze die hebben
        int myCorner = 0;
        int otherCorner = 0;

        int cPunishments = 0;
        int xPunishments = 0;

        for (int[] c : corners){
            int x = c[0];
            int y = c[1];

            if(board.getTile(x, y) == symbol){
                myCorner++;
                myStableDiscs++;
                //hoef je niet te checken voor x en c want je krijgt er geen extra pluspunten voor
            }
            else if(board.getTile(x, y) == otherSymbol){
                otherCorner++;
                otherStableDiscs++;
                //hoef je niet te checken voor x en c want je krijgt er geen extra pluspunten voor
            }

            //check voor x en c corner sides en geef harde minpunten voor het hebben van deze zonder de corner te hebben
            int directionX = 1;
            int directionY = 1;
            if(x != 0){
                directionX = -1;
            }
            if(y != 0){
                directionY = -1;
            }

            if(board.getTile(x + directionX, y) == symbol){
                cPunishments++;
            }
            if(board.getTile(x, y + directionY) == symbol){
                cPunishments++;
            }
            if(board.getTile(x + directionX, y + directionY) == symbol){
                xPunishments++;
            }
        }

        int cornerDiff = myCorner - otherCorner;

        double endTimeBefore = System.nanoTime();
        this.beforeTime += endTimeBefore - startTimeBefore;

        double startTimeStable = System.nanoTime();

        //boolean array zodat je stable discs niet dubbel telt en maar 1 keer op stable hoeft te zetten.
        long isStable = 0;

        // doe alleen zoeken naar stable pieces als de corner aan die kant van het bord behouden wordt door de speler

        //als je corner links boven hebt
        if(board.getTile(0, 0) == symbol){

            //count stable discs upper side, left > right
            for (int col = 1; col < boardSize - 1; col++){
                if(board.getTile(col, 0) == board.getTile(col - 1, 0) && board.getTile(col, 0) != OthelloTile.EMPTY){
                    if(board.getTile(col, 0) == symbol){
                        if((isStable & createBitMask(col, 0)) == 0) {
                            isStable |= createBitMask(col, 0);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(col, 0) == otherSymbol) {
                        if((isStable & createBitMask(col, 0)) == 0) {
                            isStable |= createBitMask(col, 0);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs left side, up > down
            for (int row = 1; row < boardSize - 1; row++){
                if(board.getTile(0, row) == board.getTile(0, row - 1) && board.getTile(0, row) != OthelloTile.EMPTY){
                    if(board.getTile(0, row) == symbol) {
                        if((isStable & createBitMask(0, row)) == 0) {
                            isStable |= createBitMask(0, row);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(0, row) == otherSymbol) {
                        if((isStable & createBitMask(0, row)) == 0) {
                            isStable |= createBitMask(0, row);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        //als je corner links onder hebt
        if(board.getTile(0, boardSize - 1) == symbol){

            //count stable discs lower side, left > right
            for (int col = 1; col < boardSize - 1; col++){
                if(board.getTile(col, boardSize - 1) == board.getTile(col - 1, boardSize - 1) && board.getTile(col, boardSize - 1) != OthelloTile.EMPTY){
                    if(board.getTile(col, boardSize - 1) == symbol) {
                        if((isStable & createBitMask(col, boardSize - 1)) == 0) {
                            isStable |= createBitMask(col, boardSize - 1);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(col, boardSize - 1) == otherSymbol) {
                        if((isStable & createBitMask(col, boardSize - 1)) == 0) {
                            isStable |= createBitMask(col, boardSize - 1);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs left side, down > up
            for (int row = boardSize - 2; row > 0; row--){
                if(board.getTile(0, row) == board.getTile(0, row + 1) && board.getTile(0, row) != OthelloTile.EMPTY){
                    if(board.getTile(0, row) == symbol) {
                        if((isStable & createBitMask(0, row)) == 0) {
                            isStable |= createBitMask(0, row);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(0, row) == otherSymbol) {
                        if((isStable & createBitMask(0, row)) == 0) {
                            isStable |= createBitMask(0, row);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        //als je corner rechts boven hebt
        if(board.getTile(boardSize - 1, 0) == symbol){

            //count stable discs upper side, right > left
            for (int col = boardSize - 2; col > 0; col--){
                if(board.getTile(col, 0) == board.getTile(col + 1, 0) && board.getTile(col, 0) != OthelloTile.EMPTY){
                    if(board.getTile(col, 0) == symbol) {
                        if((isStable & createBitMask(col, 0)) == 0) {
                            isStable |= createBitMask(col, 0);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(col, 0) == otherSymbol) {
                        if((isStable & createBitMask(col, 0)) == 0) {
                            isStable |= createBitMask(col, 0);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs right side, up > down
            for (int row = 1; row < boardSize - 1; row++){
                if(board.getTile(boardSize - 1, row) == board.getTile(boardSize - 1, row - 1) && board.getTile(boardSize - 1, row) != OthelloTile.EMPTY){
                    if(board.getTile(boardSize - 1, row) == symbol) {
                        if((isStable & createBitMask(boardSize - 1, row)) == 0) {
                            isStable |= createBitMask(boardSize - 1, row);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(boardSize - 1, row) == otherSymbol) {
                        if((isStable & createBitMask(boardSize - 1, row)) == 0) {
                            isStable |= createBitMask(boardSize - 1, row);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        //als je corner rechts onder hebt
        if(board.getTile(boardSize - 1, boardSize - 1) == symbol){

            //count stable discs lower side, right > left
            for (int col = boardSize - 2; col > 0; col--){
                if(board.getTile(col, boardSize - 1) == board.getTile(col + 1, boardSize - 1) && board.getTile(col, boardSize - 1) != OthelloTile.EMPTY){
                    if(board.getTile(col, boardSize - 1) == symbol) {
                        if((isStable & createBitMask(col, boardSize - 1)) == 0) {
                            isStable |= createBitMask(col, boardSize - 1);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(col, boardSize - 1) == otherSymbol) {
                        if((isStable & createBitMask(col, boardSize - 1)) == 0) {
                            isStable |= createBitMask(col, boardSize - 1);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs right side, down > up
            for (int row = boardSize - 2; row > 0; row--){
                if(board.getTile(boardSize - 1, row) == board.getTile(boardSize - 1, row + 1) && board.getTile(boardSize - 1, row) != OthelloTile.EMPTY){
                    if(board.getTile(boardSize - 1, row) == symbol) {
                        if((isStable & createBitMask(boardSize - 1, row)) == 0) {
                            isStable |= createBitMask(boardSize - 1, row);
                            myStableDiscs++;
                        }
                    }
                    else if (board.getTile(boardSize - 1, row) == otherSymbol) {
                        if((isStable & createBitMask(boardSize - 1, row)) == 0) {
                            isStable |= createBitMask(boardSize - 1, row);
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        int stableDiscDiff = myStableDiscs - otherStableDiscs;

        double endTimeStable = System.nanoTime();
        this.stableTime += endTimeStable - startTimeStable;

        double startTimeAfter = System.nanoTime();

        //heb je gewonnen of verloren?
        if(totalTiles == 64){
            if(discDiff > 0){
                value += 1000;
            } else{
                value -= 1000;
            }
        }

        int mobilityDiff = myMobility - otherMobility;

        double mobilityPhase = (1 - phase);
        double discPhase = phase;

        value += (int)((mobilityDiff * mobility_diffWeight * mobilityPhase) +
                (cornerDiff * corner_diffWeight) +
                (stableDiscDiff * stability_diffWeight) +
                (discDiff * disc_diffWeight * discPhase) -
                (cPunishments + xPunishments * -corner_diffWeight));

        double endTimeAfter = System.nanoTime();
        this.afterTime += endTimeAfter - startTimeAfter;

        return value;
    }

    public int minimax(Board<OthelloTile> board, int depth, int alpha, int beta, boolean isMax, boolean depthIsDecreased, int tileCounter){
        boolean boardFull = (tileCounter == 64);

        byte[] availableMovesUpcoming = new byte[BOARD_SIZED_SQUARED];
        byte[] availableMovesOpponent = new byte[BOARD_SIZED_SQUARED];

        if (isMax) {
            OthelloUtils.getAvailableMovesCore(board, this.symbol, this.otherSymbol, availableMovesUpcoming, reversiFirstFour);
        } else {
            OthelloUtils.getAvailableMovesCore(board, this.otherSymbol, this.symbol, availableMovesOpponent, reversiFirstFour);
        }


        if(boardFull || depth <= 0){
            if (!isMax) {
                OthelloUtils.getAvailableMovesCore(board, this.symbol, this.otherSymbol, availableMovesUpcoming, reversiFirstFour);
            } else {
                OthelloUtils.getAvailableMovesCore(board, this.otherSymbol, this.symbol, availableMovesOpponent, reversiFirstFour);
            }
            return evaluateBoard(board, availableMovesUpcoming.length, availableMovesOpponent.length);
        }

        if(isMax){

            if (availableMovesUpcoming.length > 10 && !depthIsDecreased) {
                depth--;
                depthIsDecreased = true;
            }

            int highestVal = -10000;

            if (!hasMoves(availableMovesUpcoming)) {
                OthelloUtils.getAvailableMovesCore(board, this.otherSymbol, this.symbol, availableMovesOpponent, reversiFirstFour);
                if (!hasMoves(availableMovesOpponent)) {
                    return evaluateBoard(board, 0, 0);
                }

                int curVal = minimax(board, depth-1, alpha, beta, false, depthIsDecreased, tileCounter);
                highestVal= Math.max(highestVal, curVal);

            }


            for (byte move : availableMovesUpcoming) {
                if (move == 0) break;

                int x = ((move >> 4) & 0b00001111) - 1;
                int y = (move & 0b00001111) - 1;

                Board<OthelloTile> copiedBoard = board.copyBoard();
                copiedBoard.setTile(x, y, symbol);
                OthelloUtils.flipTiles(copiedBoard, x, y, symbol);

                tileCounter++;
                int curVal = minimax(copiedBoard, depth-1, alpha, beta, false, depthIsDecreased, tileCounter);

                highestVal= Math.max(highestVal, curVal);
                alpha = Math.max(alpha, curVal);
                if(beta <= alpha){
                    break;
                }
            }

            return highestVal;
        }
        else {

            if (availableMovesOpponent.length > 10 && !depthIsDecreased) {
                depth--;
                depthIsDecreased = true;
            }

            int lowestVal = 10000;

            if (!hasMoves(availableMovesOpponent)) {
                OthelloUtils.getAvailableMovesCore(board, this.otherSymbol, this.symbol, availableMovesUpcoming, reversiFirstFour);
                if (!hasMoves(availableMovesUpcoming)) {
                    return evaluateBoard(board, 0, 0);
                }
                int curVal = minimax(board, depth-1, alpha, beta, true, depthIsDecreased, tileCounter);
                lowestVal= Math.min(lowestVal, curVal);

            }


            for (byte move : availableMovesOpponent) {
                if (move == 0) break;

                int x = ((move >> 4) & 0b00001111) - 1;
                int y = (move & 0b00001111) - 1;

                Board<OthelloTile> copiedBoard = board.copyBoard();
                copiedBoard.setTile(x, y, symbol);
                OthelloUtils.flipTiles(copiedBoard, x, y, symbol);

                tileCounter++;
                int curVal = minimax(copiedBoard, depth-1, alpha, beta, true, depthIsDecreased, tileCounter);

                lowestVal= Math.min(lowestVal, curVal);
                beta = Math.min(beta, curVal);
                if(beta <= alpha){
                    break;
                }
            }

            return lowestVal;
        }
    }


    public static boolean hasMoves(byte[] moves){
        if (moves == null) return false;
        return moves[0] != 0;
    }


    public void cleanup() {
        executor.shutdown();
    }

    private static class MoveEvaluation {
        int x, y;
        int score;
        MoveEvaluation(int x, int y, int score) {
            this.x = x;
            this.y = y;
            this.score = score;
        }
    }
}

package com.isy.util;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.othello.OthelloGame;
import com.isy.game.othello.OthelloTile;
import com.isy.game.othello.OthelloUtils;
import com.isy.game.player.Player;
import com.isy.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class OthelloAI extends Player<OthelloTile> {

    String parentName = "BASEMODEL";
    double mobility_diffWeight = 5;
    double corner_diffWeight = 25;
    double stability_diffWeight = 10;
    double disc_diffWeight = 1;
    int maxDepth = 4;

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


    public OthelloAI(String name, OthelloTile symbol, Server client, double mobWeight, double cornerWeight, double stabilityWeight, double discWeight, int maxDepth, String parentName){
        super(name, symbol, client);
        this.mobility_diffWeight = mobWeight;
        this.corner_diffWeight = cornerWeight;
        this.stability_diffWeight = stabilityWeight;
        this.disc_diffWeight = discWeight;
        this.maxDepth = maxDepth;
        this.parentName = parentName;
    }
    public OthelloAI(String name, OthelloTile symbol, Server client){
        super(name, symbol, client);
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


    public static int boardSize = 8;

    OthelloTile symbol = getSymbol();
    OthelloTile otherSymbol = (symbol == OthelloTile.PLAYER_1) ? OthelloTile.PLAYER_2 : OthelloTile.PLAYER_1;

    Game<OthelloTile> game = null;
    boolean useReversiRules = false;

    int[][] corners = {
            {0,0},
            {0, boardSize - 1},
            {boardSize - 1, 0},
            {boardSize -1, boardSize - 1}
    };


    @Override
    public int[] getMove(Game<OthelloTile> gameArg) {
        long startTime = System.nanoTime();
        game = gameArg;
        OthelloGame othelloGame = (OthelloGame) game;
        Board<OthelloTile> board = game.getBoard();

        useReversiRules = othelloGame.getUseReversiRules();

        List<int[]> availableMoves = OthelloUtils.getAvailableMoves(game.getBoard(),
                game.getActiveTurnPlayer().getSymbol(), game.getOpponent().getSymbol(), useReversiRules && game.getTurnCounter() <= 4);

        if (availableMoves.isEmpty()) {
            return null;
        }

//        double startTime = System.currentTimeMillis();
//        System.out.println("before best move " + startTime);
        this.evalCount = 0;

        int[] move = getBestMove(board);

//        System.out.println("stable count: " + this.evalCount);
//        double endTime = System.currentTimeMillis();
//        System.out.println("after best move: " + Arrays.toString(move) + " " + endTime);
//        System.out.println("time in ms: " + (endTime - startTime));

        long endTime = System.nanoTime();
        this.moveCount++;
        long currMoveTime = endTime - startTime;
        if (currMoveTime < this.minMoveTime) this.minMoveTime = currMoveTime;
        if (currMoveTime > this.maxMoveTime) this.maxMoveTime = currMoveTime;
        this.totalMoveTime += currMoveTime;

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

        byte[] avm = getAvailableMoves2(board, this.symbol, this.otherSymbol, false);

        List<Future<MoveEvaluation>> futures = new ArrayList<>();

        for (byte move : avm) {
            if (move == 0) break;

            int x = ((move >> 4) & 0b00001111) - 1;
            int y = (move & 0b00001111) - 1;

            tileCounter++;
            Board<OthelloTile> copiedBoard = board.copyBoard();
            copiedBoard.setTile(x, y, symbol);
            flipTiles(copiedBoard, x, y, symbol);

            final int tileCounterFinal = tileCounter;
            Callable<MoveEvaluation> task = () -> {
                int moveValue = minimax(copiedBoard, maxDepth, -100000, 100000, false, false, tileCounterFinal);
//                if (moveValue > bestValue){
//                    bestValue = moveValue;
//                    bestMove = new int[]{x, y};
//                }
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
//
//        System.out.println("before time: " + this.beforeTime);
//        System.out.println("stable time: " + this.stableTime);
//        System.out.println("after time: " + this.afterTime);

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
        //onthouden
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
                value += 10000;
            } else{
                value -= 10000;
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
            getAvailableMoves2(board, this.symbol, this.otherSymbol, availableMovesUpcoming);
        } else {
            getAvailableMoves2(board, this.otherSymbol, this.symbol, availableMovesOpponent);
        }


        if(boardFull || depth <= 0){
            if (!isMax) {
                getAvailableMoves2(board, this.symbol, this.otherSymbol, availableMovesUpcoming);
            } else {
                getAvailableMoves2(board, this.otherSymbol, this.symbol, availableMovesOpponent);
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
                getAvailableMoves2(board, this.otherSymbol, this.symbol, availableMovesOpponent);
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
                flipTiles(copiedBoard, x, y, symbol);

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
                getAvailableMoves2(board, this.otherSymbol, this.symbol, availableMovesUpcoming);
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
                flipTiles(copiedBoard, x, y, symbol);

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


    private static final List<int[]> directions = List.of(new int[]{1, 0}, new int[]{-1, 0},
            new int[]{0, 1}, new int[]{0, -1},
            new int[]{1, 1}, new int[]{-1, 1},
            new int[]{1, -1}, new int[]{-1, -1});

    private static int BOARD_SIZED_SQUARED = boardSize * boardSize;

    public static boolean hasMoves(byte[] moves){
        if (moves == null) return false;
        return moves[0] != 0;
    }

    public static byte[] getAvailableMoves2(Board<OthelloTile> board, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour) {
        byte[] availableMoves = new byte[BOARD_SIZED_SQUARED];

        return getAvailableMoves2(board, playerSymbol, opponentSymbol, availableMoves);
    }

    public static byte[] getAvailableMoves2(Board<OthelloTile> board, OthelloTile playerSymbol, OthelloTile opponentSymbol, byte[] availableMoves) {
        int index = 0;

//        if (reversiFirstFour){
//            for (int[] centerTile : centerTiles) {
//                availableMoves[index] = (byte) (((centerTile[0] + 1) << 4) | (centerTile[1] + 1));
//                index++;
//            }
//
//            availableMoves[index] = 0;
//            return availableMoves;
//        }

        long addedCoords = 0;

        for (int row = 0; row < board.getWidth(); row++) {
            for (int col = 0; col < board.getHeight(); col++) {
                if (board.getTile(row, col) == playerSymbol) {
                    for (int[] direction : directions) {
                        int cX = row + direction[0];
                        int cY = col + direction[1];

                        boolean foundOpponentSymbol = false;
                        while (cX >= 0 && cX < boardSize && cY >= 0 && cY < boardSize) {
                            OthelloTile tile = board.getTile(cX, cY);

                            if (tile == playerSymbol) {
                                break;
                            } else if (tile == opponentSymbol) {
                                foundOpponentSymbol = true;
                            } else if (tile == OthelloTile.EMPTY) {
                                if (foundOpponentSymbol) {
                                    int moveIndex = cX * boardSize + cY;
                                    long r = 1L << moveIndex;
                                    if ((r & addedCoords) == 0) {
                                        addedCoords |= r;
                                        availableMoves[index] = (byte) (((cX + 1) << 4) | (cY + 1));
                                        int x = ((availableMoves[index] >> 4) & 0b00001111) - 1;
                                        int y = (availableMoves[index] & 0b00001111) - 1;

//                                        if (x != cX || y != cY)
//                                            throw new RuntimeException("Incorrect byte for coords!: (" + x + ", " + y + ") and (" + cX + ", " + cY + ")");

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
    private static final int[][] centerTiles = new int[][]{
            {3, 3}, {3, 4}, {4, 3}, {4, 4}
    };

    public static List<int[]> openingAvailableMoves(Board<OthelloTile> board) {
        List<int[]> moves = new ArrayList<>();

        for (int[] coord : centerTiles) {
            OthelloTile tile = board.getTile(coord[0], coord[1]);
            if (tile == OthelloTile.EMPTY) {
                moves.add(coord);
            }
        }

        return moves;
    }

    public void flipTiles(Board<OthelloTile> board, int xO, int yO, OthelloTile symbol) {
        ArrayList<Integer[]> tilesToFlip = new ArrayList<>();

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

                if (current == OthelloTile.EMPTY){
                    tilesToFlip.clear();
                    break;
                }

                if (current != symbol){
                    tilesToFlip.add(new Integer[]{x,y});
                }

                if (current == symbol) {
                    for(Integer[] t : tilesToFlip){
                        board.setTile(t[0], t[1], symbol);
                    }
                    tilesToFlip.clear();
                    break;
                }
            }
        }
    }

    public void cleanup() {
        executor.shutdown();
    }
}

class MoveEvaluation {
    int x, y;
    int score;
    MoveEvaluation(int x, int y, int score) {
        this.x = x;
        this.y = y;
        this.score = score;
    }
}
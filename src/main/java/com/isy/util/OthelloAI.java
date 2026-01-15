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

    String parentName = "";
    double mobility_diffWeight = 5;
    double corner_diffWeight = 25;
    double stability_diffWeight = 10;
    double disc_diffWeight = 1;
    int maxDepth = 7;

    final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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
        game = gameArg;
        OthelloGame othelloGame = (OthelloGame) game;
        Board<OthelloTile> board = game.getBoard();
        OthelloTile[][] tiles = board.getTiles();

        useReversiRules = othelloGame.getUseReversiRules();

        List<int[]> availableMoves = OthelloUtils.getAvailableMoves(game.getBoard(),
                game.getActiveTurnPlayer().getSymbol(), game.getOpponent().getSymbol(), useReversiRules && game.getTurnCounter() <= 4);

        if (availableMoves.isEmpty()) {
            return null;
        }

//        double startTime = System.currentTimeMillis();
//        System.out.println("before best move " + startTime);
        this.evalCount = 0;

        int[] move = getBestMove(tiles);

//        System.out.println("stable count: " + this.evalCount);
//        double endTime = System.currentTimeMillis();
//        System.out.println("after best move: " + Arrays.toString(move) + " " + endTime);
//        System.out.println("time in ms: " + (endTime - startTime));

        return move;
    }

    public int[] getBestMove(OthelloTile[][] tiles){
        int[] bestMove = new int[]{-1, -1};
        int bestValue = Integer.MIN_VALUE;

        this.beforeTime = 0;
        this.stableTime = 0;
        this.afterTime = 0;

        byte[] avm = getAvailableMoves2(tiles, this.symbol, this.otherSymbol, false);

        List<Future<MoveEvaluation>> futures = new ArrayList<>();

        for (byte move : avm) {
            if (move == 0) break;

            int x = ((move >> 4) & 0b00001111) - 1;
            int y = (move & 0b00001111) - 1;
//            int x = move[0];
//            int y = move[1];

            OthelloTile[][] copiedBoard = copyBoard(tiles);
            copiedBoard[x][y] = symbol;
            flipTiles(copiedBoard, x, y, symbol);

            Callable<MoveEvaluation> task = () -> {
                int moveValue = minimax(copiedBoard, maxDepth, -100000, 100000, false, false);
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

//        System.out.println("before time: " + this.beforeTime);
//        System.out.println("stable time: " + this.stableTime);
//        System.out.println("after time: " + this.afterTime);

        return bestMove;
    }

    public int evaluateBoard(OthelloTile[][] tiles, int myMobility, int otherMobility){
        this.evalCount++;

        double startTimeBefore = System.nanoTime();

        int value = 0;

        if(useReversiRules){
            //als een pass een loss is:
            if(myMobility == 0){
                return -1000;
            }
        }

        int myTiles = 0;
        int otherTiles = 0;
        for(int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (tiles[col][row] == symbol) {
                    myTiles++;
                } else if (tiles[col][row] == otherSymbol) {
                    otherTiles++;
                }
            }
        }
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

            if(tiles[x][y] == symbol){
                myCorner++;
                myStableDiscs++;
                //hoef je niet te checken voor x en c want je krijgt er geen extra pluspunten voor
            }
            else if(tiles[x][y] == otherSymbol){
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

            if(tiles[x + directionX][y] == symbol){
                cPunishments++;
            }
            if(tiles[x][y + directionY] == symbol){
                cPunishments++;
            }
            if(tiles[x + directionX][y + directionY] == symbol){
                xPunishments++;
            }
        }

        int cornerDiff = myCorner - otherCorner;

        double endTimeBefore = System.nanoTime();
        this.beforeTime += endTimeBefore - startTimeBefore;

        double startTimeStable = System.nanoTime();

        //boolean array zodat je stable discs niet dubbel telt en maar 1 keer op stable hoeft te zetten.
        boolean[][] isStable = new boolean[boardSize][boardSize];

        // doe alleen zoeken naar stable pieces als de corner aan die kant van het bord behouden wordt door de speler

        //als je corner links boven hebt
        if(tiles[0][0] == symbol){

            //count stable discs upper side, left > right
            for (int col = 1; col < boardSize - 1; col++){
                if(tiles[col][0] == tiles[col - 1][0] && tiles[col][0] != OthelloTile.EMPTY){
                    if(tiles[col][0] == symbol){
                        if(!isStable[col][0]) {
                            isStable[col][0] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[col][0] == otherSymbol) {
                        if(!isStable[col][0]) {
                            isStable[col][0] = true;
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs left side, up > down
            for (int row = 1; row < boardSize - 1; row++){
                if(tiles[0][row] == tiles[0][row - 1] && tiles[0][row] != OthelloTile.EMPTY){
                    if(tiles[0][row] == symbol) {
                        if(!isStable[0][row]) {
                            isStable[0][row] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[0][row] == otherSymbol) {
                        if(!isStable[0][row]) {
                            isStable[0][row] = true;
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        //als je corner links onder hebt
        if(tiles[0][boardSize - 1] == symbol){

            //count stable discs lower side, left > right
            for (int col = 1; col < boardSize - 1; col++){
                if(tiles[col][boardSize - 1] == tiles[col - 1][boardSize - 1] && tiles[col][boardSize - 1] != OthelloTile.EMPTY){
                    if(tiles[col][boardSize - 1] == symbol) {
                        if(!isStable[col][boardSize - 1]) {
                            isStable[col][boardSize - 1] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[col][boardSize - 1] == otherSymbol) {
                        if(!isStable[col][boardSize - 1]) {
                            isStable[col][boardSize - 1] = true;
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs left side, down > up
            for (int row = boardSize - 2; row > 0; row--){
                if(tiles[0][row] == tiles[0][row + 1] && tiles[0][row] != OthelloTile.EMPTY){
                    if(tiles[0][row] == symbol) {
                        if(!isStable[0][row]) {
                            isStable[0][row] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[0][row] == otherSymbol) {
                        if(!isStable[0][row]) {
                            isStable[0][row] = true;
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        //als je corner rechts boven hebt
        if(tiles[boardSize - 1][0] == symbol){

            //count stable discs upper side, right > left
            for (int col = boardSize - 2; col > 0; col--){
                if(tiles[col][0] == tiles[col + 1][0] && tiles[col][0] != OthelloTile.EMPTY){
                    if(tiles[col][0] == symbol) {
                        if (!isStable[col][0]) {
                            isStable[col][0] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[col][0] == otherSymbol) {
                        if(!isStable[col][0]) {
                            isStable[col][0] = true;
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs right side, up > down
            for (int row = 1; row < boardSize - 1; row++){
                if(tiles[boardSize - 1][row] == tiles[boardSize - 1][row - 1] && tiles[boardSize - 1][row] != OthelloTile.EMPTY){
                    if(tiles[boardSize - 1][row] == symbol) {
                        if(!isStable[boardSize - 1][row]) {
                            isStable[boardSize - 1][row] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[boardSize - 1][row] == otherSymbol) {
                        if(!isStable[boardSize - 1][row]) {
                            isStable[boardSize - 1][row] = true;
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        //als je corner rechts onder hebt
        if(tiles[boardSize - 1][boardSize - 1] == symbol){

            //count stable discs lower side, right > left
            for (int col = boardSize - 2; col > 0; col--){
                if(tiles[col][boardSize - 1] == tiles[col + 1][boardSize - 1] && tiles[col][boardSize - 1] != OthelloTile.EMPTY){
                    if(tiles[col][boardSize - 1] == symbol) {
                        if(!isStable[col][boardSize - 1]) {
                            isStable[col][boardSize - 1] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[col][boardSize - 1] == otherSymbol) {
                        if(!isStable[col][boardSize - 1]) {
                            isStable[col][boardSize - 1] = true;
                            otherStableDiscs++;
                        }
                    }
                } else {
                    break;
                }
            }

            //count stable discs right side, down > up
            for (int row = boardSize - 2; row > 0; row--){
                if(tiles[boardSize - 1][row] == tiles[boardSize - 1][row + 1] && tiles[boardSize - 1][row] != OthelloTile.EMPTY){
                    if(tiles[boardSize - 1][row] == symbol) {
                        if(!isStable[boardSize - 1][row]) {
                            isStable[boardSize - 1][row] = true;
                            myStableDiscs++;
                        }
                    }
                    else if (tiles[boardSize - 1][row] == otherSymbol) {
                        if(!isStable[boardSize - 1][row]) {
                            isStable[boardSize - 1][row] = true;
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
        if(boardFull(tiles)){
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

    boolean boardFull(OthelloTile[][] tiles) {
        for(int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (tiles[col][row] == OthelloTile.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public int minimax(OthelloTile[][] tiles, int depth, int alpha, int beta, boolean isMax, boolean depthIsDecreased){
        boolean boardFull =  boardFull(tiles);

        byte[] availableMovesUpcoming = null;
        byte[] availableMovesOpponent = null;

        if (isMax) {
            availableMovesUpcoming = getAvailableMoves2(tiles, this.symbol, this.otherSymbol, false);
        } else {
            availableMovesOpponent = getAvailableMoves2(tiles, this.otherSymbol, this.symbol, false);
        }


        if(boardFull || depth <= 0){
            if (!isMax) {
                availableMovesUpcoming = getAvailableMoves2(tiles, this.symbol, this.otherSymbol, false);
            } else {
                availableMovesOpponent = getAvailableMoves2(tiles, this.otherSymbol, this.symbol, false);
            }
            return evaluateBoard(tiles, availableMovesUpcoming.length, availableMovesOpponent.length);
        }

        if(isMax){

            if (availableMovesUpcoming.length > 10 && !depthIsDecreased) {
                depth--;
                depthIsDecreased = true;
            }

            int highestVal = -10000;

            if (hasMoves(availableMovesUpcoming)) {
                availableMovesOpponent = getAvailableMoves2(tiles, this.otherSymbol, this.symbol, false);
                if (hasMoves(availableMovesOpponent)) {
                    return evaluateBoard(tiles, 0, 0);
                }

                int curVal = minimax(tiles, depth-1, alpha, beta, false, depthIsDecreased);
                highestVal= Math.max(highestVal, curVal);

            }


            for (byte move : availableMovesUpcoming) {
                if (move == 0) break;

                int x = ((move >> 4) & 0b00001111) - 1;
                int y = (move & 0b00001111) - 1;

                OthelloTile[][] copiedBoard = copyBoard(tiles);
                copiedBoard[x][y] = symbol;
                flipTiles(copiedBoard, x, y, symbol);

                int curVal = minimax(copiedBoard, depth-1, alpha, beta, false, depthIsDecreased);

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

            if (hasMoves(availableMovesOpponent)) {
                availableMovesUpcoming = getAvailableMoves2(tiles, this.otherSymbol, this.symbol, false);
                if (hasMoves(availableMovesUpcoming)) {
                    return evaluateBoard(tiles, 0, 0);
                }

                int curVal = minimax(tiles, depth-1, alpha, beta, true, depthIsDecreased);
                lowestVal= Math.min(lowestVal, curVal);

            }


            for (byte move : availableMovesOpponent) {
                if (move == 0) break;

                int x = ((move >> 4) & 0b00001111) - 1;
                int y = (move & 0b00001111) - 1;

                OthelloTile[][] copiedBoard = copyBoard(tiles);
                copiedBoard[x][y] = symbol;
                flipTiles(copiedBoard, x, y, symbol);

                int curVal = minimax(copiedBoard, depth-1, alpha, beta, true, depthIsDecreased);

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

//    public static List<int[]> getAvailableMoves(OthelloTile[][] tiles, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour){
//        ArrayList<int[]> availableMoves = new ArrayList<>();
//
//        byte[] moves = getAvailableMoves2(tiles, playerSymbol, opponentSymbol, reversiFirstFour);
//
//        for (byte move : moves) {
//            if (move == 0) break;
//
//            int x = ((move >> 4) & 0b00001111) - 1;
//            int y = (move & 0b00001111) - 1;
//
//            availableMoves.add(new int[]{x, y});
//        }
//
//        return availableMoves;
//    }

//    public static List<int[]> getAvailableMoves(OthelloTile[][] tiles, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour) {
//        ArrayList<int[]> availableMoves = new ArrayList<>();
//        long addedCoords = 0;
//
////        if (reversiFirstFour) {
////            return openingAvailableMoves(tiles);
////        }
//
//        int row = 0;
//        int col = 0;
//        for (int i = 0; i < BOARD_SIZED_SQUARED; i++) {
//            if (tiles[row][col] == playerSymbol) {
//                for (int[] direction : directions) {
//                    int cX = row + direction[0];
//                    int cY = col + direction[1];
//
//                    boolean foundOpponentSymbol = false;
//                    while (cX >= 0 && cX < boardSize && cY >= 0 && cY < boardSize) {
//                        if (tiles[cX][cY] == playerSymbol) {
//                            break;
//                        }
//                        if (tiles[cX][cY] == opponentSymbol) {
//                            foundOpponentSymbol = true;
//                        }
//                        if (tiles[cX][cY] == OthelloTile.EMPTY) {
//                            if (foundOpponentSymbol) {
//                                long r = 1L << (i + 1);
//                                if ((r & addedCoords) == 0) {
//                                    addedCoords |= r;
//                                    availableMoves.add(new int[]{cX, cY});
//                                }
//                            }
//                            break;
//                        }
//                        cX += direction[0];
//                        cY += direction[1];
//                    }
//                }
//            }
//
//            col++;
//            if (col >= boardSize){
//                col = 0;
//                row++;
//            }
//        }
//
//        return availableMoves;
//    }

    public static boolean hasMoves(byte[] moves){
        if (moves == null) return false;
        return moves[0] != 0;
    }

    public static byte[] getAvailableMoves2(OthelloTile[][] tiles, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour) {
//        ArrayList<int[]> availableMoves = new ArrayList<>();
        byte[] availableMoves = new byte[BOARD_SIZED_SQUARED];
        long addedCoords = 0;

//        if (reversiFirstFour) {
//            return openingAvailableMoves(tiles);
//        }

        int row = 0;
        int col = 0;
        int index = 0;
        for (int i = 0; i < BOARD_SIZED_SQUARED; i++) {
            if (tiles[row][col] == playerSymbol) {
                for (int[] direction : directions) {
                    int cX = row + direction[0];
                    int cY = col + direction[1];

                    boolean foundOpponentSymbol = false;
                    while (cX >= 0 && cX < boardSize && cY >= 0 && cY < boardSize) {
                        if (tiles[cX][cY] == playerSymbol) {
                            break;
                        } else if (tiles[cX][cY] == opponentSymbol) {
                            foundOpponentSymbol = true;
                        } else if (tiles[cX][cY] == OthelloTile.EMPTY) {
                            if (foundOpponentSymbol) {
                                long r = 1L << (i + 1);
                                if ((r & addedCoords) == 0) {
                                    addedCoords |= r;
                                    availableMoves[index] = (byte) (((cX + 1) << 4) | (cY + 1));
//                                    System.out.println(availableMoves[index] + "_" + tiles[cX][cY]);
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

            col++;
            if (col >= boardSize){
                col = 0;
                row++;
            }
        }

        return availableMoves;
    }

    private static final int[][] centerTiles = new int[][]{
            {3, 3}, {3, 4}, {4, 3}, {4, 4}
    };

    public static List<int[]> openingAvailableMoves(OthelloTile[][] tiles) {
        List<int[]> moves = new ArrayList<>();

        for (int[] coord : centerTiles) {
            OthelloTile tile = tiles[coord[0]][coord[1]];
            if (tile == OthelloTile.EMPTY) {
                moves.add(coord);
            }
        }

        return moves;
    }

    public static OthelloTile[][] copyBoard(OthelloTile[][] tiles) {
        OthelloTile[][] copy = new OthelloTile[8][8];

        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                copy[row][col] = tiles[row][col];
            }
        }

        return copy;
    }

    public void flipTiles(OthelloTile[][] tiles, int xO, int yO, OthelloTile symbol) {
        ArrayList<Integer[]> tilesToFlip = new ArrayList<>();
        int[][] dirs = {
                {1, 0},   // down
                {-1, 0},  // up
                {0, 1},   // right
                {0, -1},  // left
                {-1, -1}, // up-left
                {-1, 1},  // up-right
                {1, -1},  // down-left
                {1, 1}    // down-right
        };

        for (int[] d : dirs) {
            int x = xO;
            int y = yO;

            while (true) {
                x += d[0];
                y += d[1];

                if (x < 0 || x >= tiles.length || y < 0 || y >= tiles[0].length) {
                    tilesToFlip.clear();
                    break;
                }

                OthelloTile current = tiles[x][y];

                if (current == OthelloTile.EMPTY){
                    tilesToFlip.clear();
                    break;
                }

                if (current != symbol){
                    tilesToFlip.add(new Integer[]{x,y});
                }

                if (current == symbol) {
                    for(Integer[] t : tilesToFlip){
                        tiles[t[0]][t[1]] = symbol;
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
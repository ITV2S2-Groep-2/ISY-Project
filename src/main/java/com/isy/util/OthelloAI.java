package com.isy.util;

import com.isy.game.Board;
import com.isy.game.Game;
import com.isy.game.othello.OthelloGame;
import com.isy.game.othello.OthelloTile;
import com.isy.game.othello.OthelloUtils;
import com.isy.game.player.Player;
import com.isy.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OthelloAI extends Player<OthelloTile> {

    String parentName = "";
    double mobility_diffWeight = 5;
    double corner_diffWeight = 25;
    double stability_diffWeight = 10;
    double disc_diffWeight = 1;
    int maxDepth = 2;

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

        double startTime = System.currentTimeMillis();
        System.out.println("before best move " + startTime);

        int[] move = getBestMove(tiles);

        double endTime = System.currentTimeMillis();
        System.out.println("after best move: " + Arrays.toString(move) + " " + endTime);
        System.out.println("time in ms: " + (endTime - startTime));

        return move;
    }

    public int[] getBestMove(OthelloTile[][] tiles){
        int[] bestMove = new int[]{-1, -1};
        int bestValue = Integer.MIN_VALUE;

        List<int[]> avm = this.availableMoves(tiles, this.symbol, this.otherSymbol);

        for (int[] move : avm) {
            tiles[move[0]][move[1]] = symbol;
            int moveValue = minimax(tiles, maxDepth, -100000, 100000, false);
            tiles[move[0]][move[1]] = OthelloTile.EMPTY;
            if(moveValue > bestValue){
                bestMove[0] = move[0];
                bestMove[1] = move[1];
                bestValue = moveValue;
            }
        }

        return bestMove;
    }

    public int evaluateBoard(OthelloTile[][] tiles){
        int value = 0;

        List<int[]> myAvailableMoves = availableMoves(tiles, symbol, otherSymbol);
        if(useReversiRules){
            //als een pass een loss is:
            if(myAvailableMoves.isEmpty()){
                return -1000;
            }
        }
        int myMobility = myAvailableMoves.size();
        int otherMobility = availableMoves(tiles, otherSymbol, symbol).size();

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

        int totalCornersTaken = myCorner + otherCorner;
        int cornerDiff = myCorner - otherCorner;

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
                    if(!isStable[col][0]) {
                        isStable[col][0] = true;
                        myStableDiscs++;
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

        int totalStableDiscs = myStableDiscs + otherStableDiscs;
        int stableDiscDiff = myStableDiscs - otherStableDiscs;

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

        value = (int)((mobilityDiff * mobility_diffWeight * mobilityPhase) +
                (cornerDiff * corner_diffWeight) +
                (stableDiscDiff * stability_diffWeight) +
                (discDiff * disc_diffWeight * discPhase) -
                (cPunishments + xPunishments * -corner_diffWeight));


        var berekening = myMobility +
                otherMobility +
                mobilityDiff +
                myTiles +
                otherTiles +
                totalTiles +
                phase +
                discDiff +
                myStableDiscs +
                otherStableDiscs +
                myCorner +
                otherCorner +
                totalCornersTaken +
                cornerDiff +
                totalStableDiscs +
                stableDiscDiff +
                cPunishments +
                xPunishments;

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

    List<int[]> availableMoves(OthelloTile[][] tiles, OthelloTile mySymbol, OthelloTile opponentSymbol){
        return getAvailableMoves(tiles, mySymbol, opponentSymbol, false);
    }

    public int minimax(OthelloTile[][] tiles, int depth, int alpha, int beta, boolean isMax){
        int boardValue = evaluateBoard(tiles);

        boolean winorloss = Math.abs(boardValue) > 500;
        if(winorloss || depth == 0){
            return boardValue;
        }

        if(isMax){
            int highestVal = -10000;

            List<int[]> avm = this.availableMoves(tiles, this.symbol, this.otherSymbol);

            for (int[] move : avm) {
                tiles[move[0]][move[1]] = symbol;
                int curVal = minimax(tiles, depth-1, alpha, beta, false);
                tiles[move[0]][move[1]] = OthelloTile.EMPTY;
                highestVal= Math.max(highestVal, curVal);
                alpha = Math.max(alpha, curVal);
                if(beta <= alpha){
                    break;
                }
            }

            return highestVal;
        }
        else{
            int lowestVal = 10000;

            List<int[]> avm = this.availableMoves(tiles, this.otherSymbol, this.symbol);

            for (int[] move : avm) {
                tiles[move[0]][move[1]] = symbol;
                int curVal = minimax(tiles, depth-1, alpha, beta, true);
                tiles[move[0]][move[1]] = OthelloTile.EMPTY;
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


    public static List<int[]> getAvailableMoves(OthelloTile[][] tiles, OthelloTile playerSymbol, OthelloTile opponentSymbol, boolean reversiFirstFour) {
        ArrayList<int[]> availableMoves = new ArrayList<>();

        if (reversiFirstFour) {
            return openingAvailableMoves(tiles);
        }

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (tiles[row][col] == playerSymbol) {


                    for (int[] direction : directions) {
                        int cX = row + direction[0];
                        int cY = col + direction[1];

                        boolean foundOpponentSymbol = false;
                        while (cX >= 0 && cX < boardSize && cY >= 0 && cY < boardSize) {
                            if (tiles[cX][cY] == playerSymbol) {
                                break;
                            }
                            if (tiles[cX][cY] == opponentSymbol) {
                                foundOpponentSymbol = true;
                            }
                            if (tiles[cX][cY] == OthelloTile.EMPTY) {
                                if (foundOpponentSymbol) {
                                    addUniqueCoords(availableMoves, new int[]{cX, cY});
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

    public static List<int[]> openingAvailableMoves(OthelloTile[][] tiles) {
        List<int[]> moves = new ArrayList<>();
        int[][] centerTiles = new int[][]{
                {3, 3}, {3, 4}, {4, 3}, {4, 4}
        };

        for (int[] coord : centerTiles) {
            OthelloTile tile = tiles[coord[0]][coord[1]];
            if (tile == OthelloTile.EMPTY) {
                moves.add(coord);
            }
        }

        return moves;
    }
}
package com.isy;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        // TIJDELIJKE WERKENDE SPELER TEGEN SPELER SIMULATIE

        HumanPlayer speler1 = new HumanPlayer("Jantje", Tile.X, scanner);
        HumanPlayer speler2 = new HumanPlayer("Pietje", Tile.O, scanner);

        Player currentPlayer = speler1;

        while (true) {
            System.out.println(board);

            int[] move = currentPlayer.getMove(board);
            boolean validMove = board.setTile(move[0], move[1], currentPlayer.getSymbol());

            if(!validMove){
                System.out.println("Dit vakje is al bezet!");
                continue;
            }

            if (board.checkWin(move[0], move[1], currentPlayer)){
                System.out.println(currentPlayer.getName() + " heeft gewonnen!");
                break;
            }

            if(currentPlayer == speler1){
                currentPlayer = speler2;
            } else {
                currentPlayer = speler1;
            }
        }

    }
}
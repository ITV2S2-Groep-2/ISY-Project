package com.isy;

import java.util.Scanner;

public class HumanPlayer extends Player{

    public HumanPlayer(String name, Tile symbol){
        super(name, symbol);
    }

//    @Override
//    public int[] getMove(Board board) {
//        System.out.println(getName() + " (" +  getSymbol() + ")" + " geef het x-coördinaat (1 t/m 3): ");
//        int x = scanner.nextInt() - 1;
//
//        System.out.println("Geef het y coördinaat (1 t/m 3): ");
//        int y = scanner.nextInt() - 1;
//
//        return new int[]{x, y};
//    }

    @Override
    public int[] getMove(Board board) {
        return new int[]{0,0};
    }
}

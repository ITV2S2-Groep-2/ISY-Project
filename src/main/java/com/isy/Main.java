package com.isy;

public class Main {
    public static void main(String[] args) {

//        Board board = new Board();
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println(board);
//        System.out.println("X: ");
//
//        boolean xOrY = true;
//        int x = 0, y = 0;
//        while (scanner.hasNext()){
//            String next = scanner.next();
//
//            if (next.equalsIgnoreCase("exit()")){
//                scanner.close();
//                break;
//            }else if (xOrY) {
//                try {
//                    x = Integer.parseInt(next);
//                }catch (NumberFormatException e){
//                    System.out.println("Unexpected input: '" + next + "'");
//                    continue;
//                }
//
//                if (x > 3 || x < 1){
//                    System.out.println("Input: '" + next + "' needs to be between 1 and 3");
//                    continue;
//                }
//
//                x--;
//            }else{
//                try {
//                    y = Integer.parseInt(next);
//                }catch (NumberFormatException e){
//                    System.out.println("Unexpected input: '" + next + "'");
//                    continue;
//                }
//
//                if (y > 3 || y < 1){
//                    System.out.println("Input: '" + next + "' needs to be between 1 and 3");
//                    continue;
//                }
//
//                y--;
//            }
//
//            xOrY = !xOrY;
//
//            if (xOrY){
//                board.setTile(x, y, Tile.X);
//
//                System.out.println(board);
//
//                System.out.println("X: ");
//            }else{
//                System.out.println("Y: ");
//            }
//        }
    }
}
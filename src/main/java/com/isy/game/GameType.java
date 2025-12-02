package com.isy.game;

import com.isy.game.ticTacToe.TicTacToeGame;

public enum GameType {
    TICTACTOE ("tic-tac-toe"), OTHELLO ("othello");

    public final String label;

    GameType(String label) {
        this.label = label;
    }

    public static GameType fromLabel(String label) {
        return switch (label) {
            case "tic-tac-toe" -> GameType.TICTACTOE;
            case "othello" -> GameType.OTHELLO;
            default -> null;
        };
    }

    public static Class<? extends Game<?>> getClass(GameType type) {
        return switch (type) {
            case TICTACTOE -> TicTacToeGame.class;
            case OTHELLO ->
                //TODO: update
                    TicTacToeGame.class;
        };
    }

}

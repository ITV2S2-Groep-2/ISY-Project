package com.isy.game;

import com.isy.game.ticTacToe.TicTacToeGame;

public enum GameType {
    TICTACTOE ("tic-tac-toe"), OTHELLO ("othello");

    public final String label;

    GameType(String label) {
        this.label = label;
    }

    static public GameType fromLabel(String label) {
        switch (label) {
            case "tic-tac-toe":
                return GameType.TICTACTOE;
            case "othello":
                return GameType.OTHELLO;
            default:
                return null;
        }
    }

    static public Class<? extends Game> getClass(GameType type) {
        switch (type) {
            case TICTACTOE:
                return TicTacToeGame.class;
            case OTHELLO:
                //TODO: update
                return TicTacToeGame.class;
            default:
                return null;
        }
    }

}

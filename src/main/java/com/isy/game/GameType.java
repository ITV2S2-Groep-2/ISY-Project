package com.isy.game;

import com.isy.game.othello.*;
import com.isy.game.player.Player;
import com.isy.game.ticTacToe.*;
import com.isy.game.othello.OthelloAIPlayer;

public enum GameType {
    TICTACTOE ("tic-tac-toe"), OTHELLO ("reversi");

    public final String label;

    GameType(String label) {
        this.label = label;
    }

    public static GameType fromLabel(String label) {
        return switch (label) {
            case "tic-tac-toe" -> GameType.TICTACTOE;
            case "reversi" -> GameType.OTHELLO;
            default -> null;
        };
    }

    public static Class<? extends Game<?>> getClass(GameType type) {
        return switch (type) {
            case TICTACTOE -> TicTacToeGame.class;
            case OTHELLO -> OthelloGame.class;
        };
    }

    public static Class<? extends Player<?>> getPlayerClass(PlayerType playerType, GameType gameType) {
        switch (playerType) {
            case HUMAN -> {
                return switch (gameType) {
                    case TICTACTOE -> TicTacToeHumanPlayer.class;
                    case OTHELLO -> OthelloHumanPlayer.class;
                    default -> null; //TODO: default human player class -> no difference between the games
                };
            }
            case AI -> {
                return switch (gameType) {
                    case TICTACTOE -> TicTacToeAIPlayer.class;
                    case OTHELLO -> OthelloAIPlayer.class;
                    default -> null;
                };
            }

            case REMOTE -> {
                return switch (gameType) {
                    case TICTACTOE -> TicTacToeRemotePlayer.class;
                    case OTHELLO -> OthelloRemotePlayer.class;
                    default -> null;
                };
            }
            default -> {
                return null;
            }
        }
    }

    public static Enum<? extends ITile> getPlayerTileValue(int player, GameType gameType) {
        switch (gameType) {
            case TICTACTOE -> {
                return switch (player) {
                    case 0 -> TicTacToeTile.X;
                    case 1 -> TicTacToeTile.O;
                    default -> null;
                };
            }
            case OTHELLO -> {
                return switch (player) {
                    case 0 -> OthelloTile.PLAYER_1;
                    case 1 -> OthelloTile.PLAYER_2;
                    default -> null;
                };
            }
        }
        return null;
    }

    public static int[] getBoardDimensionsByGameType(GameType gameType) {
        return switch (gameType) {
            case TICTACTOE -> new int[]{3, 3};
            case OTHELLO -> new int[]{8, 8};
            default -> new int[]{3, 3};
        };
    }
}

package com.isy.game;

public enum PlayerType {
    HUMAN ("Human"), AI ("AI"), REMOTE ("Remote");

    public final String label;

    private PlayerType(String label) {
        this.label = label;
    }

    static public PlayerType fromLabel(String label) {
        switch (label) {
            case "Human":
                return PlayerType.HUMAN;
            case "AI":
                return PlayerType.AI;
            case "Remote":
                return PlayerType.REMOTE;
            default:
                return null;
        }
    }

}

package com.isy.game;

public enum PlayerType {
    HUMAN ("Human"), AI ("AI"), REMOTE ("Remote"), RANDOMAI("RandomAI");

    public final String label;

    private PlayerType(String label) {
        this.label = label;
    }

    public static PlayerType fromLabel(String label) {
        return switch (label) {
            case "Human" -> PlayerType.HUMAN;
            case "AI" -> PlayerType.AI;
            case "Remote" -> PlayerType.REMOTE;
            case "RandomAI" -> PlayerType.RANDOMAI;
            default -> null;
        };
    }

}

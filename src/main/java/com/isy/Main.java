package com.isy;

import com.isy.game.Game;
import com.isy.game.GameType;
import com.isy.game.PlayerType;
import com.isy.gui.Window;
import com.isy.util.GameCreator;
import com.isy.util.ResultWriter;


public class Main {
    public static Window window;
    public static Game runningGame = null;

    public static void main(String[] args) {
        String player1Name = "Base AI";
        String player2Name = "Test AI";
        if (args.length == 3) { // moet aangepast worden misschien in de toekomst dat je de settings door kan geven
            player1Name = args[1];
            player2Name = args[2];
        }
        GameCreator gameCreator = GameCreator.createNewInstance(GameType.OTHELLO);
        gameCreator.setPlayers(PlayerType.AI, PlayerType.AI, player1Name, player2Name);
        for (int i = 0; i < 3; i++) {
            gameCreator.startLocalGame();
        }

        ResultWriter.write("first.json");
        // window = new Window();
    }

}
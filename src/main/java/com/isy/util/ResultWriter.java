package com.isy.util;

import com.isy.game.player.Player;
import com.isy.game.ticTacToe.TicTacToeAiPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class ResultWriter {

    public static int winPlayer1 = 0;
    public static int winPlayer2 = 0;
    public static Player player1;
    public static Player player2;
    public static void write(String filename) {
        JSONArray players = new JSONArray();

        JSONObject winner = new JSONObject();
        winner.put("wons", winPlayer1);
        winner.put("name", player1.getName());
        winner.put("symbol", player1.getSymbol());

        JSONObject loser = new JSONObject();
        loser.put("wons", winPlayer2);
        loser.put("name", player2.getName());
        loser.put("symbol", player2.getSymbol());

        players.put(winner);
        players.put(loser);

        JSONObject root = new JSONObject();
        root.put("players", players);

        try (FileWriter file = new FileWriter(filename)) {
            file.write(root.toString(4));
            System.out.println("JSON Object write to a File successfully");
            System.out.println("JSON Object: " + root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

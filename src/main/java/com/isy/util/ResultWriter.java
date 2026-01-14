package com.isy.util;

import com.isy.game.player.Player;
import com.isy.game.ticTacToe.TicTacToeAiPlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ResultWriter {

    public static HashMap<String, JSONObject> modelMap = new HashMap<>();

    public static void addModel(OthelloAI model) {

        JSONObject settings = new JSONObject();
        settings.put("Parent", model.getParent());
        settings.put("mobility_diffWeight", model.getMobility_diffWeight());
        settings.put("corner_diffWeight", model.getCorner_diffWeight());
        settings.put("stability_diffWeight", model.getStability_diffWeight());
        settings.put("disc_diffWeight", model.getDisc_diffWeight());
        settings.put("maxDepth", model.getMaxDepth());

        JSONObject stats = new JSONObject();
        stats.put("wins", 0);
        stats.put("losses", 0);
        stats.put("draws", 0);

        JSONObject time = new JSONObject();
        time.put("AVG Time", 0.0);
        time.put("MIN Time", 0.0);
        time.put("MAX Time", 0.0);
        time.put("Total Time", 0.0);

        JSONObject JsonModel = new JSONObject();
        JsonModel.put("settings", settings);
        JsonModel.put("stats", stats);
        JsonModel.put("time", time);

        modelMap.put(model.getName(), JsonModel);
    }

    public static void addWin(String modelName) {
        JSONObject model = modelMap.get(modelName);
        JSONObject stats = model.getJSONObject("stats");
        stats.put("wins", stats.getInt("wins") + 1);
    }

    public static void addLoss(String modelName) {
        JSONObject model = modelMap.get(modelName);
        JSONObject stats = model.getJSONObject("stats");
        stats.put("losses", stats.getInt("losses") + 1);
    }
    public static void addDraw(String modelName) {
        JSONObject model = modelMap.get(modelName);
        JSONObject stats = model.getJSONObject("stats");
        stats.put("draws", stats.getInt("draws") + 1);
    }

    public static void addTime(String modelName, long avgTime, long minTime, long maxTime, long totalTime) {
        JSONObject model = modelMap.get(modelName);
        JSONObject time = model.getJSONObject("time");
        time.put("AVG Time", avgTime);
        time.put("MIN Time", minTime);
        time.put("MAX Time", maxTime);
        time.put("Total Time", totalTime);
    }
    public static void writeAll(String filename) {
        JSONObject root = ModelFileReader.Read(filename);
        if (root == null) {
            root = new JSONObject();
        }
        for (var entry : modelMap.entrySet()) {
            root.put(entry.getKey(), entry.getValue());
        }
        try (FileWriter file = new FileWriter(filename)) {
            file.write(root.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void writeTop5(String filename) {
        JSONObject root = new JSONObject();

        ArrayList<Map.Entry<String, JSONObject>> list = new ArrayList<>(modelMap.entrySet());

        list.sort((element1, element2) ->
                element2.getValue().getJSONObject("stats").getInt("wins") -
                        element1.getValue().getJSONObject("stats").getInt("wins")); // heb ik met behulp van AI geschreven
        for (int i = 0; i < 5; i++ ) {
            root.put(list.get(i).getKey(), list.get(i).getValue());
        }

        try (FileWriter file = new FileWriter(filename)) {
            file.write(root.toString(4));
            System.out.println("JSON Object write to a File successfully");
            System.out.println("JSON Object: " + root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

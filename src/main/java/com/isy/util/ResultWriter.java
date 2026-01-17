package com.isy.util;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultWriter {

    public static HashMap<String, JSONObject> modelMap = new HashMap<>();

    public static void addModel(OthelloAI model) {

        JSONObject settings = new JSONObject();
        settings.put("parent", model.getParent());
        settings.put("mobility_diff_weight", model.getMobility_diffWeight());
        settings.put("corner_diff_weight", model.getCorner_diffWeight());
        settings.put("stability_diff_weight", model.getStability_diffWeight());
        settings.put("disc_diff_weight", model.getDisc_diffWeight());
        settings.put("max_depth", model.getMaxDepth());

        JSONObject stats = new JSONObject();
        stats.put("wins", 0);
        stats.put("losses", 0);
        stats.put("draws", 0);
        stats.put("top_5_count", 0);

        JSONObject time = new JSONObject();
        time.put("avg_time", 0);
        time.put("min_time", 0);
        time.put("max_time", 0);
        time.put("total_time", 0);

        time.put("avg_move_time", 0);
        time.put("min_move_time", 0);
        time.put("max_move_time", 0);
        time.put("total_move_time", 0);


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

    public static void addTime(String modelName, long avgTime, long minTime, long maxTime, long totalTime, long avgMoveTime, long minMoveTime, long maxMoveTime, long totalMoveTime) {
        JSONObject model = modelMap.get(modelName);
        JSONObject time = model.getJSONObject("time");
        time.put("avg_time", avgTime);
        time.put("min_time", minTime);
        time.put("max_time", maxTime);
        time.put("total_time", totalTime);

        time.put("avg_move_time", avgMoveTime);
        time.put("min_move_time", minMoveTime);
        time.put("max_move_time", maxMoveTime);
        time.put("total_move_time", totalMoveTime);

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
    public static JSONObject getTop5() {
        JSONObject root = new JSONObject();

        ArrayList<Map.Entry<String, JSONObject>> list = new ArrayList<>(modelMap.entrySet());

        list.sort((element1, element2) ->
                element2.getValue().getJSONObject("stats").getInt("wins") -
                        element1.getValue().getJSONObject("stats").getInt("wins")); // heb ik met behulp van AI geschreven

        JSONObject history = ModelFileReader.Read("history.json");

        for (int i = 0; i < 5; i++ ) {

            if (history != null) {
                String parent = list.get(i).getValue().getJSONObject("settings").getString("parent");
                JSONObject parentObj = history.getJSONObject(parent);

                JSONObject parentSettings = parentObj.getJSONObject("settings");
                JSONObject newSettings = list.get(i).getValue().getJSONObject("settings");
                if (
                        (Math.abs(parentSettings.getDouble("mobility_diff_weight") - newSettings.getDouble("mobility_diff_weight")) < 0.0001)
                        && (Math.abs(parentSettings.getDouble("corner_diff_weight") - newSettings.getDouble("corner_diff_weight")) < 0.001)
                        && (Math.abs(parentSettings.getDouble("stability_diff_weight") - newSettings.getDouble("stability_diff_weight")) < 0.001)
                        && (Math.abs(parentSettings.getDouble("disc_diff_weight") - newSettings.getDouble("disc_diff_weight")) < 0.001)
                        && (Math.abs(parentSettings.getInt("max_depth") - newSettings.getInt("max_depth")) < 0.001)
                ) {
                    int parentTop5Count = parentObj.getJSONObject("stats").getInt("top_5_count");
                    list.get(i).getValue().getJSONObject("stats").put("top_5_count", parentTop5Count + 1);
                } else {
                    list.get(i).getValue().getJSONObject("stats").put("top_5_count", 1);
                }

            } else {
                list.get(i).getValue().getJSONObject("stats").put("top_5_count", 1);
            }

            root.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return root;
    }
    public static void writeTop5(String filename) {

        JSONObject root = getTop5();
        try (FileWriter file = new FileWriter(filename)) {
            file.write(root.toString(4));
            System.out.println("JSON Object write to a File successfully");
            System.out.println("JSON Object: " + root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

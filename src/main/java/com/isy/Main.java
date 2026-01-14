package com.isy;

import com.isy.game.Game;
import com.isy.game.GameType;
import com.isy.game.ITile;
import com.isy.game.PlayerType;
import com.isy.game.othello.OthelloGame;
import com.isy.game.othello.OthelloTile;
import com.isy.game.player.Player;
import com.isy.game.ticTacToe.TicTacToeTile;
import com.isy.gui.Window;
import com.isy.util.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main {
    public static Window window;
    public static Game runningGame = null;

    public static void main(String[] args) {
        String player1Name = "Base AI";
        String player2Name = "Test AI";
        if (args.length == 3) { // moet aangepast worden misschien in de toekomst
            player1Name = args[1];
            player2Name = args[2];
        }
        //'maken' van een model kan zijn dat je een record hebt waarin je de variabelen opslaat
        // en als je de game speelt geef je ze door wanneer je de ai player maakt voor het spelen van de game

        JSONObject topmodel = ModelFileReader.Read("topmodel.json");
        // depth met cap 5 afronden
        ArrayList<OthelloAI> models = new ArrayList<OthelloAI>();
        if (topmodel == null) {
            OthelloAI baseModel = new OthelloAI("BASEMODEL", OthelloTile.PLAYER_1, null);
            //
            for (int i = 0; i < 23; i++) {
                char[] PlusOrMinus = {'+', '-'};
                Random r = new Random();
                double randomValueObWeight = baseModel.getMobility_diffWeight() * (r.nextDouble(5, 20) / 100);
                double randomValueCornerWeight = baseModel.getCorner_diffWeight() * (r.nextDouble(5, 20) / 100);
                double randomValueStabilityWeight = baseModel.getStability_diffWeight() * (r.nextDouble(5, 20) / 100);
                double randomValueDiscWeight = baseModel.getDisc_diffWeight() * (r.nextDouble(5, 20) / 100);
                double randomValueMaxDepth = baseModel.getMaxDepth() * (r.nextInt(5, 20) / 100.0);


                randomValueObWeight = (PlusOrMinus[r.nextInt(2)] == '+') ? baseModel.getMobility_diffWeight() + randomValueObWeight : baseModel.getMobility_diffWeight() - randomValueObWeight;
                randomValueCornerWeight = (PlusOrMinus[r.nextInt(2)] == '+') ? baseModel.getCorner_diffWeight() + randomValueCornerWeight : baseModel.getCorner_diffWeight() - randomValueCornerWeight;
                randomValueStabilityWeight = (PlusOrMinus[r.nextInt(2)] == '+') ? baseModel.getStability_diffWeight() + randomValueStabilityWeight : baseModel.getStability_diffWeight() - randomValueStabilityWeight;
                randomValueDiscWeight = (PlusOrMinus[r.nextInt(2)] == '+') ? baseModel.getDisc_diffWeight() + randomValueDiscWeight : baseModel.getDisc_diffWeight() - randomValueDiscWeight;
                randomValueMaxDepth = (PlusOrMinus[r.nextInt(2)] == '+') ? baseModel.getMaxDepth() + randomValueMaxDepth : baseModel.getMaxDepth() - randomValueMaxDepth;


                double roundObWeight = Math.round(randomValueObWeight * 100.0) / 100.0;
                double roundCornerWeight = Math.round(randomValueCornerWeight * 100.0) / 100.0;
                double roundStabilityWeight = Math.round(randomValueStabilityWeight * 100.0) / 100.0;
                double roundValueDiscWeight = Math.round(randomValueDiscWeight * 100.0) / 100.0;
                int roundMaxDepth =  (int) Math.round(randomValueMaxDepth);

                OthelloAI model = new OthelloAI("MODEL_" + i, OthelloTile.PLAYER_1, null, roundObWeight, roundCornerWeight, roundStabilityWeight, roundValueDiscWeight, roundMaxDepth, baseModel.getName());
                models.add(model);
            }
        }
        else {
            // nu nog niks
        }
//                for topmodel in file
//                maak 4 variaties van model
//                // zet bij de betreffende variatie de naam van de parent
//                maak list van modellen

        //zo kom je uit op 25 modellen totaal

        for( OthelloAI model : models) {
            long totalTime = 0;
            long minTime = Long.MAX_VALUE;
            long maxTime = 0;
            ResultWriter.addModel(model);
            for (int i = 0; i < 19; i++ ){
                long startTime =  System.nanoTime();
                OthelloGame game = new OthelloGame(new Player[]{model, new OthelloRandomAI("RandomAI", OthelloTile.PLAYER_2, null)});
                game.run();
                long endTime = System.nanoTime();
                long realTime = endTime - startTime;
                if (realTime < minTime) {
                    minTime = realTime;
                }
                if (realTime > maxTime) {
                    maxTime = realTime;
                }
                totalTime += realTime;
            }
            long avgTime = totalTime / 20;
            ResultWriter.addTime(model.getName(),avgTime, minTime, maxTime, totalTime);
        }
        System.out.println("Writing");
        ResultWriter.writeAll("history.json");
        ResultWriter.writeTop5("top5.json");


        // window = new Window();

    }

}
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
import java.util.UUID;


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

        JSONObject topmodel = ModelFileReader.Read("top5.json");
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

                UUID id = UUID.randomUUID();
                String modelId = id.toString();

                OthelloAI model = new OthelloAI(modelId, OthelloTile.PLAYER_1, null, roundObWeight, roundCornerWeight, roundStabilityWeight, roundValueDiscWeight, roundMaxDepth, baseModel.getName());
                models.add(model);
            }
        }
        else {
            for (String baseModelName : topmodel.keySet()) {
                JSONObject modelJson = topmodel.getJSONObject(baseModelName);
                JSONObject settings = modelJson.getJSONObject("settings");
                double BaseMobility_diffWeight = settings.getDouble("mobility_diffWeight");
                double BaseCorner_diffWeight = settings.getDouble("corner_diffWeight");
                double BaseStability_diffWeight = settings.getDouble("stability_diffWeight");
                double BaseDisc_diffWeight = settings.getDouble("disc_diffWeight");
                int BaseMaxDepth = settings.getInt("maxDepth");
                String parent = settings.getString("Parent");
                OthelloAI baseModel = new OthelloAI(baseModelName, OthelloTile.PLAYER_1, null, BaseMobility_diffWeight,BaseCorner_diffWeight, BaseStability_diffWeight,  BaseDisc_diffWeight, BaseMaxDepth, parent );
                models.add(baseModel);
                for (int i = 0; i < 3; i++) {
                    char[] PlusOrMinus = {'+', '-'};
                    Random r = new Random();
                    double randomValueObWeight = BaseMobility_diffWeight * (r.nextDouble(5, 20) / 100);
                    double randomValueCornerWeight = BaseCorner_diffWeight * (r.nextDouble(5, 20) / 100);
                    double randomValueStabilityWeight = BaseStability_diffWeight * (r.nextDouble(5, 20) / 100);
                    double randomValueDiscWeight = BaseDisc_diffWeight * (r.nextDouble(5, 20) / 100);
                    double randomValueMaxDepth =BaseMaxDepth * (r.nextInt(5, 20) / 100.0);


                    randomValueObWeight = (PlusOrMinus[r.nextInt(2)] == '+') ?BaseMobility_diffWeight + randomValueObWeight : BaseMobility_diffWeight - randomValueObWeight;
                    randomValueCornerWeight = (PlusOrMinus[r.nextInt(2)] == '+') ? BaseCorner_diffWeight + randomValueCornerWeight :BaseCorner_diffWeight - randomValueCornerWeight;
                    randomValueStabilityWeight = (PlusOrMinus[r.nextInt(2)] == '+') ? BaseStability_diffWeight + randomValueStabilityWeight : BaseStability_diffWeight - randomValueStabilityWeight;
                    randomValueDiscWeight = (PlusOrMinus[r.nextInt(2)] == '+') ? BaseDisc_diffWeight + randomValueDiscWeight : BaseDisc_diffWeight - randomValueDiscWeight;
                    randomValueMaxDepth = (PlusOrMinus[r.nextInt(2)] == '+') ? BaseMaxDepth + randomValueMaxDepth : BaseMaxDepth - randomValueMaxDepth;


                    double roundObWeight = Math.round(randomValueObWeight * 100.0) / 100.0;
                    double roundCornerWeight = Math.round(randomValueCornerWeight * 100.0) / 100.0;
                    double roundStabilityWeight = Math.round(randomValueStabilityWeight * 100.0) / 100.0;
                    double roundValueDiscWeight = Math.round(randomValueDiscWeight * 100.0) / 100.0;
                    int roundMaxDepth =  (int) Math.round(randomValueMaxDepth);
                    UUID id = UUID.randomUUID();
                    String modelId = id.toString();

                    OthelloAI model = new OthelloAI(modelId, OthelloTile.PLAYER_1, null, roundObWeight, roundCornerWeight, roundStabilityWeight, roundValueDiscWeight, roundMaxDepth, baseModelName);
                    models.add(model);
                }


            }
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
            for (int i = 0; i < 2; i++ ){
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
            long avgTime = totalTime / 3;
            ResultWriter.addTime(model.getName(),avgTime, minTime, maxTime, totalTime);
        }
        // krijg nu een error maar starks als ik die top5 maak op bassis van models uit het bestand werkt het wel. (verwijder top5.json)
        System.out.println("Writing");
        ResultWriter.writeAll("history.json");
        ResultWriter.writeTop5("top5.json");


        // window = new Window();

    }

}
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
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;


public class Main {
    public static Window window;
    public static Game runningGame = null;

    public static final int MAXDEPTH = 8;
    public static final int MINDEPTH = 5;

    public static void main(String[] args) {
        long start = System.nanoTime();

        JSONObject topmodel = ModelFileReader.Read("top5.json");
        // depth met cap 5 afronden
        ArrayList<OthelloAI> models = new ArrayList<OthelloAI>();
        Random r = new Random();
        if (topmodel == null) {
            OthelloAI baseModel = new OthelloAI("BASEMODEL", OthelloTile.PLAYER_1, null);
            models.add(baseModel);

            double BaseMobility_diffWeight = baseModel.getMobility_diffWeight();
            double BaseCorner_diffWeight = baseModel.getCorner_diffWeight();
            double BaseStability_diffWeight = baseModel.getStability_diffWeight();
            double BaseDisc_diffWeight = baseModel.getDisc_diffWeight();
            int BaseMaxDepth = baseModel.getMaxDepth();
            String parent = baseModel.getName();

            for (int i = 0; i < 23; i++) {
                OthelloAI newModel = newModel(BaseMobility_diffWeight, BaseCorner_diffWeight, BaseStability_diffWeight, BaseDisc_diffWeight, BaseMaxDepth, parent);
                models.add(newModel);
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

                for (int i = 0; i < 4; i++) {
                    OthelloAI newModel = newModel(BaseMobility_diffWeight, BaseCorner_diffWeight, BaseStability_diffWeight, BaseDisc_diffWeight, BaseMaxDepth, baseModelName);
                    models.add(newModel);
                }


            }
            // nu nog niks
        }

        List<ModelRunner> modelRunnerList = new ArrayList<>();
        for( OthelloAI model : models) {
            ModelRunner runner = new ModelRunner(model);
            modelRunnerList.add(runner);
            new Thread(runner).start();
        }

        boolean allFinished = false;

        while (!allFinished){
            allFinished = true;

            for (ModelRunner modelRunner : modelRunnerList) {
                if (!modelRunner.finished) {
                    allFinished = false;
                    break;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (ModelRunner modelRunner : modelRunnerList) {
            ResultWriter.addTime(modelRunner.model.getName(),modelRunner.avgTime, modelRunner.minTime, modelRunner.maxTime, modelRunner.totalTime);
        }


        boolean isTheSame = true;
        // krijg nu een error maar starks als ik die top5 maak op bassis van models uit het bestand werkt het wel. (verwijder top5.json)
        System.out.println("Writing");
        ResultWriter.writeAll("history.json");

        JSONObject NewTop5 = ResultWriter.getTop5();

        if (topmodel == null){
            isTheSame = false;
        } else {
            for (String baseModelName : topmodel.keySet()) {
                if (!NewTop5.has(baseModelName)){
                    isTheSame = false;
                }
            }
        }

        if (!isTheSame) {
            ResultWriter.writeTop5("top5.json");
            System.out.println("Is not the same");
        } else {
            System.out.println("Is the same");
        }

        System.out.println("Time: " + (System.nanoTime() - start));

        // window = new Window();

    }

    public static OthelloAI newModel(double BaseMobility_diffWeight, double BaseCorner_diffWeight, double BaseStability_diffWeight, double BaseDisc_diffWeight, int BaseMaxDepth, String baseModelName){
        Random r = new Random();
        char[] PlusOrMinus = {'+', '-'};
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

        if (roundMaxDepth < MINDEPTH) {
            roundMaxDepth = MINDEPTH;
        } else if (roundMaxDepth > MAXDEPTH) {
            roundMaxDepth = MAXDEPTH;
        }

        UUID id = UUID.randomUUID();
        String modelId = id.toString();

        OthelloAI model = new OthelloAI(modelId, OthelloTile.PLAYER_1, null, roundObWeight, roundCornerWeight, roundStabilityWeight, roundValueDiscWeight, roundMaxDepth, baseModelName);
        return model;
    }

    static final int GAME_AMOUNT = 500;
    static final int MAX_GAME_THREADS = 10;
    static class ModelRunner implements Runnable{
        final ExecutorService executor = Executors.newFixedThreadPool(MAX_GAME_THREADS);

        long avgTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        long totalTime = 0;
        boolean finished = false;
        final OthelloAI model;

        public ModelRunner(OthelloAI model){
            this.model = model;
        }

        @Override
        public void run() {
            totalTime = 0;
            minTime = Long.MAX_VALUE;
            maxTime = 0;
            ResultWriter.addModel(model);

            List<Future<GameResult>> games = new ArrayList<>();

            for (int i = 0; i < GAME_AMOUNT; i++ ) {
                final long startTime = System.nanoTime();
                Callable<GameResult> task = getGameResultCallable(i, startTime);
                games.add(executor.submit(task));
            }

            try {
                for (Future<GameResult> game : games) {
                    GameResult result = game.get(); // This blocks until the thread is done

                    long realTime = result.realTime;

                    if (realTime < minTime) {
                        minTime = realTime;
                    }
                    if (realTime > maxTime) {
                        maxTime = realTime;
                    }
                    totalTime += realTime;
                }
            } catch (InterruptedException | ExecutionException e) {
                finished = true;
                e.printStackTrace();
            }

            model.cleanup();
            avgTime = totalTime / GAME_AMOUNT;
            finished = true;
        }

        private @NotNull Callable<GameResult> getGameResultCallable(int i, long startTime) {
            Callable<GameResult> task = () -> {
                OthelloGame game;
                if(i < GAME_AMOUNT / 2){
                    game = new OthelloGame(new Player[]{model, new OthelloRandomAI("RandomAI", OthelloTile.PLAYER_2, null)});
                }
                else {
                    game = new OthelloGame(new Player[]{new OthelloRandomAI("RandomAI", OthelloTile.PLAYER_2, null), model});
                }
                game.run();

                long endTime = System.nanoTime();
                long realTime = endTime - startTime;

                return new GameResult(realTime);
            };
            return task;
        }
    }

    public static class GameResult{
        long realTime;
        public GameResult(long realTime){
            this.realTime = realTime;
        }
    }

}
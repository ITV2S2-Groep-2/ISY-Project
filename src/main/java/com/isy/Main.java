package com.isy;

import com.isy.game.Game;
import com.isy.game.othello.OthelloGame;
import com.isy.game.othello.OthelloTile;
import com.isy.game.player.Player;
import com.isy.gui.Window;
import com.isy.util.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;


public class Main {
    public static Window window;
    public static Game runningGame = null;

    private static final Random r = new Random();


    public static final int MAXDEPTH = 4;
    public static final int MINDEPTH = 4;

    public static boolean endCase = false;

    public static void main(String[] args) {
        while (!endCase) {
            ResultWriter.modelMap.clear();
            System.out.println(LocalDateTime.now());
                long start = System.nanoTime();

            JSONObject topmodel = ModelFileReader.Read("top5.json");
            // depth met cap 5 afronden
            ArrayList<OthelloAI> models = new ArrayList<OthelloAI>();
            if (topmodel == null) {
                OthelloAI baseModel = new OthelloAI("BASEMODEL", OthelloTile.PLAYER_1, null);
                models.add(baseModel);

                double baseMobility_diffWeight = baseModel.getMobility_diffWeight();
                double baseCorner_diffWeight = baseModel.getCorner_diffWeight();
                double baseStability_diffWeight = baseModel.getStability_diffWeight();
                double baseDisc_diffWeight = baseModel.getDisc_diffWeight();
                int baseMaxDepth = baseModel.getMaxDepth();
                String parent = baseModel.getName();

                for (int i = 0; i < 23; i++) {
                    OthelloAI newModel = newModel(baseMobility_diffWeight, baseCorner_diffWeight, baseStability_diffWeight, baseDisc_diffWeight, baseMaxDepth, parent);
                    models.add(newModel);
                }
            } else {
                for (String baseModelName : topmodel.keySet()) {
                    JSONObject modelJson = topmodel.getJSONObject(baseModelName);
                    JSONObject settings = modelJson.getJSONObject("settings");

                    double baseMobilityDiffWeight = settings.getDouble("mobility_diff_weight");
                    double baseCornerDiffWeight = settings.getDouble("corner_diff_weight");
                    double baseStabilityDiffWeight = settings.getDouble("stability_diff_weight");
                    double baseDiscDiffWeight = settings.getDouble("disc_diff_weight");
                    int baseMaxDepth = settings.getInt("max_depth");
                    String parent = settings.getString("parent");
                    UUID id = UUID.randomUUID();
                    String modelId = id.toString();

                    OthelloAI baseModel = new OthelloAI(modelId, OthelloTile.PLAYER_1, null, baseMobilityDiffWeight, baseCornerDiffWeight, baseStabilityDiffWeight, baseDiscDiffWeight, baseMaxDepth, baseModelName);
                    models.add(baseModel);

                    for (int i = 0; i < 4; i++) {
                        OthelloAI newModel = newModel(baseMobilityDiffWeight, baseCornerDiffWeight, baseStabilityDiffWeight, baseDiscDiffWeight, baseMaxDepth, baseModelName);
                        models.add(newModel);
                    }


                }
                // nu nog niks
            }

            List<ModelRunner> modelRunnerList = new ArrayList<>();
            for (OthelloAI model : models) {
                ResultWriter.addModel(model);
                ModelRunner runner = new ModelRunner(model);
                modelRunnerList.add(runner);
                new Thread(runner).start();
            }

            boolean allFinished = false;

            while (!allFinished) {
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
                ResultWriter.addTime(
                        modelRunner.model.getName(),
                        modelRunner.avgTime, modelRunner.minTime, modelRunner.maxTime, modelRunner.totalTime,
                        modelRunner.model.totalMoveTime / modelRunner.model.moveCount, modelRunner.model.minMoveTime, modelRunner.model.maxMoveTime, modelRunner.model.totalMoveTime
                );
            }


            boolean isTheSame = true;
            // krijg nu een error maar starks als ik die top5 maak op bassis van models uit het bestand werkt het wel. (verwijder top5.json)
            System.out.println("Writing");

            JSONObject newTop5 = ResultWriter.getTop5();
            ResultWriter.writeAll("history.json");

            for (String key : newTop5.keySet()) {
                if (newTop5.getJSONObject(key).getJSONObject("stats").getInt("top_5_count") >= 5) {
                    System.out.println("ENDCASE BEHAALD STOPPEN MET TESTEN");
                    endCase = true;
                    System.out.println(LocalDateTime.now());
                }
            }


            if (topmodel == null) {
                isTheSame = false;
            } else {
                for (String baseModelName : topmodel.keySet()) {
                    if (!newTop5.has(baseModelName)) {
                        isTheSame = false;
                    }
                }
            }

            ResultWriter.writeTop5("top5.json");
            if (!isTheSame) {
                System.out.println("Is not the same");
            } else {
                System.out.println("Is the same");
            }

            System.out.println("Time: " + (System.nanoTime() - start));

            // window = new Window();
        }
    }

    public static OthelloAI newModel(double baseMobility_diffWeight, double baseCorner_diffWeight, double baseStability_diffWeight, double baseDisc_diffWeight, int baseMaxDepth, String baseModelName){
        char[] plusOrMinus = {'+', '-'};
        double randomValueObWeight = baseMobility_diffWeight * (r.nextDouble(5, 20) / 100);
        double randomValueCornerWeight = baseCorner_diffWeight * (r.nextDouble(5, 20) / 100);
        double randomValueStabilityWeight = baseStability_diffWeight * (r.nextDouble(5, 20) / 100);
        double randomValueDiscWeight = baseDisc_diffWeight * (r.nextDouble(5, 20) / 100);
        double randomValueMaxDepth =baseMaxDepth * (r.nextInt(5, 20) / 100.0);


        randomValueObWeight = (plusOrMinus[r.nextInt(2)] == '+') ?baseMobility_diffWeight + randomValueObWeight : baseMobility_diffWeight - randomValueObWeight;
        randomValueCornerWeight = (plusOrMinus[r.nextInt(2)] == '+') ? baseCorner_diffWeight + randomValueCornerWeight :baseCorner_diffWeight - randomValueCornerWeight;
        randomValueStabilityWeight = (plusOrMinus[r.nextInt(2)] == '+') ? baseStability_diffWeight + randomValueStabilityWeight : baseStability_diffWeight - randomValueStabilityWeight;
        randomValueDiscWeight = (plusOrMinus[r.nextInt(2)] == '+') ? baseDisc_diffWeight + randomValueDiscWeight : baseDisc_diffWeight - randomValueDiscWeight;
        randomValueMaxDepth = (plusOrMinus[r.nextInt(2)] == '+') ? baseMaxDepth + randomValueMaxDepth : baseMaxDepth - randomValueMaxDepth;


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
        long maxTime = Long.MIN_VALUE;
        long totalTime = 0;

        boolean finished = false;
        public final OthelloAI model;

        public ModelRunner(OthelloAI model){
            this.model = model;
        }

        @Override
        public void run() {
            totalTime = 0;
            minTime = Long.MAX_VALUE;
            maxTime = 0;

            List<Future<GameResult>> games = new ArrayList<>();

            for (int i = 0; i < GAME_AMOUNT; i++ ) {
                Callable<GameResult> task = getGameResultCallable(i);
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
                this.executor.shutdown();
            }

            model.cleanup();
            avgTime = totalTime / GAME_AMOUNT;
            finished = true;

            this.executor.shutdown();
        }

        private @NotNull Callable<GameResult> getGameResultCallable(int i) {
            Callable<GameResult> task = () -> {
                final long startTime = System.nanoTime();

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
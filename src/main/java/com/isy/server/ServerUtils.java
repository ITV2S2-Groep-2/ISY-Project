package com.isy.server;

import com.isy.server.await.IWaitable;
import com.isy.server.await.Promise;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.isy.server.ServerUtils.await;

public class ServerUtils {
    public static final int waitTime = 10;
    public static Pattern playerToMovePattern = Pattern.compile("PLAYERTOMOVE:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
    public static Pattern opponentPattern = Pattern.compile("OPPONENT:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    public static <T> T await(IWaitable<T> waitable){
        while (!waitable.hasData()){
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return waitable.getData();
    }

    public static String await(Promise promise){
        return promise.getData();
    }

    public static void asyncAwait(Promise promise, Consumer<String> consumer){
        new Thread(() -> {
           consumer.accept(await(promise));
        }).start();
    }
}

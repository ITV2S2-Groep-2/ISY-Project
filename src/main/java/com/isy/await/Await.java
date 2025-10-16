package com.isy.await;

import java.io.IOException;
import java.util.function.Consumer;

public class Await {
    public static final int waitTime = 100;

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
        try {
            String s = promise.getData();
            System.out.println("RESPONSE: " + s);

            return s;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void asyncAwait(Promise promise, Consumer<String> consumer){
        new Thread(() -> {
           consumer.accept(await(promise));
        }).start();
    }
}

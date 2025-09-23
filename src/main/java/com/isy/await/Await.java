package com.isy.await;

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
}

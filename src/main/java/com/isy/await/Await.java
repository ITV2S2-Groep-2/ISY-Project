package com.isy.await;

public class Await {
    public static <A extends IWaitable<B>, B> B await(A waitable){
        while (!waitable.hasData()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return waitable.getData();
    }
}

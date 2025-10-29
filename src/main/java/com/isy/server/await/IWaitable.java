package com.isy.server.await;

public interface IWaitable<T> {
    boolean hasData();
    T getData();
}

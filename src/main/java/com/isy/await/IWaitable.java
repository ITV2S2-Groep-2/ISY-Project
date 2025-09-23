package com.isy.await;

public interface IWaitable<T> {
    boolean hasData();
    T getData();
}

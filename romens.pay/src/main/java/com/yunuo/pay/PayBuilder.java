package com.yunuo.pay;

/**
 * Created by siery on 16/1/28.
 */
public abstract class PayBuilder<T> {
    public abstract PayType getPayType();

    protected abstract T build();
}

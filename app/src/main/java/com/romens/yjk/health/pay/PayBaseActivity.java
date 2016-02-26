package com.romens.yjk.health.pay;

import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public abstract class PayBaseActivity<T extends PayParams> extends BaseActionBarActivityWithAnalytics {

    protected abstract boolean sendPayRequest(T payParams);
}

package com.romens.yjk.health.pay;

import android.os.Bundle;

/**
 * @author Zhou Lisi
 * @create 16/3/3
 * @description
 */
public class PayParamsForWX extends PayParams {

    public Bundle toPayBundle(Bundle ext) {
        Bundle payParams = toBundle();
        payParams.putBundle("ext", ext);
        return payParams;
    }
}

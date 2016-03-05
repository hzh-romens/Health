package com.romens.yjk.health.wx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by siery on 16/2/4.
 */
public abstract class WXResponse extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WXManager.getInstance(this).getWXAPI().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WXManager.getInstance(this).getWXAPI().handleIntent(intent, this);
    }
}

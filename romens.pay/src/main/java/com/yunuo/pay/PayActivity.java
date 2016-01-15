package com.yunuo.pay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunuo.pay.apply.PayDemoActivity;
import com.yunuo.pay.wx.Constants;
import com.yunuo.pay.wx.WXActivity;

public class PayActivity extends Activity{
    private TextView applyView, wxView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initApi();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("支付选择");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        applyView = (TextView) findViewById(R.id.apply);
        wxView = (TextView) findViewById(R.id.wx);
        applyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PayActivity.this, PayDemoActivity.class));
            }
        });
        wxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PayActivity.this, WXActivity.class));
                finish();
            }
        });
      //  msgApi.handleIntent(getIntent(), this);
    }

    private IWXAPI msgApi;

    private void initApi() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        msgApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        msgApi.registerApp(Constants.APP_ID);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        msgApi.handleIntent(intent, this);
//    }

//    @Override
//    public void onReq(BaseReq baseReq) {
//
//    }
//
//    @Override
//    public void onResp(BaseResp baseResp) {
//
//    }
}

package com.yunuo.pay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yunuo.pay.adapter.PayAdapter;

public class PayActivity extends Activity {

    private ListView listView;
    private LinearLayout applyButton;
    private PayAdapter payAdapter;
    private String deliveryName;
    private double sumMoney;
    private String orderNumber;
    private TextView priceView, orderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        deliveryName = intent.getStringExtra("deliveryName");
        sumMoney = intent.getDoubleExtra("sumMoney", 0);
        orderNumber = intent.getStringExtra("orderNumber");
        initView();
    }


    private void initView() {
        priceView = (TextView) findViewById(R.id.price);
        orderView = (TextView) findViewById(R.id.orderNumber);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(deliveryName);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        applyButton = (LinearLayout) findViewById(R.id.btn_apply);
        priceView.setText("¥" + sumMoney);
        orderView.setText("订单编号" + orderNumber);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("微信支付".equals(deliveryName)) {
                    //微信支付
                    Intent intent = new Intent(PayActivity.this, WXPayActivity.class);
                    startActivity(intent);
                    finish();
                } else if ("支付宝支付".equals(deliveryName)) {
                    //支付宝支付
                }

            }
        });
    }
}

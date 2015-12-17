package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/9/22.
 */
public class CommitResultActivity extends BaseActivity implements View.OnClickListener {


    private LinearLayout firstLayout, secondLayout;
    private TextView tv_orderNumber, date, money, title;
    private Button toShop, toOrder;
    private String sumMoney, orderNumber, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitresult);
        initVew();
        Intent intent = getIntent();
        if ("true".equals(intent.getStringExtra("success"))) {
            title.setText("订单提交成功");
            firstLayout.setVisibility(View.VISIBLE);
            secondLayout.setVisibility(View.GONE);
            sumMoney = intent.getStringExtra("sumMoney");
            orderNumber = intent.getStringExtra("orderNumber");
            time = intent.getStringExtra("time");
        } else {
            title.setText("订单提交失败");
            firstLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.VISIBLE);
        }
        money.setText("订单总金额：¥" + sumMoney);
        tv_orderNumber.setText("订单编号：" + orderNumber);
        date.setText("下单时间：" + time);
        toShop.setOnClickListener(this);
        toOrder.setOnClickListener(this);
    }


    private void initVew() {
        firstLayout = (LinearLayout) findViewById(R.id.first_layout);
        secondLayout = (LinearLayout) findViewById(R.id.second_layout);
        tv_orderNumber = (TextView) findViewById(R.id.orderNumber);
        date = (TextView) findViewById(R.id.time);
        money = (TextView) findViewById(R.id.money);
        toOrder = (Button) findViewById(R.id.toOrder);
        toShop = (Button) findViewById(R.id.toShop);
        title = (TextView) findViewById(R.id.title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toOrder:
                Intent intent = new Intent(this, MyOrderActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.toShop:
                Intent i = new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        return false;
    }
}

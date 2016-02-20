package com.yunuo.pay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yunuo.pay.adapter.PayAdapter;

import java.util.ArrayList;
import java.util.List;

public class PayActivity extends Activity {

    private ListView listView;
    private LinearLayout applyButton;
    private PayAdapter payAdapter;
    private String deliveryName, orderNumber;
    private double sumMoney;

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
        listView = (ListView) findViewById(R.id.listview);
        applyButton = (LinearLayout) findViewById(R.id.btn_apply);
        payAdapter = new PayAdapter(this);
        getData();
        payAdapter.bindData(result, types);
        payAdapter.setStatus(status);
        listView.setAdapter(payAdapter);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("微信支付".equals(deliveryName)) {

                } else if ("支付宝支付".equals(deliveryName)) {

                }
                startActivity(new Intent(PayActivity.this, PayResultActivity.class));
            }
        });
    }

    private List<String> result;
    private SparseArray types;
    private SparseBooleanArray status;


    private void getData() {
        result = new ArrayList<>();
        types = new SparseArray();
        status = new SparseBooleanArray();
        result.add(orderNumber);
        result.add(sumMoney + "");
        types.append(0, ListItemType.orderCode);
        types.append(1, ListItemType.sumPrices);
        types.append(2, ListItemType.beneficary);
    }

    public void showToast(String info) {
        Toast.makeText(PayActivity.this, info, Toast.LENGTH_SHORT).show();
    }
}

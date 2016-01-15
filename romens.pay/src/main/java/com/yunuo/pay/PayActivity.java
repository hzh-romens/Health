package com.yunuo.pay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunuo.pay.wx.Constants;

import java.util.ArrayList;
import java.util.List;

public class PayActivity extends Activity {

    private ListView listView;
    private LinearLayout applyButton;
    private PayAdapter payAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initApi();
        initView();
//        applyView = (TextView) findViewById(R.id.apply);
//        wxView = (TextView) findViewById(R.id.wx);
//        applyView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(PayActivity.this, PayDemoActivity.class));
//            }
//        });
//        wxView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(PayActivity.this, WXActivity.class));
//                finish();
//            }
//        });
    }


    private IWXAPI msgApi;

    private void initApi() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        msgApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        msgApi.registerApp(Constants.APP_ID);
    }

    private void initView() {
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
        listView = (ListView) findViewById(R.id.listview);
        applyButton = (LinearLayout) findViewById(R.id.btn_apply);
        payAdapter = new PayAdapter(this);
        getData();
        payAdapter.bindData(result, types);
        listView.setAdapter(payAdapter);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private List<String> result;
    //  private List<String> types;
    private SparseArray types;

    private int contentFlag = 1;
    private int titleFlag = 2;
    private int wxFlag = 3;
    private int applyFlag = 4;

    private void getData() {
        result = new ArrayList<>();
        types = new SparseArray();
        for (int i = 0; i < 2; i++) {
            result.add("九九感冒灵");
            result.add("9.9");
        }
        types.append(0, contentFlag);
        types.append(1, titleFlag);
        types.append(2, wxFlag);
        types.append(3, applyFlag);
    }
}

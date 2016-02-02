package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.EditTextCells;

/**
 * Created by HZH on 2016/1/28.
 */

public class BindMemberActivity extends BaseActivity {
    private FrameLayout pharmacy, container;
    private CardView btnOk;
    private TextView pharmacyName;
    private RadioGroup radioGroup;
    private EditTextCells cells;
    private int selectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindmember, R.id.action_bar);
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("绑定会员卡");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        initView();
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
        selectMode = 1;
        cells = new EditTextCells(BindMemberActivity.this, selectMode);
        container.addView(cells);
        RadioButton phoneRadio = (RadioButton) findViewById(R.id.bindByPhone);
        phoneRadio.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.bindByPhone:
                        selectMode = 1;
                        cells = new EditTextCells(BindMemberActivity.this, selectMode);
                        container.removeAllViews();
                        container.addView(cells, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                        break;
                    case R.id.bindByCard:
                        selectMode = 2;
                        cells = new EditTextCells(BindMemberActivity.this, selectMode);
                        container.removeAllViews();
                        container.addView(cells, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                        break;
                }
            }
        });
        cells.setListener(new EditTextCells.buttonClickListener() {
            @Override
            public void sendMessage() {
                //向服务器获取验证码
                getMessageCode();

                showToastMessage("向服务器获取验证码" + cells.phonePassword.getText().toString());
            }
        });
    }


    private void initView() {
        pharmacy = (FrameLayout) findViewById(R.id.pharmacy);
        container = (FrameLayout) findViewById(R.id.container);
        btnOk = (CardView) findViewById(R.id.btnSure);
        pharmacyName = (TextView) findViewById(R.id.pharmacy_name);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        //点击确认
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastMessage("点击了确认键");
            }
        });
    }

    //get code from Server
    private void getMessageCode() {
    }

    //toast
    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

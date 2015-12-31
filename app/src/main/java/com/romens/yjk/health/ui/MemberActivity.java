package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2015/12/31.
 */
public class MemberActivity extends BaseActivity {
    private boolean isMember = false;

    private LinearLayout btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member, R.id.action_bar);
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("会员卡绑定");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
        btn_ok = (LinearLayout) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewStub stub = (ViewStub) findViewById(R.id.memberView);
                stub.inflate();
            }
        });
    }


}

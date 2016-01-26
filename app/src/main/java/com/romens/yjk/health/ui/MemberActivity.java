package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.adapter.MemberAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2015/12/31.
 */
public class MemberActivity extends BaseActivity {
    private boolean isMember = true;
    private LinearLayout btnOk, pwdLayout;
    private ActionBar actionBar;
    private List<String> types;
    private ListView listview, cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initBindView();
        if (isMember) {
            hideMemberView();
            initMember();
        }
    }


    private void initBindView() {
        pwdLayout = (LinearLayout) findViewById(R.id.pwdLayout);
        cardList = (ListView) findViewById(R.id.card_list);
    }

    private void hideMemberView() {
        pwdLayout.setVisibility(View.GONE);
        ViewStub viewById = (ViewStub) findViewById(R.id.memberLayout);
        viewById.inflate();
        isMember = true;
    }

    private void initMember() {
        listview = (ListView) findViewById(R.id.member_list);
        MemberAdapter memberAdapter = new MemberAdapter(this);
        getMemberData();
        memberAdapter.bindData(types);
        listview.setAdapter(memberAdapter);
    }

    public void getMemberData() {
        types = new ArrayList<String>();
        for (int i = 1; i <= 10; i++) {
            types.add(i + "");
        }
    }

    private List<String> cardData;
    public void getMemberCardData() {
        cardData=new ArrayList<String>();
        cardData.add("要健康");
        cardData.add("先声再康");
        cardData.add("人民同泰");
    }


}

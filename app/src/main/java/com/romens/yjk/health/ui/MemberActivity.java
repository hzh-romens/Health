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
//        actionBar = (ActionBar) findViewById(R.id.action_bar);
//        actionBar.setBackgroundColor(0xffeeeeee);
//        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_grey600_24dp);
//        actionBar.setTitle("会员卡绑定", 0xff212121);
//        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
//            @Override
//            public void onItemClick(int i) {
//                if (i == -1) {
//                    finish();
//                }
//            }
//        });
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
        //    actionBar.setTitle("我的会员卡", 0xff212121);
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
        for (int i = 1; i <= 4; i++) {
            types.add(i + "");
        }
    }

    private void getMemberCardData() {

    }


}

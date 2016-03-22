package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.adapter.CardListAdapter;
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
    private CardListAdapter cardListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member, R.id.action_bar);
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("我的会员卡");

        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(0, R.drawable.ic_add_white_24dp);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    //跳转到添加会员卡界面
                    startActivity(new Intent(MemberActivity.this, NewMemberActivity.class));
                }
            }
        });
        initBindView();
        if (isMember) {
            hideMemberView();
            initMember();
        }
    }


    private void initBindView() {
        pwdLayout = (LinearLayout) findViewById(R.id.pwdLayout);
        cardList = (ListView) findViewById(R.id.card_list);
        cardListAdapter = new CardListAdapter(this);
        getMemberCardData();
        cardListAdapter.BindData(cardData);
        cardList.setAdapter(cardListAdapter);
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
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //优惠卷
                if (position == 5) {
                    UIOpenHelper.openCuoponActivityWithBundle(MemberActivity.this, false);
                } else if (position == 8) {
                    //跳转到个人资料
                    UIOpenHelper.openAccountSettingActivity(MemberActivity.this);
                }
            }
        });
    }

    public void getMemberData() {
        types = new ArrayList<String>();
        for (int i = 1; i <= 4; i++) {
            types.add(i + "");
        }
    }

    private List<String> cardData;

    public void getMemberCardData() {
        cardData = new ArrayList<String>();
        cardData.add("要健康");
        cardData.add("先声再康");
        cardData.add("人民同泰");
    }


}

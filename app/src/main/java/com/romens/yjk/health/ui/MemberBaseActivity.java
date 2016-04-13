package com.romens.yjk.health.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.io.json.JacksonMapper;
import com.romens.android.log.FileLog;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.components.ToastCell;
import com.romens.yjk.health.ui.fragment.BindMemberFragment;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;
import com.romens.yjk.health.ui.fragment.MemberFragment;

import java.io.IOException;
import java.util.Map;

/**
 * Created by HZH on 2016/3/24.
 */
public class MemberBaseActivity extends DarkActionBarActivity {
    private ProgressBar progressBar;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    JudgeMember();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memebr_base_layout, R.id.action_bar);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        ActionBar actionBar = getMyActionBar();
        ActionBarMenu menu = actionBar.createMenu();
        // menu.addItem(0, R.drawable.ic_add_white_24dp);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
//                else if (i == 0) {
//                    //跳转到添加会员卡界面
//                    initFragmentManager();
//                    BindMemberFragment bindMemberFragment = new BindMemberFragment();
//                    fragmentTransaction.add(R.id.layout_content, bindMemberFragment);
//                    setActionBarTitle("绑定会员卡");
//                    fragmentTransaction.commit();
//                    fragmentManager.executePendingTransactions();
//                }
            }
        });
        JudgeMember();

    }

    private void showProgress(boolean progress) {
        progressBar.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    public void JudgeMember() {
        showProgress(true);
        Map<String, String> args = new FacadeArgs.MapBuilder().
                put("", "").build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserMemberCard", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(
                        new Connect.AckDelegate() {
                            @Override
                            public void onResult(Message message, Message errorMessage) {
                                showProgress(false);
                                if (errorMessage == null) {
                                    //2016-03-24 zhoulisi 使用JsonNode
                                    ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                                    JsonNode jsonNode = responseProtocol.getResponse();
                                    try {
                                        String result = jsonNode.get("RESULT").asText();
                                        if (TextUtils.equals("0", result)) {
                                            BindMemberFragment bindMemberFragment = new BindMemberFragment();
                                            setActionBarTitle("绑定会员卡");
                                            setupFragment(bindMemberFragment);
                                        } else {
                                            JsonNode dataObj = JacksonMapper.getInstance().readTree(jsonNode.get("DATA").textValue());
                                            JsonNode memberObj = dataObj.get("member").get(0);
                                            String lvLevel = memberObj.get("GROUPNAME").asText();//会员等级
                                            String integral = memberObj.get("JF").asText();//积分
                                            String remainMoney = memberObj.get("BALANCE").asText();//余额
                                            String cardId = memberObj.get("ID").asText();
                                            JsonNode card = dataObj.get("card");
                                            String diybgc = card.get("diybg").asText();
                                            String cardbackbg = card.get("cardbackbg").asText();
                                            MemberFragment memberFragment = new MemberFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("lvLevel", lvLevel);
                                            bundle.putString("integral", integral);
                                            bundle.putString("remainMoney", remainMoney);
                                            bundle.putString("cardId", cardId);
                                            bundle.putString("diybgc", diybgc);
                                            bundle.putString("cardbackbg", cardbackbg);
                                            memberFragment.setArguments(bundle);
                                            setActionBarTitle("我的会员卡");
                                            setupFragment(memberFragment);
                                        }
                                    } catch (IOException e) {
                                        FileLog.e(e);
                                    }
                                } else {
                                    ToastCell.toast(MemberBaseActivity.this, "发生了一些错误,请稍后再试...");
                                    finish();
                                }
                            }
                        }
                ).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void setupFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }

    public void setActionBarTitle(String title) {
        getMyActionBar().setTitle(title);
    }

    @Override
    protected String getActivityName() {
        return "我的会员卡";
    }
}

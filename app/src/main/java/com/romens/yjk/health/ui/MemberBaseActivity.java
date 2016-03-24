package com.romens.yjk.health.ui;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

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
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.ui.fragment.BindMemberFragment;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;

import java.io.IOException;
import java.util.Map;

/**
 * Created by HZH on 2016/3/24.
 */
public class MemberBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memebr_base_layout);
        JudgeMember();
        ActionBar actionBar = (ActionBar) findViewById(R.id.action_bar);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        BindMemberFragment bindMemberFragment = new BindMemberFragment();
        fragmentTransaction.add(R.id.layout_content, bindMemberFragment, "bind");
        fragmentTransaction.commit();

    }

    public void JudgeMember() {
        Map<String, String> args = new FacadeArgs.MapBuilder().
                put("PHONE", UserSession.getInstance().get().getPhone()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserMemberCard", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(
                        new Connect.AckDelegate() {
                            @Override
                            public void onResult(Message message, Message errorMessage) {
                                needHideProgress();
                                if (errorMessage == null) {
                                    //2016-03-24 zhoulisi 使用JsonNode
                                    ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                                    JsonNode jsonNode = responseProtocol.getResponse();
//                                        JSONObject dataObj = new JSONArray(object.getString("DATA")).getJSONObject(0);
                                    Log.i("数据------", jsonNode.toString());
                                    try {
                                        String result = jsonNode.get("result").asText();
                                        Log.i("结果----", result + "");
                                        JsonNode dataObj = JacksonMapper.getInstance().readTree(jsonNode.get("DATA").textValue());
                                        JsonNode memberObj = dataObj.get("member").get(0);
                                        String lvLevel = memberObj.get("GROUPNAME").asText();//会员等级
                                        String integral = memberObj.get("JF").asText();//积分
                                        String remainMoney = memberObj.get("BALANCE").asText();//余额
                                        String cardId = memberObj.get("ID").asText();
                                        JsonNode card = dataObj.get("card");
                                        String diybgc = card.get("diybg").asText();
                                        String cardbackbg = card.get("cardbackbg").asText();
                                    } catch (IOException e) {
                                        FileLog.e(e);
                                    }
                                } else {
                                    Log.e("tag", "--->" + errorMessage.msg);
                                }
                            }
                        }
                ).build();

        ConnectManager.getInstance().request(this, connect);
    }

}

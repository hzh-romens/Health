package com.romens.yjk.health.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.zxing.BarcodeFormat;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
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
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.adapter.CardListAdapter;
import com.romens.yjk.health.ui.adapter.MemberAdapter;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;
import com.romens.yjk.health.ui.utils.CodeUtils;
import com.romens.yjk.health.ui.utils.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        needShowProgress("正在加载");
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
                if (position == 3) {
                    Bitmap bitmap = CodeUtils.encodeAsBitmap(UserConfig.getInstance().getClientUserPhone(), BarcodeFormat.CODE_128, 300, 96);
                    DialogUtils dialogUtils = new DialogUtils();
                    dialogUtils.show_infor(MemberActivity.this, bitmap);
                } else if (position == 5) {
                    //优惠卷
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
        Map<String, String> args = new FacadeArgs.MapBuilder().
                put("PHONE", UserSession.getInstance().get().getPhone()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserMemberCard", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
//                .withParser(new JSONNodeParser())
                .withDelegate(
                        new Connect.AckDelegate() {
                            @Override
                            public void onResult(Message message, Message errorMessage) {
                                needHideProgress();
                                if (errorMessage == null) {
                                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) message.protocol;
                                    try {
                                        JSONObject object = new JSONObject(responseProtocol.getResponse());
//                                        JSONObject dataObj = new JSONArray(object.getString("DATA")).getJSONObject(0);
                                        String dataStr = object.getString("DATA");
                                        JSONObject dataObj = new JSONObject(dataStr);
                                        JSONObject memberObj = dataObj.getJSONArray("member").getJSONObject(0);
                                        String lvLevel = memberObj.getString("GROUPNAME");//会员等级
                                        String integral = memberObj.getString("JF");//积分
                                        String remainMoney = memberObj.getString("BALANCE");//余额
                                        String cardId = memberObj.getString("ID");
                                        JSONObject card = dataObj.getJSONObject("card");
                                        String diybgc = card.getString("diybg");
                                        String cardbackbg = card.getString("cardbackbg");
                                        MemberAdapter memberAdapter = (MemberAdapter) listview.getAdapter();
                                        memberAdapter.setData(cardId, lvLevel, integral, remainMoney, diybgc, cardbackbg);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
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

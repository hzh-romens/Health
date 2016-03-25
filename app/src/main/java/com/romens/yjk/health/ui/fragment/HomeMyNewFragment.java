package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.io.json.JacksonMapper;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.TextIconCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.DevelopModeManager;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.MonitorHelper;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.ControlAddressActivity;
import com.romens.yjk.health.ui.FeedBackNewActivity;
import com.romens.yjk.health.ui.HelpNewActivity;
import com.romens.yjk.health.ui.HistoryActivity;
import com.romens.yjk.health.ui.MemberBaseActivity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.SettingActivity;
import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.cells.GridViewCell;
import com.romens.yjk.health.ui.cells.KeyAndImgCell;
import com.romens.yjk.health.ui.cells.LoginCell;
import com.romens.yjk.health.ui.cells.NewUserProfileCell;
import com.romens.yjk.health.ui.cells.SupportCell;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2016/2/17.
 */
public class HomeMyNewFragment extends BaseFragment implements AppNotificationCenter.NotificationCenterDelegate {
    private ListView listView;
    private ListAdapter adapter;
    private String integral = "0";
    private String remainMoney = "0";
    private String coupon = "0";
    private String sexType = "0";

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        adapter = new ListAdapter(getActivity());
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FrameLayout content = new FrameLayout(context);

        listView = new ListView(context);
        content.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == developModeRow) {
                    DevelopModeManager.openDebug(getActivity());
                }
            }
        });
        return content;
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginOut);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onUserSubjoinChanged);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        MonitorHelper.unregisterUpdate();
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.loginSuccess);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.loginOut);
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onUserSubjoinChanged);
        super.onDestroy();
    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {
        updateData();
    }

    public void updateData() {
        UserEntity clientUser = UserSession.getInstance().get();

        rowCount = 0;
        if (clientUser != null) {
            requestUserInfo();
            loginRow = -1;
            userProfileRow = rowCount++;
            orderTitleRow = rowCount++;
            orderControlRow = rowCount++;
            userInfoSectionRow1 = rowCount++;
            personControlRow = rowCount++;
        } else {
            loginRow = rowCount++;
            userProfileRow = -1;
            orderTitleRow = -1;
            orderControlRow = -1;
            userInfoSectionRow1 = -1;
            personControlRow = -1;
        }

        if (clientUser != null) {
            otherSectionRow = rowCount++;
            otherControlRow = rowCount++;
            checkUpdateRow = -1;
        } else {
            otherSectionRow = -1;
            otherControlRow = -1;
            checkUpdateRow = rowCount++;
        }

        supportRow = rowCount++;
        if (DevelopModeManager.enable()) {
            developModeRow = rowCount++;
        } else {
            developModeRow = -1;
        }
        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int loginRow;
    private int userProfileRow;

    private int orderTitleRow;
    private int orderControlRow;

    private int userInfoSectionRow1;
    private int personControlRow;

    private int otherSectionRow;
    private int otherControlRow;

    private int checkUpdateRow;

    private int supportRow;

    private int developModeRow;

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.loginSuccess) {
            updateData();
        } else if (i == AppNotificationCenter.loginOut) {
            updateData();
        } else if (i == AppNotificationCenter.onUserSubjoinChanged) {
            requestUserInfo();
        }
    }

    class ListAdapter extends BaseFragmentAdapter {
        private Context adapterContext;

        public ListAdapter(Context context) {
            adapterContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return i == developModeRow;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == userProfileRow) {
                return 0;
            } else if (i == userInfoSectionRow1 || i == otherSectionRow) {
                return 1;
            } else if (i == personControlRow || i == otherControlRow || i == orderControlRow) {
                return 2;
            } else if (i == supportRow) {
                return 3;
            } else if (i == loginRow) {
                return 4;
            } else if (i == checkUpdateRow || i == developModeRow) {
                return 5;
            } else if (i == orderTitleRow) {
                return 6;
            }
            return -1;
        }

        @Override
        public int getViewTypeCount() {
            return 8;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (view == null) {
                    view = new NewUserProfileCell(adapterContext);
                }
                NewUserProfileCell cell = (NewUserProfileCell) view;
                UserEntity clientUser = UserSession.getInstance().get();
                cell.setUser(clientUser, integral, remainMoney, coupon, sexType);
            } else if (type == 1) {
                if (view == null) {
                    view = new TextIconCell(adapterContext);
                }
                TextIconCell cell = (TextIconCell) view;
                cell.setBackgroundResource(R.drawable.greydivider);
                if (position == userInfoSectionRow1) {
                    cell.setIconText(R.drawable.ic_person, "个人中心", false);
                } else if (position == otherSectionRow) {
                    cell.setIconText(R.drawable.ic_other, "其他", false);
                }
            } else if (type == 2) {

                if (view == null) {
                    view = new GridViewCell(adapterContext);
                }
                GridViewCell cell = (GridViewCell) view;
                if (position == personControlRow) {
                    final List<Map<String, Object>> personControlList = initPersonControlData();
                    cell.setData(personControlList);
                    cell.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {//账户管理
                                UIOpenHelper.openAccountSettingActivity(getActivity());
                            } /*else if (position == 1) {//我的订单
                                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                            }*/ else if (position == 1) {//会员管理
                                // Toast.makeText(adapterContext, "正在开发，敬请期待!", Toast.LENGTH_SHORT).show();
                                //UIOpenHelper.openMemberActivity(getActivity());
                                startActivity(new Intent(getActivity(), MemberBaseActivity.class));
                            } else if (position == 3) {//我的收藏
                                UIOpenHelper.openFavoritesActivity(getActivity());
                            } else if (position == 4) {//历史浏览
                                startActivity(new Intent(getActivity(), HistoryActivity.class));
                            } else if (position == 2) {//收货地址管理
                                startActivity(new Intent(getActivity(), ControlAddressActivity.class));
                            } else if (position == 5) {//个人健康
                                Toast.makeText(adapterContext, "正在开发，敬请期待!", Toast.LENGTH_SHORT).show();
                            } else if (position == 6) {
                                UserConfig.clearUser();
                                UserConfig.clearConfig();
                                FacadeToken.getInstance().expired();
                                updateData();
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -100000);
                            }
                        }
                    });
                } else if (position == otherControlRow) {
                    final List<Map<String, Object>> otherControlList = initOtherControlData();
                    cell.setData(otherControlList);
                    cell.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {//帮助
                                startActivity(new Intent(getActivity(), HelpNewActivity.class));
                            } else if (position == 1) {//意见反馈
                                startActivity(new Intent(getActivity(), FeedBackNewActivity.class));
                            } else if (position == 2) {//检查更新
                                MonitorHelper.checkUpdate(getActivity(), true);
                            } else if (position == 3) {//设置
//                                Toast.makeText(adapterContext, "正在开发，敬请期待!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(adapterContext, SettingActivity.class);
                                startActivityForResult(intent, UserGuidConfig.REQUEST_HOMEMY_TO_SETTING);
                            }
                        }
                    });
                } else if (position == orderControlRow) {
                    final List<Map<String, Object>> otherControlList = initOrderControlData();
                    cell.setData(otherControlList);
                    cell.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), MyOrderActivity.class);
                            if (position == 0) {//处理中
                                intent.putExtra("fragmentIndex", 1);
                            } else if (position == 1) {//已完成
                                intent.putExtra("fragmentIndex", 2);
                            } else if (position == 2) {//已评价
                                intent.putExtra("fragmentIndex", 3);
                            }
                            startActivity(intent);
                        }
                    });
                }

            } else if (type == 3) {
                if (view == null) {
                    view = new SupportCell(adapterContext);
                    view.setPadding(0, AndroidUtilities.dp(8), 0, 0);
                }
                SupportCell cell = (SupportCell) view;
                cell.setBackgroundResource(R.drawable.greydivider);
                cell.setMultilineDetail(true);
                SpannableStringBuilder value = new SpannableStringBuilder();
                SpannableString supportOrg = new SpannableString("青岛雨诺网络信息股份有限公司");
                supportOrg.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, supportOrg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                value.append(supportOrg);
                value.append("\n");
                try {
                    PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    value.append(String.format("%s for Android v%s (c%d)", getString(R.string.app_name), pInfo.versionName, pInfo.versionCode));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (position == supportRow) {
                    cell.setTextAndValue("技术支持", value, true);
                }
            } else if (type == 4) {
                if (view == null) {
                    view = new LoginCell(adapterContext);
                }
                LoginCell cell = (LoginCell) view;
                if (position == loginRow) {
                    cell.setLoginCellDelegate(new LoginCell.LoginCellDelegate() {
                        @Override
                        public void onLoginClick() {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            } else if (type == 5) {
                if (view == null) {
                    view = new TextSettingsCell(adapterContext);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                if (position == checkUpdateRow) {
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        cell.setTextAndValue("检查更新", "c" + pInfo.versionCode, true);
                    } catch (PackageManager.NameNotFoundException e) {
                        cell.setText("检查更新", true);
                    }
                } else if (position == developModeRow) {
                    cell.setTextAndValue("开发模式入口", "已开启", true);
                }
            } else if (type == 6) {
                if (view == null) {
                    view = new KeyAndImgCell(adapterContext);
                }
                KeyAndImgCell cell = (KeyAndImgCell) view;
//                cell.setBackgroundResource(R.drawable.greydivider);
                cell.setLeftTextViewPadding(24, 0);
                cell.setKeyColor(0xff121212);
                cell.setInfo("全部订单", "查看全部订单", R.drawable.y, true);
                cell.setDivider(true, AndroidUtilities.dp(24), 0);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), MyOrderActivity.class));
                    }
                });
            }
            return view;
        }
    }

    public void requestUserInfo() {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserInfoList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(
                        new Connect.AckDelegate() {
                            @Override
                            public void onResult(Message message, Message errorMessage) {
                                if (errorMessage == null) {
                                    ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                                    JsonNode response = responseProtocol.getResponse();
                                    if (!response.has("ERROR")) {
                                        handleUserInfoResponse(responseProtocol.getResponse());
                                        return;
                                    }
                                }
                                Toast.makeText(getActivity(), "获取积分余额失败!", Toast.LENGTH_SHORT).show();
                            }
                        }
                ).build();

        ConnectManager.getInstance().request(getActivity(), connect);
    }

    private void handleUserInfoResponse(final JsonNode jsonNode) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JsonNode jfyeObject = JacksonMapper.getInstance().readTree(jsonNode.get("JFYE").asText()).get(0);
                    double integralD = jfyeObject.get("SIGNINPOINT").asDouble(0) +
                            jfyeObject.get("CONSUMPTIONPOINT").asDouble(0);
                    integral = String.valueOf(integralD);
                    remainMoney = jfyeObject.get("CONSUMEAMOUNT").asText();
                    coupon = jfyeObject.get("COUPONNUM").asText();
                    JsonNode userinfoObj = JacksonMapper.getInstance().readTree(jsonNode.get("USERINFO").asText()).get(0);
                    sexType = userinfoObj.get("SEX").asText();
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                JSONObject jfyeObj = new JSONArray(object.getString("JFYE")).getJSONObject(0);
//                double integralD = Double.parseDouble(jfyeObj.getString("SIGNINPOINT")) +
//                        Double.parseDouble(jfyeObj.getString("CONSUMPTIONPOINT"));
//                integral = integralD + "";
//                remainMoney = Double.parseDouble(jfyeObj.getString("CONSUMEAMOUNT")) + "";
//                coupon = jfyeObj.getString("COUPONNUM") + "";
//                JSONObject userinfoObj = new JSONArray(object.getString("USERINFO")).getJSONObject(0);
//                sexType = userinfoObj.getString("SEX");
//                adapter.notifyDataSetChanged();
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == UserGuidConfig.RESPONSE_SETTING_TO_HOMEMY) {
//            userEntity = null;
//            updateData();
//        }
//    }

    private List<Map<String, Object>> initOrderControlData() {
        List<Map<String, Object>> personControlList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "待处理");
        map.put("icon", R.drawable.ic_being);
        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "已完成");
        map.put("icon", R.drawable.ic_complete);
        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "已评价");
        map.put("icon", R.drawable.ic_evaluate);
        personControlList.add(map);

        return personControlList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserGuidConfig.RESPONSE_SETTING_TO_HOMEMY) {
            //userEntity = null;
            updateData();
        }
    }

    private List<Map<String, Object>> initOtherControlData() {
        List<Map<String, Object>> personControlList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "帮助");
        map.put("icon", R.drawable.ic_help);
        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "意见反馈");
        map.put("icon", R.drawable.ic_feedback);
        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "检查更新");
        map.put("icon", R.drawable.ic_checkup);
        personControlList.add(map);

//        map = new HashMap<>();
//        map.put("name", "设置");
//        map.put("icon", R.drawable.ic_setting);
//        personControlList.add(map);

        return personControlList;
    }

    private List<Map<String, Object>> initPersonControlData() {
        List<Map<String, Object>> personControlList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "个人信息");
        map.put("icon", R.drawable.ic_account);
        personControlList.add(map);

//        map = new HashMap<>();
//        map.put("name", "我的订单");
//        map.put("icon", R.drawable.ic_order);
//        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "我的会员卡");
        map.put("icon", R.drawable.ic_member);
        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "收货地址");
        map.put("icon", R.drawable.ic_address2);
        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "我的收藏");
        map.put("icon", R.drawable.ic_favorite_person);
        personControlList.add(map);

        map = new HashMap<>();
        map.put("name", "历史浏览");
        map.put("icon", R.drawable.ic_history);
        personControlList.add(map);

//        map = new HashMap<>();
//        map.put("name", "个人健康");
//        map.put("icon", R.drawable.ic_health);
//        personControlList.add(map);

//        map = new HashMap<>();
//        map.put("name", "退出登录");
//        map.put("icon", R.drawable.ic_health);
//        personControlList.add(map);

        return personControlList;
    }
}

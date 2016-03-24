package com.romens.yjk.health.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.BottomSheet;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.LocationAddressHelper;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.CitysDao;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.db.entity.CitysEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.adapter.ControlAddressAdapter;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/8/19.
 * 用户地址管理页面
 */
public class ControlAddressActivity extends BaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listView;
    private ActionBar actionBar;
    private Button addAddress;
    private List<AddressEntity> addressListEntitis;
    private ControlAddressAdapter adapter;
    private ProgressBar progressBar;

    private LinearLayout havaAddressLayout;
    private LinearLayout noHaveAddressLayout;

    private String userGuid = "3333";
    private Dialog editDialog;

    private String isFromCommitOrderActivity;
    public static final int ADDRESS_EDIT_TYPE = 0;
    public static final int ADDRESS_CHECK_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        setContentView(R.layout.activity_shipping_address, R.id.action_bar);
        havaAddressLayout = (LinearLayout) findViewById(R.id.control_address_hava_address);
        listView = (RecyclerView) findViewById(R.id.control_address_recycler);
        listView.setLayoutManager(new LinearLayoutManager(this));
//        listView.addItemDecoration(new LineItemDecoration(this));
        addAddress = (Button) findViewById(R.id.control_address_add_address);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        noHaveAddressLayout = (LinearLayout) findViewById(R.id.control_address_no_address);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        UIHelper.setupSwipeRefreshLayoutProgress(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                requestDataChanged(userGuid, "0");
            }
        });

        actionBarEven();
        initData();
        showProgressLayout();
//        if (addressListEntitis != null && addressListEntitis.size() > 0) {
//            setHaveAddressView();
//        } else {
//            setNoHaveAddressView();
//        }
//        queryDb();

        //同步省市县数据
        LocationAddressHelper.syncServerLocationAddress(this);

        Intent intent = getIntent();
        isFromCommitOrderActivity = intent.getStringExtra("chose");
    }

    private void showProgressLayout() {
        noHaveAddressLayout.setVisibility(View.GONE);
        havaAddressLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    //没有地址时，显示
    private void setNoHaveAddressView() {
        progressBar.setVisibility(View.GONE);
        noHaveAddressLayout.setVisibility(View.VISIBLE);
        havaAddressLayout.setVisibility(View.GONE);

        adapter = new ControlAddressAdapter(this, addressListEntitis);
        listView.setAdapter(adapter);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIOpenHelper.openAddShippingAddress(ControlAddressActivity.this, 0);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestDataChanged(userGuid, "0");
    }

    //有地址时，显示
    private void setHaveAddressView() {
        adapter = new ControlAddressAdapter(this, addressListEntitis);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        progressBar.setVisibility(View.GONE);
        noHaveAddressLayout.setVisibility(View.GONE);
        havaAddressLayout.setVisibility(View.VISIBLE);
        if (isFromCommitOrderActivity != null && isFromCommitOrderActivity.equals("chose")) {
            adapter.setIsShowArrow(false);
        }

        adapter.setOnItemLongClickLinstener(new ControlAddressAdapter.onItemLongClickListener() {
            @Override
            public void itemLongClickListener(int position) {
                removeDialogView(position);
            }

            @Override
            public void isDefaultClickLListener(int position) {
                requestDefaultChanged(userGuid, addressListEntitis.get(position).getADDRESSID(), position);
            }

            @Override
            public void itemClickListener(int position) {
                chooseItemBackEvent(position);
            }

            @Override
            public void itemClickToEditListener(int position, int type) {
                UIOpenHelper.openAddShippingAddress(ControlAddressActivity.this, addressListEntitis.get(position), type);
            }

            @Override
            public void itemClickToCheckListner(int position, int type) {
                if (isFromCommitOrderActivity != null && isFromCommitOrderActivity.equals("chose")) {
                    chooseItemBackEvent(position);
                } else {
                    UIOpenHelper.openAddShippingAddress(ControlAddressActivity.this, addressListEntitis.get(position), type);
                }
            }
        });
    }

    //请求收货地址列表
    private void requestDataChanged(String userGuid, String defaultFlag) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("DEFAULTFLAG", defaultFlag);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserAddressList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                refreshLayout();
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                refreshLayout();
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setAddressListData(responseProtocol.getResponse());
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    private void setAddressListData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count < 0) {
            return;
        }
        addressListEntitis.clear();
        for (LinkedTreeMap<String, String> item : response) {
            AddressEntity entity = AddressEntity.mapToEntity(item);
            addressListEntitis.add(entity);
        }
        adapter.setData(addressListEntitis);
        refreshLayout();
    }

    private void refreshLayout() {
        swipeRefreshLayout.setRefreshing(false);
        if (addressListEntitis != null && addressListEntitis.size() > 0) {
            setHaveAddressView();
        } else {
            setNoHaveAddressView();
        }
    }

    private void actionBarEven() {
        actionBar = getMyActionBar();
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_add_white_24dp);

        actionBar.setTitle("收货地址管理");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    backEvent();
                } else if (i == 0) {
                    UIOpenHelper.openAddShippingAddress(ControlAddressActivity.this, 0);
                    //startActivity(new Intent(ControlAddressActivity.this, NewShippingAddressActivity.class));
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (editDialog != null && editDialog.isShowing()) {
            editDialog.dismiss();
        }
        super.onPause();
    }

    public void removeDialogView(final int position) {
        editDialog = new BottomSheet.Builder(this)
                .setTitle("地址管理")
                .setItems(new CharSequence[]{"删除地址"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        if (which == 0) {
                            requestDeleteDataChanged(addressListEntitis.get(position).getADDRESSID());
                            dialog.dismiss();
                            needShowProgress("正在处理...");
                        }
                    }
                }).create();
        editDialog.show();
    }

//    public void removeDialogView(final int position) {
//        final AlertDialog dialog = new AlertDialog.Builder(this).create();
//        TextView textView = new TextView(this);
//
//        textView.setBackgroundResource(R.drawable.bg_white);
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//        textView.setSingleLine(true);
//        textView.setGravity(Gravity.CENTER);
//        textView.setText("删除");
//        textView.setTextColor(getResources().getColor(R.color.theme_primary));
//        textView.setPadding(AndroidUtilities.dp(32), AndroidUtilities.dp(8), AndroidUtilities.dp(32), AndroidUtilities.dp(8));
//        LinearLayout.LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
//        infoViewParams.weight = 1;
//        infoViewParams.gravity = Gravity.CENTER;
//        textView.setLayoutParams(infoViewParams);
//
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestDeleteDataChanged(addressListEntitis.get(position).getADDRESSID());
//                dialog.dismiss();
//                needShowProgress("正在处理...");
//            }
//        });
//
//        dialog.show();
//        dialog.setContentView(textView);
//    }

    private void initData() {
        addressListEntitis = new ArrayList<>();
        requestDataChanged(userGuid, "0");
    }

    //请求修改默认
    private void requestDefaultChanged(final String userGuid, String addressId, final int index) {
        needShowProgress("正在处理...");
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ADDRESSID", addressId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "setDefaultAddress", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        requestDataChanged(userGuid, "0");
                    } else {
                        Toast.makeText(ControlAddressActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", msg.msg);
                }
            }
        });
    }

    //请求删除接口
    private void requestDeleteDataChanged(String addressId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("ADDRESSID", addressId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "deleteAddress", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        requestDataChanged(userGuid, "0");
                    } else {
                        Toast.makeText(ControlAddressActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                    needHideProgress();
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", msg.msg);
                }
            }
        });
    }

    //请求省市县
    private void requestCityDataChanged() {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetAllDistrict", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(ControlAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    setQueryData(responseProtocol.getResponse());
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR");
                }
                needHideProgress();
            }
        });
    }

    private List<CitysEntity> entities;

    private void setQueryData(List<LinkedTreeMap<String, String>> response) {
        int count = response == null ? 0 : response.size();
        if (count <= 0) {
            return;
        }
        for (LinkedTreeMap<String, String> item : response) {
            CitysEntity entity = CitysEntity.mapToEntity(item);
            entities.add(entity);
            CitysDao dao = DBInterface.instance().openReadableDb().getCitysDao();
            dao.insert(entity);
        }
    }

    public void queryDb() {
        CitysDao dao = DBInterface.instance().openReadableDb().getCitysDao();
        entities = dao.queryBuilder().list();
    }


    private void chooseItemBackEvent(int position) {
        if (isFromCommitOrderActivity != null && isFromCommitOrderActivity.equals("chose")) {
            Intent intent = new Intent();
            AddressEntity entity = addressListEntitis.get(position);
            intent.putExtra("responseCommitEntity", entity);
            setResult(RESULT_OK, intent);
            finish();
//        } else {
//            UIOpenHelper.openAddShippingAddress(ControlAddressActivity.this, addressListEntitis.get(position));
        }
    }

    private void backEvent() {
        if (isFromCommitOrderActivity != null && isFromCommitOrderActivity.equals("chose")) {
            Intent intent = new Intent();
            AddressEntity entity = null;
            for (int j = 0; j < addressListEntitis.size(); j++) {
                if (addressListEntitis.get(j).getISDEFAULT().equals("1")) {
                    entity = addressListEntitis.get(j);
                }
            }
            if (entity == null) {
                if (addressListEntitis.size() > 0 && addressListEntitis.size() != 0) {
                    entity = addressListEntitis.get(0);
                    intent.putExtra("responseCommitEntity", entity);
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED, intent);
                }
            } else {
                intent.putExtra("responseCommitEntity", entity);
                setResult(RESULT_OK, intent);
            }

        }
        finish();
    }

    @Override
    public void onBackPressed() {
        backEvent();
        return;
    }
}

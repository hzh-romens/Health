package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.LocationAddressHelper;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.db.entity.CitysEntity;
import com.romens.yjk.health.ui.activity.BaseActivity;
import com.romens.yjk.health.ui.activity.LocationAddressSelectActivity;
import com.romens.yjk.health.ui.cells.InputTextCell;
import com.romens.yjk.health.ui.cells.LocationAddressInputCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/17.
 * 添加一个新的收获地址
 */
public class NewShoppingAddressActivity extends BaseActivity implements AppNotificationCenter.NotificationCenterDelegate {

    private InputTextCell editUserView;
    private InputTextCell editPhoneView;
    private LocationAddressInputCell editAddressIdView;
    private InputTextCell editAddressDetailView;

    private String[] locationNames;
    private String[] locationValues;

    private String userGuid = "3333";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onLocationAddressChanged);
        userGuid = UserGuidConfig.USER_GUID;
        setContentView(R.layout.activity_shopping_address, R.id.action_bar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        actionBarEven();

        initView();
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onLocationAddressChanged);
        FacadeClient.cancel(this);
        super.onDestroy();
    }

    private void actionBarEven() {
        ActionBar actionBar = getMyActionBar();
        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(0, R.drawable.checkbig);

        actionBar.setTitle("新增收货地址");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    String receiver = editUserView.getValue();
                    String contectPhone = editPhoneView.getValue();
                    String AddressDetail = editAddressDetailView.getValue();
//                    String districtGuid = "";
//                    if (isExecutInitList) {
//                        districtGuid = districtList.get(districtView.getCurrentItem()).getGuid();
//                        if (districtGuid == null && districtGuid.equals("")) {
//                            districtGuid = cityList.get(cityView.getCurrentItem()).getGuid();
//                        }
//                    }
                    if (receiver == null || receiver.equals("")) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请输入联系人", Toast.LENGTH_SHORT).show();
                    } else if (contectPhone == null || contectPhone.equals("")) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
                    } else if (locationValues == null) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请选择地址", Toast.LENGTH_SHORT).show();
                    } else if (AddressDetail == null || AddressDetail.equals("")) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请输入详细地址", Toast.LENGTH_SHORT).show();
                    } else {
                        saveAddress2Sevice(receiver, contectPhone, locationValues, AddressDetail);
                    }
                }
            }
        });
    }

    private void saveAddress2Sevice(String receiver, String contectPhone, String[] guids, String AddressDetail) {
        AddressEntity entity = new AddressEntity();
        entity.setADDRESSID("");
        entity.setRECEIVER(receiver);
        entity.setCONTACTPHONE(contectPhone);
        entity.setPROVINCE(guids[0]);
        entity.setCITY(guids[0]);
        entity.setREGION(guids[0]);
        entity.setADDRESS(AddressDetail);
        entity.setISDEFAULT("0");
        entity.setADDRESSTYPE("1");

        Gson gson = new Gson();
        String result = gson.toJson(entity);
        requestSaveControlAddress(userGuid, result);
    }

    public void requestSaveControlAddress(String userGuid, final String jsonData) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("JSONDATA", jsonData);

        final FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "saveAddress", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(NewShoppingAddressActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
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
                        finish();
                    } else {
                        Toast.makeText(NewShoppingAddressActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg != null) {
                    Toast.makeText(NewShoppingAddressActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        editUserView = (InputTextCell) findViewById(R.id.new_shopping_address_user_name);
        editUserView.setTextAndValue("联系人:", "", "请输入联系人", false, true);
        editPhoneView = (InputTextCell) findViewById(R.id.new_shopping_address_user_phone);
        editPhoneView.setInputType(InputType.TYPE_CLASS_PHONE);
        editPhoneView.setTextAndValue("联系人电话:", "", "请输入联系人电话", false, true);
        editAddressDetailView = (InputTextCell) findViewById(R.id.new_shopping_address_detail);
        editAddressDetailView.setMultilineValue(true);
        editAddressDetailView.setTextAndValue("详细地址:", "", "请输入收货详细地址", false, true);
        editAddressIdView = (LocationAddressInputCell) findViewById(R.id.new_shopping_address_address);
        editAddressIdView.setTextAndValue("所在地区:", "", "", "", false, true);
        editAddressIdView.setDelegate(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LocationAddressHelper.openLocationAddress(NewShoppingAddressActivity.this, 0, "所在地区选择")) {
                    LocationAddressHelper.syncServerLocationAddress(NewShoppingAddressActivity.this);
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        updateLocationAddress();
//        editAddressIdView.setOnCellClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!LocationAddressHelper.openLocationAddress(NewShoppingAddressActivity.this, 0, "所在地区选择")) {
//                    LocationAddressHelper.syncServerLocationAddress(NewShoppingAddressActivity.this);
//                }
//            }
//        });
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                locationNames = data.getStringArrayExtra(LocationAddressSelectActivity.RESULT_KEY_SELECTED_NAME);
                locationValues = data.getStringArrayExtra(LocationAddressSelectActivity.RESULT_KEY_SELECTED_VALUE);
                updateLocationAddress();
            }
        }
    }

    private void updateLocationAddress() {
        String province = "";
        String city = "";
        String county = "";
        if (locationValues != null) {
            if (locationValues.length > 0) {
                province = locationNames[0];
            }
            if (locationValues.length > 1) {
                city = locationNames[1];
            }
            if (locationValues.length > 2) {
                county = locationNames[2];
            }
        }
        editAddressIdView.setTextAndValue("所在地区:", province, city, county, false, true);
    }
}

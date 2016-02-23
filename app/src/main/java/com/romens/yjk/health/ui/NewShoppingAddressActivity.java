package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.romens.yjk.health.db.DBHelper;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DiscoveryDao;
import com.romens.yjk.health.db.dao.LocationAddressDao;
import com.romens.yjk.health.db.dao.ShopCarDao;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.db.entity.LocationAddressEntity;
import com.romens.yjk.health.ui.activity.BaseActivity;
import com.romens.yjk.health.ui.activity.LocationAddressSelectActivity;
import com.romens.yjk.health.ui.cells.InputTextCell;
import com.romens.yjk.health.ui.cells.LocationAddressInputCell;

import org.json.JSONException;
import org.json.JSONObject;

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

    private final String[] locationNames = new String[3];
    private final String[] locationValues = new String[3];

    private String userGuid;

    private AddressEntity toCommitEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onLocationAddressChanged);
        userGuid = UserGuidConfig.USER_GUID;
        setContentView(R.layout.activity_shopping_address, R.id.action_bar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ActionBar actionBar = getMyActionBar();
        actionBarEven(actionBar);

        initView();

        Intent intent = getIntent();
        toCommitEntity = (AddressEntity) intent.getSerializableExtra("responseUpDataEntity");
        if (toCommitEntity != null) {
            actionBar.setTitle("修改收货地址");
            locationValues[0] = toCommitEntity.getPROVINCE();
            LocationAddressDao dao = DBInterface.instance().openReadableDb().getLocationAddressDao();
            if (toCommitEntity.getREGION() != null) {
                locationValues[1] = dao.queryBuilder()
                        .where(LocationAddressDao.Properties.Key.eq(toCommitEntity.getREGION()))
                        .orderDesc(LocationAddressDao.Properties.Key)
                        .limit(1)
                        .unique().getParentId();
            }
            locationValues[2] = toCommitEntity.getREGION();
            initViewData(toCommitEntity);
        }
    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.onLocationAddressChanged);
        FacadeClient.cancel(this);
        super.onDestroy();
    }

    private void actionBarEven(ActionBar actionBar) {
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
        if (toCommitEntity != null) {
            entity.setADDRESSID(toCommitEntity.getADDRESSID());
        }
        entity.setRECEIVER(receiver);
        entity.setCONTACTPHONE(contectPhone);
        entity.setPROVINCE(guids[0]);
        entity.setCITY(guids[1]);
        entity.setREGION(guids[2]);
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

    private void initViewData(AddressEntity entity) {
        editUserView.setTextAndValue("联系人:", entity.getRECEIVER(), "", false, true);
        editPhoneView.setTextAndValue("联系人电话:", entity.getCONTACTPHONE(), "", false, true);
        editAddressDetailView.setTextAndValue("详细地址:", entity.getADDRESS(), "", false, true);
        editAddressIdView.setTextAndValue("所在地区:", entity.getPROVINCENAME(), entity.getCITYNAME(), entity.getREGIONNAME(), false, true);
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
                final int deep = 0;
                if (!LocationAddressHelper.openLocationAddress(NewShoppingAddressActivity.this, deep, "所在地区选择")) {
                    LocationAddressHelper.syncServerLocationAddress(NewShoppingAddressActivity.this);
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int deep = 1;
                String name = locationNames[deep - 1];
                String value = locationValues[deep - 1];
                LocationAddressHelper.openLocationCityOrCountySelect(NewShoppingAddressActivity.this, deep, name, value);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int deep = 2;
                String name = locationNames[deep - 1];
                String value = locationValues[deep - 1];
                LocationAddressHelper.openLocationCityOrCountySelect(NewShoppingAddressActivity.this, deep, name, value);
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
        if (requestCode == 0 || requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String[] names = data.getStringArrayExtra(LocationAddressSelectActivity.RESULT_KEY_SELECTED_NAME);
                String[] values = data.getStringArrayExtra(LocationAddressSelectActivity.RESULT_KEY_SELECTED_VALUE);
                int length = values.length;
                for (int i = requestCode; i < 3; i++) {
                    if (length == 3) {
                        locationNames[i] = names[i];
                        locationValues[i] = values[i];
                    } else if (length == 2) {
                        locationNames[i] = names[i - requestCode];
                        locationValues[i] = values[i - requestCode];
                    } else if (length == 1) {
                        locationNames[i] = names[i - requestCode];
                        locationValues[i] = values[i - requestCode];
                    }
                }
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

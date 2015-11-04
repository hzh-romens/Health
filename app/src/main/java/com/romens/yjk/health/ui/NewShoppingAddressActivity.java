package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
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
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.CitysDao;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.db.entity.CitysEntity;
import com.romens.yjk.health.ui.activity.LocationAddressSelectActivity;
import com.romens.yjk.health.ui.cells.InputTextCell;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import widget.OnWheelChangedListener;
import widget.WheelView;
import widget.adapters.ArrayWheelAdapter;

/**
 * Created by anlc on 2015/9/17.
 * 添加一个新的收获地址
 */
public class NewShoppingAddressActivity extends BaseActivity implements OnWheelChangedListener, AppNotificationCenter.NotificationCenterDelegate {

    private InputTextCell editUserView;
    private InputTextCell editPhoneView;
    private InputTextCell editAddressIdView;
    private InputTextCell editAddressDetailView;

    private List<CitysEntity> entities;

    private String[] provinces;
    private String[] citys;
    private String[] districts;

    private WheelView provinceView;
    private WheelView cityView;
    private WheelView districtView;

    private List<CitysEntity> provinceList;
    private List<CitysEntity> cityList;
    private List<CitysEntity> districtList;

    private boolean isExecutInitList = false;
    private String userGuid = "3333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.onLocationAddressChanged);
        userGuid = UserGuidConfig.USER_GUID;
        setContentView(R.layout.activity_shopping_address, R.id.action_bar);
        actionBarEven();

        queryDb();
        initView();
        initProvinces();
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
                    String districtGuid = "";
                    if (isExecutInitList) {
                        districtGuid = districtList.get(districtView.getCurrentItem()).getGuid();
                        if (districtGuid == null && districtGuid.equals("")) {
                            districtGuid = cityList.get(cityView.getCurrentItem()).getGuid();
                        }
                    }
                    if (receiver == null || receiver.equals("")) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请输入联系人", Toast.LENGTH_SHORT).show();
                    } else if (contectPhone == null || contectPhone.equals("")) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请输入联系方式", Toast.LENGTH_SHORT).show();
                    } else if (districtGuid == null || districtGuid.equals("")) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请选择地址", Toast.LENGTH_SHORT).show();
                    } else if (AddressDetail == null || AddressDetail.equals("")) {
                        Toast.makeText(NewShoppingAddressActivity.this, "请输入详细地址", Toast.LENGTH_SHORT).show();
                    } else {
                        saveAddress2Sevice(receiver, contectPhone, districtGuid, AddressDetail);
                    }
                }
            }
        });
    }

    private void saveAddress2Sevice(String receiver, String contectPhone, String distrctId, String AddressDetail) {
        AddressEntity entity = new AddressEntity();
        entity.setADDRESSID("");
        entity.setRECEIVER(receiver);
        entity.setCONTACTPHONE(contectPhone);
        entity.setDISTRCTID(distrctId);
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
        editUserView.setTextAndValue("联系人:", "", "请输入联系人", true, true);
        editPhoneView = (InputTextCell) findViewById(R.id.new_shopping_address_user_phone);
        editPhoneView.setInputType(InputType.TYPE_CLASS_PHONE);
        editPhoneView.setTextAndValue("联系人电话:", "", "请输入联系人电话", true, true);
        editAddressDetailView = (InputTextCell) findViewById(R.id.new_shopping_address_detail);
        editAddressDetailView.setMultilineValue(true);
        editAddressDetailView.setTextAndValue("详细地址:", "", "请输入收货详细地址", false, true);
        editAddressIdView = (InputTextCell) findViewById(R.id.new_shopping_address_address);
        editAddressIdView.setMultilineValue(true);
        editAddressIdView.setInputEnable(false);
        editAddressIdView.setTextAndValue("所在地区:", "", "点击选择所在地区", false, true);
        editAddressIdView.setOnCellClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!LocationAddressHelper.openLocationAddress(NewShoppingAddressActivity.this, 0,"所在地区选择")) {
                    LocationAddressHelper.syncServerLocationAddress(NewShoppingAddressActivity.this);
                }
                //chooseCityPopupWindow(editAddressIdView);
            }
        });
    }

    private void wheelViewEvent(View view) {
        provinceView = (WheelView) view.findViewById(R.id.id_province);
        cityView = (WheelView) view.findViewById(R.id.id_city);
        districtView = (WheelView) view.findViewById(R.id.id_district);
        provinceView.setWheelBackground(R.color.white);
        cityView.setWheelBackground(R.color.white);
        districtView.setWheelBackground(R.color.white);

        provinceView.addChangingListener(this);
        cityView.addChangingListener(this);
        cityView.addChangingListener(this);
    }


    public void chooseCityPopupWindow(View showAsView) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_city, null);
        TextView close = (TextView) view.findViewById(R.id.dialog_choose_city_close);
        TextView finish = (TextView) view.findViewById(R.id.dialog_choose_city_finish);
        wheelViewEvent(view);
        upDataProvinces();
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setAnimationStyle(R.anim.choose_citys_anim);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white));
//        popupWindow.showAsDropDown(showAsView);
        popupWindow.showAtLocation(showAsView, Gravity.BOTTOM, 0, 0);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressInfo = provinces[provinceView.getCurrentItem()] + " " + citys[cityView.getCurrentItem()] + " " + districts[districtView.getCurrentItem()];
                editAddressIdView.changeValue(addressInfo);
                popupWindow.setAnimationStyle(R.anim.choose_citys_anim_exit);
                popupWindow.dismiss();
            }
        });
    }

//    public void chooseCityDialog() {
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_city, null);
//        Button button = (Button) view.findViewById(R.key.shopping_address_choose_address_save);
//
//        wheelViewEvent(view);
//        upDataProvinces();
//
//        final Dialog dialog = new AlertDialog.Builder(this).create();
//        dialog.show();
//        dialog.setContentView(view);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String addressInfo = provinces[provinceView.getCurrentItem()] + " " + citys[cityView.getCurrentItem()] + " " + districts[districtView.getCurrentItem()];
//                editAddressIdView.setText(addressInfo);
//                dialog.dismiss();
//            }
//        });
//    }

    public void queryDb() {
        CitysDao dao = DBInterface.instance().openReadableDb().getCitysDao();
        entities = dao.queryBuilder().list();
    }

    public void initProvinces() {
        if (entities.size() > 0) {
            provinceList = new ArrayList<>();
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getLevel().equals("")) {
                    provinceList.add(entities.get(i));
                }
            }
            provinces = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                provinces[i] = provinceList.get(i).getName();
            }
        }
    }

    public void initCitys(String parentId) {
        if (entities != null && entities.size() > 0) {
            cityList = new ArrayList<>();
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getParentGuid().equals(parentId)) {
                    cityList.add(entities.get(i));
                }
            }
            citys = new String[cityList.size()];
            for (int i = 0; i < cityList.size(); i++) {
                citys[i] = cityList.get(i).getName();
            }
        }
    }

    public void initDistricts(String parentId) {
        if (entities != null && entities.size() > 0) {
            districtList = new ArrayList<>();
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getParentGuid().equals(parentId)) {
                    districtList.add(entities.get(i));
                }
            }
            districts = new String[districtList.size()];
            for (int i = 0; i < districtList.size(); i++) {
                districts[i] = districtList.get(i).getName();
            }
        }
    }

    private void upDataProvinces() {
        isExecutInitList = true;
        if (provinces != null && provinces.length > 0) {
            provinceView.setViewAdapter(new ArrayWheelAdapter<String>(NewShoppingAddressActivity.this, provinces));
            provinceView.setVisibleItems(7);
            cityView.setVisibleItems(7);
            districtView.setVisibleItems(7);
            upDataCtiry();
        }
    }

    private void upDataDistrict() {
        int position = cityView.getCurrentItem();
        if (cityList != null && cityList.size() > 0) {
            String parentId = cityList.get(position).getGuid();
            initDistricts(parentId);

            districtView.setViewAdapter(new ArrayWheelAdapter<String>(this, districts));
            districtView.setCurrentItem(0);
        }
    }

    private void upDataCtiry() {
        int position = provinceView.getCurrentItem();
        if (provinceList != null && provinceList.size() > 0) {
            String parentId = provinceList.get(position).getGuid();
            initCitys(parentId);

            cityView.setViewAdapter(new ArrayWheelAdapter<String>(this, citys));
            cityView.setCurrentItem(0);
            upDataDistrict();
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == provinceView) {
            upDataCtiry();
        } else if (wheel == cityView) {
            upDataDistrict();
        } else if (wheel == districtView) {

        }
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String[] names = data.getStringArrayExtra(LocationAddressSelectActivity.RESULT_KEY_SELECTED_NAME);
                String[] values = data.getStringArrayExtra(LocationAddressSelectActivity.RESULT_KEY_SELECTED_VALUE);
                StringBuilder address = new StringBuilder();
                for (String name :
                        names) {
                    address.append(name);
                    address.append("-");
                }
                String locationAddress = address.toString();
                if (locationAddress.endsWith("-")) {
                    locationAddress = locationAddress.substring(0, locationAddress.length() - 1);
                }
                editAddressIdView.changeValue(locationAddress);
            }
        }
    }
}

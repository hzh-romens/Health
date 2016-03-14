package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avast.android.dialogs.iface.IListDialogListener;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.CommitOrderEntity;
import com.romens.yjk.health.model.DeliverytypeEntity;
import com.romens.yjk.health.model.FilterChildEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.adapter.CommitOrderAdapter;
import com.romens.yjk.health.ui.utils.DialogUtils;
import com.romens.yjk.health.ui.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by AUSU on 2015/9/20.
 * 订单提交
 */
public class CommitOrderActivity extends BaseActivity implements IListDialogListener {
    private ExpandableListView expandableListView;
    private ImageView back;
    private TextView address, sumMoneys, accounts, person;
    private CommitOrderAdapter adapter;

    private HashMap<String, List<ShopCarEntity>> childData;
    private List<ParentEntity> parentData;
    private int sumCount;
    private double sumMoney;
    private String addressId; //送货地址id
    private String deliveryType;//送货方式
    private String mBillName; //发票抬头名称
    private boolean needBill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitorder);
        initView();
        getData();
        setData();
    }


    private void initView() {
        expandableListView = (ExpandableListView) findViewById(R.id.ev);
        back = (ImageView) findViewById(R.id.btn_back);
        sumMoneys = (TextView) findViewById(R.id.tv_content);
        accounts = (TextView) findViewById(R.id.accounts);
        addHeadView();
        needShowProgress("正在加载...");
    }

    private void addHeadView() {
        View view = getLayoutInflater().inflate(R.layout.list_item_address, null);
        person = (TextView) view.findViewById(R.id.person);
        address = (TextView) view.findViewById(R.id.address);
        expandableListView.addHeaderView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIOpenHelper.openControlAddressActivityForResult(CommitOrderActivity.this, 2);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UIOpenHelper.openShopCarActivityWithAnimation(CommitOrderActivity.this);
                finish();
            }
        });

    }

    private void getData() {
        getAdressData();
        getIntentData();
        getSendData();
        getCuopon();
    }


    //获取默认的收货地址信息
    public void getAdressData() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).put("DEFAULTFLAG", "1").build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserAddressList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetUserAddressList", "ERROR");
                needHideProgress();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;

                    try {
                        JSONArray jsonArray = new JSONArray(responseProtocol.getResponse());
                        if (jsonArray.length() == 0) {
                            showBuilder();
                        } else {
                            Gson gson = new Gson();
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String addressValue = jsonObject.getString("ADDRESSID");
                            person.setText("收货人：" + jsonObject.getString("RECEIVER") + " " + jsonObject.getString("CONTACTPHONE"));
                            address.setText(jsonObject.getString("ADDRESS"));
                            addressId = addressValue;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("GetUserAddressList", errorMsg.msg);
                }
            }
        });
    }

    private void getIntentData() {
        childData = (HashMap<String, List<ShopCarEntity>>) getIntent().getSerializableExtra("childData");
        parentData = (List<ParentEntity>) getIntent().getSerializableExtra("parentData");
    }

    private void setData() {
        getAll(childData);
        getTypeData();
        adapter = new CommitOrderAdapter(this, parentData.size() + 1);
        expandableListView.setAdapter(adapter);
        adapter.SetData(parentData, childData, parentTypes);
        //获取派送方式
        adapter.setFragmentManger(getSupportFragmentManager());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < parentData.size(); i++) {
                    expandableListView.expandGroup(i);
                }
            }
        });
        adapter.setSwitchChangeListener(new CommitOrderAdapter.SwitchChangeListener() {
            @Override
            public void getSwitchValue(boolean value) {
                needBill = value;

            }
        });
        adapter.setEditTextChangeListener(new CommitOrderAdapter.EditTextChangeListener() {
            @Override
            public void textValueChange(String value) {
                mBillName = value;
            }
        });
    }

    private List<String> parentTypes;

    private void getTypeData() {
        parentTypes = new ArrayList<>();
        for (int i = 1; i <= parentData.size() + 7; i++) {
            parentTypes.add(i + "");
        }
    }

    public void getAll(HashMap<String, List<ShopCarEntity>> childData) {
        Iterator iter = childData.entrySet().iterator();
        final List<FilterChildEntity> filteData = new ArrayList<FilterChildEntity>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
            for (int i = 0; i < child.size(); i++) {
                ShopCarEntity shopCarEntity = child.get(i);
                sumCount = sumCount + shopCarEntity.getBUYCOUNT();
                sumMoney = sumMoney + shopCarEntity.getBUYCOUNT() * shopCarEntity.getGOODSPRICE();
                FilterChildEntity filterChildEntity = toFilterEntity(shopCarEntity);
                filteData.add(filterChildEntity);
            }
        }

        String counts = sumCount + "";
        String moneys = "¥" + UIUtils.getDouvleValue(sumMoney + "");
        sumMoneys.setText(getColorText(counts, moneys));

        //订单提交，并跳转到下一个页面
        accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needShowProgress("正在提交..");
                if (deliveryType != null && !("".equals(deliveryType))) {
                    if (needBill) {
                        if ("".equals(mBillName) || mBillName != null || (mBillName == null)) {
                            needHideProgress();
                            DialogUtils dialogUtils = new DialogUtils();
                            dialogUtils.show_infor("请输入发票内容", CommitOrderActivity.this, "提示");
                        } else {
                            commitOrder(filteData, sumCount);
                        }
                    }

                } else {
                    needHideProgress();
                    DialogUtils dialogUtils = new DialogUtils();
                    dialogUtils.show_infor("请选择派送方式", CommitOrderActivity.this, "提示");
                }

            }
        });
    }

    public SpannableStringBuilder getColorText(String counts, String moneys) {
        String str1 = "共";
        String str2 = "件  总金额 ";
        String textStr = str1 + counts + str2 + moneys;
        SpannableStringBuilder builder = new SpannableStringBuilder(textStr);
        // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan greedSpan = new ForegroundColorSpan(getResources().getColor(R.color.theme_primary));
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(getResources().getColor(R.color.theme_primary));
        builder.setSpan(blackSpan, str1.length(), str1.length() + counts.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(greedSpan, str1.length() + counts.length() + str2.length(), textStr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return builder;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            AddressEntity addressEntity = (AddressEntity) data.getSerializableExtra("responseCommitEntity");
            person.setText("收货人：" + addressEntity.getRECEIVER() + " " + addressEntity.getCONTACTPHONE());
            address.setText(addressEntity.getADDRESS());
            addressId = addressEntity.getADDRESSID();
        } else if (resultCode == 3) {
            showBuilder();
        }
    }


    //向服务器提交订单
    private void commitOrder(List<FilterChildEntity> data, final int count) {
        String jsonData = getJsonData(data, deliveryType, addressId, mBillName);
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).put("JSONDATA", jsonData).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "saveOrder", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetBuyCarCount", "ERROR");
                needHideProgress();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    Log.i("responseProtocol", responseProtocol.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        String success = jsonObject.getString("success");
                        Intent i = new Intent(CommitOrderActivity.this, CommitResultActivity.class);
                        if (success.equals("yes")) {
                            i.putExtra("success", "true");
                            i.putExtra("sumMoney", sumMoney + "");
                            i.putExtra("orderNumber", jsonObject.getString("msg1"));
                            i.putExtra("time", jsonObject.getString("msg2"));
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -count);
                        } else {
                            String errorMsgs = jsonObject.getString("errorMsg");
                            i.putExtra("success", "false");
                            i.putExtra("errormsg", errorMsgs);
                        }
                        startActivity(i);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("CommitOrder", errorMsg.msg);
                }
            }
        });
    }

    public String getJsonData(List<FilterChildEntity> data, String deliverytype, String addressid, String billName) {
        Gson gson = new Gson();
        CommitOrderEntity commitOrderEntity = new CommitOrderEntity();
        commitOrderEntity.setDELIVERYTYPE(deliverytype);
        commitOrderEntity.setADDRESSID(addressid);
        commitOrderEntity.setGOODSLIST(data);
        commitOrderEntity.setBILLNAME(billName);
        commitOrderEntity.setCOUPONGUID("");
        String JSON_DATA = gson.toJson(commitOrderEntity);
        return JSON_DATA;
    }


    private void showBuilder() {
        DialogUtils dialogUtils = new DialogUtils();
        dialogUtils.show_infor_two("请填写收货地址", this, "提示", new DialogUtils.ConfirmListenerCallBack() {
            @Override
            public void ConfirmListener() {
                UIOpenHelper.openShippingAddress(CommitOrderActivity.this, 0);
            }
        }, new DialogUtils.CancelListenerCallBack() {
            @Override
            public void CancelListener() {
                finish();
            }
        });
    }


    private List<DeliverytypeEntity> result = new ArrayList<DeliverytypeEntity>();

    public void getSendData() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetTransport", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetUserAddressList", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    List<LinkedTreeMap<String, String>> response = responseProtocol.getResponse();
                    for (LinkedTreeMap<String, String> item : response) {
                        DeliverytypeEntity entity = DeliverytypeEntity.mapToEntity(item);
                        result.add(entity);
                    }
                    adapter.SetDeliverytypeData(result);
                } else {
                    Log.e("GetUserAddressList", errorMsg.msg);
                }
            }
        });
    }

    public FilterChildEntity toFilterEntity(ShopCarEntity shopCarEntity) {
        FilterChildEntity filterChildEntity = new FilterChildEntity();
        filterChildEntity.setSHOPID(shopCarEntity.getSHOPID());
        filterChildEntity.setBUYCOUNT(shopCarEntity.getBUYCOUNT() + "");
        filterChildEntity.setGOODSGUID(shopCarEntity.getGOODSGUID());
        filterChildEntity.setGOODSPRICE(shopCarEntity.getGOODSPRICE() + "");
        return filterChildEntity;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // UIOpenHelper.openShopCarActivityWithAnimation(CommitOrderActivity.this);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onListItemSelected(CharSequence charSequence, int number, int requestCode) {
        if (requestCode == 9) {
            adapter.SetValue(charSequence.toString());
            for (int i = 0; i < result.size(); i++) {
                if (charSequence.toString().equals(result.get(i).getNAME())) {
                    deliveryType = result.get(i).getGUID();
                }
            }
        }
    }

    public void getCuopon() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetCoupon", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetCoupon", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                } else {
                    Log.e("GetCoupon", errorMsg.msg);
                }
            }
        });
    }
}

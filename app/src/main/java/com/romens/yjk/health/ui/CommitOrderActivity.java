package com.romens.yjk.health.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avast.android.dialogs.iface.IListDialogListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.model.CommitOrderEntity;
import com.romens.yjk.health.model.DeliverytypeEntity;
import com.romens.yjk.health.model.FilterChildEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.adapter.CommitOrderAdapter;
import com.romens.yjk.health.ui.components.CustomDialog;
import com.romens.yjk.health.ui.utils.DialogUtils;

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
public class CommitOrderActivity extends BaseActivity implements IListDialogListener{
    private ExpandableListView expandableListView;
    private ImageView back;
    private HashMap<String, List<ShopCarEntity>> childData;
    private List<ParentEntity> parentData;
    private TextView person;
    private TextView address, tv_content, accounts;
    private CommitOrderAdapter adapter;
    private int sumCount;
    private double sumMoney;
    private CustomDialog.Builder ibuilder;

    private String ADDRESSID; //送货地址id
    private String DELIVERYTYPE;//送货方式


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commitorder);
        initView();
        needShowProgress("正在加载...");
        getAdressData();
        childData = (HashMap<String, List<ShopCarEntity>>) getIntent().getSerializableExtra("childData");
        parentData = (List<ParentEntity>) getIntent().getSerializableExtra("parentData");
        getAll(childData);
        adapter = new CommitOrderAdapter(this,parentData.size()+1);
        getSendData();
        expandableListView.setAdapter(adapter);
        adapter.SetData(parentData, childData);
        //获取派送方式
        adapter.setFragmentManger(getSupportFragmentManager());
        adapter.setCheckDataChangeListener(new CommitOrderAdapter.CheckDataCallBack() {
            @Override
            public void getCheckData(String flag) {
//                if ("药店派送".equals(flag)) {
//                    DELIVERYTYPE = "55b30f7d31f2f";
//                } else {
//                    DELIVERYTYPE = "23B70F47-45D6-4ECE-8A3A-13CC92DEA4B1";
//                }

                Log.i("DELIVERYTYPE=======",DELIVERYTYPE);
            }
        });
        //将ExpandAbleListView的每个parentItem展开
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < parentData.size(); i++) {
                    expandableListView.expandGroup(i);
                }
            }
        });


    }

    public void getAll(HashMap<String, List<ShopCarEntity>> childData) {
        Iterator iter = childData.entrySet().iterator();
        final List<FilterChildEntity> filteData = new ArrayList<FilterChildEntity>();
        while (iter.hasNext()) {
            ParentEntity fatherEntity = new ParentEntity();
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
            for (int i = 0; i < child.size(); i++) {
                sumCount = sumCount + child.get(i).getBUYCOUNT();
                sumMoney = sumMoney + child.get(i).getBUYCOUNT() * child.get(i).getGOODSPRICE();
                FilterChildEntity filterChildEntity = new FilterChildEntity();
                filterChildEntity.setSHOPID(child.get(i).getSHOPID());
                filterChildEntity.setBUYCOUNT(child.get(i).getBUYCOUNT() + "");
                filterChildEntity.setGOODSGUID(child.get(i).getGOODSGUID());
                filterChildEntity.setGOODSPRICE(child.get(i).getGOODSPRICE() + "");
                filteData.add(filterChildEntity);
            }
        }
        String countStr = sumCount + "";
        String moneyStr = "¥" + sumMoney;
        String str1 = "共";
        String str2 = "件  总金额";
        String textStr = str1 + countStr + str2 + moneyStr;
        if (!TextUtils.isEmpty(textStr)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(textStr);
            // ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
            ForegroundColorSpan greedSpan = new ForegroundColorSpan(getResources().getColor(R.color.accouns_color));
            ForegroundColorSpan blackSpan = new ForegroundColorSpan(getResources().getColor(R.color.accouns_color));
            builder.setSpan(blackSpan, str1.length(), str1.length() + countStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(greedSpan, str1.length() + countStr.length() + str2.length(), textStr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tv_content.setText(builder);
        } else {
            tv_content.setText("");
        }
        //订单提交，并跳转到下一个页面
        accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needShowProgress("正在提交..");
                if (DELIVERYTYPE != null && !("".equals(DELIVERYTYPE))) {
                    commitOrder(filteData, sumCount);
                } else {
                    needHideProgress();
                    DialogUtils dialogUtils = new DialogUtils();
                    dialogUtils.show_infor("请选择派送方式", CommitOrderActivity.this, "提示");
                }

            }
        });
    }

    private void initView() {
        expandableListView = (ExpandableListView) findViewById(R.id.ev);
        back = (ImageView) findViewById(R.id.btn_back);
        tv_content = (TextView) findViewById(R.id.tv_content);
        accounts = (TextView) findViewById(R.id.accounts);
        //头部添加地址
        View view = getLayoutInflater().inflate(R.layout.list_item_address, null);
        person = (TextView) view.findViewById(R.id.person);
        address = (TextView) view.findViewById(R.id.address);
        expandableListView.addHeaderView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CommitOrderActivity.this, ControlAddressActivity.class);
                i.putExtra("chose", "chose");
                startActivityForResult(i, 2);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CommitOrderActivity.this, ShopCarActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

    }

    //startActivityForResult数据回调回来
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            //对从ControlAddressActivity返回的数据进行处理
            AddressEntity addressEntity= (AddressEntity)data.getSerializableExtra("responseCommitEntity");
            person.setText("收货人：" + addressEntity.getRECEIVER()+ " " + addressEntity.getCONTACTPHONE());
            address.setText(addressEntity.getADDRESS());
            ADDRESSID = addressEntity.getADDRESSID();
        }
    }

    //向服务器提交订单
    private void commitOrder(List<FilterChildEntity> data, final int count) {
        Gson gson = new Gson();
        CommitOrderEntity commitOrderEntity = new CommitOrderEntity();
        commitOrderEntity.setDELIVERYTYPE(DELIVERYTYPE);
        commitOrderEntity.setADDRESSID(ADDRESSID);
        commitOrderEntity.setGOODSLIST(data);
        String JSON_DATA = gson.toJson(commitOrderEntity);
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).put("JSONDATA", JSON_DATA).build();
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
                            ibuilder = new CustomDialog.Builder(CommitOrderActivity.this);
                            ibuilder.setTitle(R.string.prompt);
                            ibuilder.setMessage("请填写收货地址");
                            ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(CommitOrderActivity.this, NewShoppingAddressActivity.class));
                                    finish();
                                }
                            });
                            ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            ibuilder.create().show();
                        } else {
                            Gson gson = new Gson();
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String addressid = jsonObject.getString("ADDRESSID");
                            person.setText("收货人：" + jsonObject.getString("RECEIVER") + " " + jsonObject.getString("CONTACTPHONE"));
                            address.setText(jsonObject.getString("ADDRESS"));
                            ADDRESSID = addressid;

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

    private List<DeliverytypeEntity> result;
    public void getSendData() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetTransport", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetUserAddressList", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Gson gson = new Gson();
                    result = gson.fromJson(response, new TypeToken<List<DeliverytypeEntity>>() {
                    }.getType());
                    adapter.SetDeliverytypeData(result);

                } else {
                    Log.e("GetUserAddressList", errorMsg.msg);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, ShopCarActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onListItemSelected(CharSequence charSequence, int number, int requestCode) {
      if(requestCode==9){
          adapter.SetValue(charSequence.toString());
          for (int i = 0; i < result.size(); i++) {
              if(charSequence.toString().equals(result.get(i).getNAME())){
                  DELIVERYTYPE=result.get(i).getGUID();
              }
          }
      }
    }
}

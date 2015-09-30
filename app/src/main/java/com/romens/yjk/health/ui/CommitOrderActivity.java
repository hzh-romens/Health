package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.model.CommitOrderEntity;
import com.romens.yjk.health.model.FilterChildEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.adapter.CommitOrderAdapter;
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
public class CommitOrderActivity extends BaseActivity {
    private ExpandableListView ev;
    private FrameLayout back;
    private HashMap<String, List<ShopCarEntity>> childData;
    private List<ParentEntity> parentData;
    private TextView person;
    private TextView address, tv_content, accounts;
    private CommitOrderAdapter adapter;
    private int sumCount;
    private double sumMoney;

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
        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setCheck("false");
        parentEntity.setShopID("-1");
        parentEntity.setShopName("1");
        parentData.add(parentEntity);
        List<ShopCarEntity> shopCarEntities = new ArrayList<ShopCarEntity>();
        ShopCarEntity shopCarEntity = new ShopCarEntity();
        shopCarEntity.setCHECK("false");
        shopCarEntity.setSHOPID("-1");
        shopCarEntities.add(shopCarEntity);
        childData.put("-1", shopCarEntities);
        adapter = new CommitOrderAdapter(this);
        ev.setAdapter(adapter);
        adapter.SetData(parentData, childData);
        //获取派送方式
        adapter.setCheckDataChangeListener(new CommitOrderAdapter.CheckDataCallBack() {
            @Override
            public void getCheckData(String flag) {
                if("药店派送".equals(flag)){
                    DELIVERYTYPE="1";
                }else{
                    DELIVERYTYPE="2";
                }
            }
        });
        //将ExpandAbleListView的每个parentItem展开
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < parentData.size() - 1; i++) {
                    ev.expandGroup(i);
                }
            }
        });


    }

    public void getAll(HashMap<String, List<ShopCarEntity>> childData) {
        Iterator iter = childData.entrySet().iterator();
        final List<FilterChildEntity> filteData=new ArrayList<FilterChildEntity>();
        while (iter.hasNext()) {
            ParentEntity fatherEntity = new ParentEntity();
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
            for (int i = 0; i < child.size(); i++) {
                sumCount = sumCount + child.get(i).getBUYCOUNT();
                sumMoney = sumMoney + child.get(i).getBUYCOUNT() * child.get(i).getGOODSPRICE();
                FilterChildEntity filterChildEntity=new FilterChildEntity();
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
                Log.i("DELIVERYTYPE","======"+DELIVERYTYPE);
                if(DELIVERYTYPE!=null&&!("".equals(DELIVERYTYPE))){
                    commitOrder(filteData);
                }else{
                    needHideProgress();
                    DialogUtils dialogUtils=new DialogUtils();
                    dialogUtils.show_infor("请选择派送方式",CommitOrderActivity.this,"提示");
                }

            }
        });
    }

    private void initView() {
        ev = (ExpandableListView) findViewById(R.id.ev);
        back = (FrameLayout) findViewById(R.id.btn_back);
        tv_content = (TextView) findViewById(R.id.tv_content);
        accounts = (TextView) findViewById(R.id.accounts);
        //头部添加地址
        View view = getLayoutInflater().inflate(R.layout.list_item_address, null);
        person = (TextView) view.findViewById(R.id.person);
        address = (TextView) view.findViewById(R.id.address);
        ev.addHeaderView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent i = new Intent(CommitOrderActivity.this, ControlAddressActivity.class);
              startActivityForResult(i, 2);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //startActivityForResult数据回调回来
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            //对从ControlAddressActivity返回的数据进行处理
        }
    }

    //向服务器提交订单
    private void commitOrder(List<FilterChildEntity> data) {
        Gson gson=new Gson();
        CommitOrderEntity commitOrderEntity=new CommitOrderEntity();
        commitOrderEntity.setDELIVERYTYPE(DELIVERYTYPE);
        commitOrderEntity.setADDRESSID(ADDRESSID);
        commitOrderEntity.setGOODSLIST(data);
        String JSON_DATA = gson.toJson(commitOrderEntity);
        Log.i("DELIVERYTYPE-ADDRESSID+", DELIVERYTYPE + "=" + ADDRESSID);
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", "2222").put("JSONDATA",JSON_DATA).build();
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
                    Log.i("提交订单返回数据",responseProtocol.getResponse());
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        String success = jsonObject.getString("success");
                        Intent i = new Intent(CommitOrderActivity.this, CommitResultActivity.class);
                        if (success.equals("yes")) {
                            i.putExtra("success", "true");
                            i.putExtra("sumMoney", sumMoney + "");
                            i.putExtra("orderNumber", jsonObject.getString("msg1"));
                            i.putExtra("time", jsonObject.getString("msg2"));
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
    public void getAdressData(){
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", "2222").put("DEFAULT_FLAG", "1").build();
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
                    Log.i("收货地址-----",responseProtocol.getResponse());
                    //判空处理
                    if(responseProtocol.getResponse()==null||"".equals(responseProtocol.getResponse())){
                        //如果为空
                    }else {
                        try {
                            JSONArray jsonArray = new JSONArray(responseProtocol.getResponse());
                            Gson gson = new Gson();
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String addressid = jsonObject.getString("ADDRESSID");
                            person.setText("收货人："+jsonObject.getString("RECEIVER")+" "+jsonObject.getString("CONTACTPHONE"));
                            address.setText(jsonObject.getString("ADDRESS"));
                            ADDRESSID = addressid;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e("GetUserAddressList", errorMsg.msg);
                }
            }
        });
    }
}

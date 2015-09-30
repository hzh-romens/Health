package com.romens.yjk.health.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.model.GoodsEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.adapter.ShopAdapter;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;
import com.romens.yjk.health.ui.utils.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ShopCarActivity extends BaseActivity {
    private ExpandableListView ev;
    private CheckableFrameLayout all_choice;
    private TextView tv_all1;
    private TextView iv_accounts;
    private List<ShopCarEntity> datas;
    private ShopAdapter myAdapter;
    private FrameLayout btn_back;
    private TextView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car);
        initView();
        //获取数据
        needShowProgress("正在加载...");
        myAdapter = new ShopAdapter(this);
        ev.setAdapter(myAdapter);
        requestShopCarDataChanged();


    }

    private void initView() {
        all_choice = (CheckableFrameLayout) findViewById(R.id.all_choice);
        tv_all1 = (TextView) findViewById(R.id.tv_all1);
        iv_accounts = (TextView) findViewById(R.id.accounts);
        ev = (ExpandableListView) findViewById(R.id.ev);
        btn_back = (FrameLayout) findViewById(R.id.btn_back);
        edit = (TextView) findViewById(R.id.edit);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //向服务器提交购物车信息并跳转到订单页面
        iv_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送请求
                needShowProgress("正在提交");
                HashMap<String, List<ShopCarEntity>> childData = myAdapter.getChildData();
                ArrayList<ParentEntity> parentData = (ArrayList<ParentEntity>) myAdapter.getParentData();
                ArrayList<ParentEntity> filterParentData = new ArrayList<ParentEntity>();
                HashMap<String, List<ShopCarEntity>> filterChildData = new HashMap<String, List<ShopCarEntity>>();
                ArrayList<GoodsEntity> JSON_DATA = new ArrayList<GoodsEntity>();
                if (parentData != null) {
                    for (int i = 0; i < parentData.size(); i++) {
                        if ("true".equals(parentData.get(i).getCheck())) {
                            filterParentData.add(parentData.get(i));
                        }
                    }

                    Iterator iter = childData.entrySet().iterator();
                    while (iter.hasNext()) {
                        ParentEntity fatherEntity = new ParentEntity();
                        Map.Entry entry = (Map.Entry) iter.next();
                        String key = (String) entry.getKey();
                        List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
                        List<ShopCarEntity> mChild = new ArrayList<ShopCarEntity>();
                        for (int i = 0; i < child.size(); i++) {
                            if ("true".equals(child.get(i).getCHECK())) {
                                mChild.add(child.get(i));
                            }
                            GoodsEntity goodsEntity = new GoodsEntity();
                            goodsEntity.setBUYCOUNT(child.get(i).getBUYCOUNT() + "");
                            goodsEntity.setGOODSGUID(child.get(i).getGOODSGUID() + "");
                            JSON_DATA.add(goodsEntity);
                        }
                        if (mChild != null) {
                            filterChildData.put(key, mChild);
                        }
                    }
                    Gson gson = new Gson();
                    String s = gson.toJson(JSON_DATA);
                    CommitData(s, filterChildData, filterParentData);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //获取购物车的信息
    private void requestShopCarDataChanged() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", "2222").build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserBuyCarList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {

            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Log.e("GetBuyCarCount", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    onResponseShopCarData(responseProtocol.getResponse());
                } else {
                    Log.e("GetBuyCarCount", "ERROR");
                }
            }
        });
    }

    private List<ShopCarEntity> result;
    List<ParentEntity> fatherEntities;//药店名称
    HashMap<String, List<ShopCarEntity>> childData;

    //获取购物车信息
    public void onResponseShopCarData(List<LinkedTreeMap<String, String>> ShopCarData) {
        int count = ShopCarData == null ? 0 : ShopCarData.size();
        if (count <= 0) {
            return;
        }
        List<ShopCarEntity> result = new ArrayList<ShopCarEntity>();
        for (LinkedTreeMap<String, String> item : ShopCarData) {
            ShopCarEntity entity = ShopCarEntity.mapToEntity(item);
            entity.setCHECK("true");
            result.add(entity);
        }
        //暂且不做数据库插入操作
        //  if (result.size() > 0) {
        //    ShopCarDao shopCarDao =DBInterface.instance().openWritableDb().getShopCarDao();
        //  shopCarDao.insertOrReplaceInTx(result);
        // }
        fatherEntities = new ArrayList<ParentEntity>();
        childData = new HashMap<String, List<ShopCarEntity>>();
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setCHECK("true");
            if (childData.containsKey(result.get(i).getSHOPID())) {
                List<ShopCarEntity> shopCarEntities = childData.get(result.get(i).getSHOPID());
                shopCarEntities.add(result.get(i));
                childData.put(result.get(i).getSHOPID(), shopCarEntities);
            } else {
                List<ShopCarEntity> data = new ArrayList<ShopCarEntity>();
                data.add(result.get(i));
                childData.put(result.get(i).getSHOPID(), data);
            }
        }

        Iterator iter = childData.entrySet().iterator();
        while (iter.hasNext()) {
            ParentEntity fatherEntity = new ParentEntity();
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
            fatherEntity.setShopID(key);
            fatherEntity.setShopName(child.get(0).getSHOPNAME());
            fatherEntity.setCheck("true");
            fatherEntities.add(fatherEntity);
        }
        myAdapter.bindData(fatherEntities, childData, new ShopAdapter.AdapterCallBack() {

            @Override
            public void UpdateData() {
                all_choice.setChecked(myAdapter.isAllSelected());
            }

            @Override
            public void UpdateMoney(String money) {
                tv_all1.setText("总计：" + money);
            }
        });


        all_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.switchAllSelect(!all_choice.isChecked());
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < fatherEntities.size(); i++) {
                    ev.expandGroup(i);
                }
            }
        });

    }

    private void bindData() {
        List<ShopCarEntity> result = DBInterface.instance().loadAllShopCar();

    }

    //向服务器提交购物车信息
    private void CommitData(String data, final HashMap<String, List<ShopCarEntity>> filterChildData, final ArrayList<ParentEntity> filterParentData) {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", "2222").put("JSONDATA", data).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "saveCart", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("CommitData", "ERROR");
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
                        if (success.equals("yes")) {
                            Intent i = new Intent(ShopCarActivity.this, CommitOrderActivity.class);
                            i.putExtra("childData", filterChildData);
                            i.putExtra("parentData", filterParentData);
                            startActivity(i);
                        } else {
                            Toast.makeText(ShopCarActivity.this, "出现异常，请您稍后再试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("ERROR", errorMsg.msg);
                }

            }
        });
    }


}

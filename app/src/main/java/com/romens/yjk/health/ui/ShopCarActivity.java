package com.romens.yjk.health.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.romens.extend.scanner.FinishListener;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.model.DeleteEntity;
import com.romens.yjk.health.model.GoodsEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.adapter.ShopAdapter;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;
import com.romens.yjk.health.ui.components.CustomDialog;
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
    private ExpandableListView expandableListView;
    private CheckableFrameLayout all_choice;
    private TextView tv_all1;
    private TextView iv_accounts;
    private List<ShopCarEntity> datas;
    private ShopAdapter myAdapter;
    private ImageView btn_back;
    private ImageView delete;
    private CustomDialog.Builder ibuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car);
        initView();
        //获取数据
        needShowProgress("正在加载...");
        myAdapter = new ShopAdapter(this);
        expandableListView.setAdapter(myAdapter);
        requestShopCarDataChanged();
        //向服务器提交购物车信息并跳转到订单页面
        iv_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送请求
                HashMap<String, List<ShopCarEntity>> childData = myAdapter.getChildData();
                ArrayList<ParentEntity> parentData = (ArrayList<ParentEntity>) myAdapter.getParentData();
                if (myAdapter.isAllNotSelected()) {
                    Toast.makeText(ShopCarActivity.this, "请选择一样商品", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<ParentEntity> mFilterParentData = new ArrayList<ParentEntity>();
                    if (childData != null && parentData != null) {
                        needShowProgress("正在提交");
                        ArrayList<ParentEntity> filterParentData = new ArrayList<ParentEntity>();
                        HashMap<String, List<ShopCarEntity>> filterChildData = new HashMap<String, List<ShopCarEntity>>();
                        ArrayList<GoodsEntity> JSON_DATA = new ArrayList<GoodsEntity>();
                        SparseBooleanArray parentStatus = myAdapter.getParentStatus();
                        if (parentData != null) {
                            for (int i = 0; i < parentData.size(); i++) {
                                //  if ("true".equals(parentData.get(i).getCheck())) {
                                if (parentStatus.get(i)) {
                                    filterParentData.add(parentData.get(i));
                                }
                                //}
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
                                ParentEntity filterParentEntity = new ParentEntity();
                                filterParentEntity.setShopID(key);
                                filterParentEntity.setShopName(child.get(0).getSHOPNAME());
                                mFilterParentData.add(filterParentEntity);
                            }
                            Gson gson = new Gson();
                            String s = gson.toJson(JSON_DATA);
                            CommitData(s, filterChildData, filterParentData);
                        }
                    } else {
                        Toast.makeText(ShopCarActivity.this, "购物车为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void initView() {
        all_choice = (CheckableFrameLayout) findViewById(R.id.all_choice);
        tv_all1 = (TextView) findViewById(R.id.tv_all1);
        iv_accounts = (TextView) findViewById(R.id.accounts);
        expandableListView = (ExpandableListView) findViewById(R.id.ev);
        btn_back = (ImageView) findViewById(R.id.back);
        delete = (ImageView) findViewById(R.id.edit);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myAdapter.getChildData();
                ibuilder = new CustomDialog.Builder(ShopCarActivity.this);
                ibuilder.setTitle(R.string.prompt);
                ibuilder.setMessage("是否删除？");
                ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, List<ShopCarEntity>> childData = myAdapter.getChildData();
                        HashMap<String, SparseBooleanArray> childStatusList = myAdapter.getChildStatusList();
                        if (myAdapter.isAllSelected()) {
                            Iterator iter = childData.entrySet().iterator();
                            List<ShopCarEntity> filterData = new ArrayList<ShopCarEntity>();
                            int count = 0;
                            while (iter.hasNext()) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                String key = (String) entry.getKey();
                                List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
                                SparseBooleanArray sparseBooleanArray = childStatusList.get(key);
                                for (int i = 0; i < child.size(); i++) {
                                    filterData.add(child.get(i));
                                    count = count + child.get(i).getBUYCOUNT();
                                }


                            }
                            List<DeleteEntity> deleteData = new ArrayList<DeleteEntity>();
                            for (int i = 0; i < filterData.size(); i++) {
                                DeleteEntity deleteEntity = new DeleteEntity();
                                deleteEntity.setMERCHANDISEID(filterData.get(i).getGOODSGUID());
                                deleteData.add(deleteEntity);
                            }
                            Gson gson = new Gson();
                            String s = gson.toJson(deleteData);
                            DeleteData(s, count);
                        } else if (myAdapter.isAllNotSelected()) {
                            Toast.makeText(ShopCarActivity.this, "请至少选择一件商品", Toast.LENGTH_SHORT).show();

                        } else {
                            if (childData != null) {
                                Iterator iter = childData.entrySet().iterator();
                                List<ShopCarEntity> filterData = new ArrayList<ShopCarEntity>();
                                int count = 0;
                                while (iter.hasNext()) {
                                    ParentEntity fatherEntity = new ParentEntity();
                                    Map.Entry entry = (Map.Entry) iter.next();
                                    String key = (String) entry.getKey();
                                    List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
                                    SparseBooleanArray sparseBooleanArray = childStatusList.get(key);
                                    for (int i = 0; i < sparseBooleanArray.size(); i++) {
                                        if (sparseBooleanArray.get(i)) {
                                            filterData.add(child.get(i));
                                            count = count + child.get(i).getBUYCOUNT();
                                        }
                                    }


                                }
                                List<DeleteEntity> deleteData = new ArrayList<DeleteEntity>();
                                for (int i = 0; i < filterData.size(); i++) {
                                    DeleteEntity deleteEntity = new DeleteEntity();
                                    deleteEntity.setMERCHANDISEID(filterData.get(i).getGOODSGUID());
                                    deleteData.add(deleteEntity);
                                }
                                Gson gson = new Gson();
                                String s = gson.toJson(deleteData);
                                DeleteData(s, count);
                            } else {
                                Toast.makeText(ShopCarActivity.this, "列表为空", Toast.LENGTH_SHORT).show();
                            }
                        }
//                        SparseBooleanArray parentStatus = myAdapter.getParentStatus();
//                        if (childData != null) {
//                            Iterator iter = childData.entrySet().iterator();
//                            List<ShopCarEntity> filterData = new ArrayList<ShopCarEntity>();
//                            int count = 0;
//                            while (iter.hasNext()) {
//                                ParentEntity fatherEntity = new ParentEntity();
//                                Map.Entry entry = (Map.Entry) iter.next();
//                                String key = (String) entry.getKey();
//                                List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
//                                for (int i = 0; i < child.size(); i++) {
//                                    if ("true".equals(child.get(i).getCHECK())) {
//                                        filterData.add(child.get(i));
//                                        count = count + child.get(i).getBUYCOUNT();
//                                    }
//
//                                }
//
//
//                            }
//                            List<DeleteEntity> deleteData = new ArrayList<DeleteEntity>();
//                            for (int i = 0; i < filterData.size(); i++) {
//                                DeleteEntity deleteEntity = new DeleteEntity();
//                                deleteEntity.setMERCHANDISEID(filterData.get(i).getGOODSGUID());
//                                deleteData.add(deleteEntity);
//                            }
//                            Gson gson = new Gson();
//                            String s = gson.toJson(deleteData);
//                            DeleteData(s, count);
//                        } else {
//                            Toast.makeText(ShopCarActivity.this, "列表为空", Toast.LENGTH_SHORT).show();
//                        }

                    }
                });
                ibuilder.setNegativeButton(R.string.cancel, null);
                ibuilder.create().show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //获取购物车的信息
    private void requestShopCarDataChanged() {

        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        if (UserConfig.isClientLogined()) {
            args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
        }
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
                    Log.e("GetBuyCarCount", errorMsg.msg);
                }
            }
        });
    }

    private List<ShopCarEntity> result;
    List<ParentEntity> fatherEntities;//药店名称
    HashMap<String, List<ShopCarEntity>> childData;


    public void onResponseShopCarData(List<LinkedTreeMap<String, String>> ShopCarData) {
        int count = ShopCarData == null ? 0 : ShopCarData.size();
        if (count <= 0) {
            List<ParentEntity> parentResult = new ArrayList<ParentEntity>();
            HashMap<String, List<ShopCarEntity>> childResult = new HashMap<String, List<ShopCarEntity>>();
            myAdapter.bindData(parentResult, childResult, new ShopAdapter.AdapterCallBack() {
                @Override
                public void UpdateData() {

                }

                @Override
                public void UpdateMoney(String money) {
                    tv_all1.setText("总计：" + money);
                }
            });
            all_choice.setChecked(false);
            return;
        }
        List<ShopCarEntity> result = new ArrayList<ShopCarEntity>();
        for (LinkedTreeMap<String, String> item : ShopCarData) {
            ShopCarEntity entity = ShopCarEntity.mapToEntity(item);
            entity.setCHECK("true");
            result.add(entity);
        }
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
                    expandableListView.expandGroup(i);
                }
            }
        });

    }

    private void bindData() {
        List<ShopCarEntity> result = DBInterface.instance().loadAllShopCar();

    }

    //向服务器提交购物车信息
    private void CommitData(String data, final HashMap<String, List<ShopCarEntity>> filterChildData, final ArrayList<ParentEntity> filterParentData) {
        if (UserConfig.isClientLogined()) {
            Map<String, String> args = new FacadeArgs.MapBuilder()
                    .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).put("JSONDATA", data).build();
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
                                finish();
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
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    //删除商品
    private void DeleteData(String deletedata, final int reduceCount) {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).put("JSONDATA", deletedata).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DelCartItem", args);
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
                    if (responseProtocol.getResponse() == null || "".equals(responseProtocol.getResponse())) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                            String success = jsonObject.getString("success");
                            if (success.equals("yes")) {
                                Log.i("是佛---", "是");
                                requestShopCarDataChanged();
                            } else {
                                Toast.makeText(ShopCarActivity.this, "出现异常，请您稍后再试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //暂时这样做
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, -reduceCount);
                        requestShopCarDataChanged();
                    }
                } else {
                    Log.i("ERROR", errorMsg.msg);
                }

            }
        });
    }

}

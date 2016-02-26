package com.romens.yjk.health.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.romens.android.log.FileLog;
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
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.GoodsEntity;
import com.romens.yjk.health.model.ParentEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.activity.CommitOrderActivity;
import com.romens.yjk.health.ui.adapter.ShopAdapter;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;
import com.romens.yjk.health.ui.utils.UIHelper;
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
 * Created by HZH on 2016/2/2.
 */
public class ShopCarFragment extends BaseFragment {
    private ExpandableListView expandableListView;
    private CheckableFrameLayout checkAll;
    private TextView sumMoneyView, accountsView;
    private ImageView btnBack, delete;
    private List<ShopCarEntity> datas;
    private ShopAdapter shopAdapter;
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shop_car_fragment, container, false);
        initView(view);
        //获取数据
        shopAdapter = new ShopAdapter(getActivity());
        expandableListView.setAdapter(shopAdapter);
        // needShowProgress("正在加载...");
        //getShopCarData();
        //向服务器提交购物车信息并跳转到订单页面
        accountsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送请求
                HashMap<String, List<ShopCarEntity>> childData = shopAdapter.getChildData();
                ArrayList<ParentEntity> parentData = (ArrayList<ParentEntity>) shopAdapter.getParentData();
                HashMap<String, SparseBooleanArray> childStatusList = shopAdapter.getChildStatusList();
                if (shopAdapter.isAllNotSelected()) {
                    Toast.makeText(getActivity(), "请选择一样商品", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<ParentEntity> mFilterParentData = new ArrayList<ParentEntity>();
                    if (childData != null && parentData != null) {
                        needShowProgress("正在提交");
                        ArrayList<ParentEntity> filterParentData = new ArrayList<ParentEntity>();
                        HashMap<String, List<ShopCarEntity>> filterChildData = new HashMap<String, List<ShopCarEntity>>();
                        ArrayList<GoodsEntity> jsonData = new ArrayList<GoodsEntity>();
                        SparseBooleanArray parentStatus = shopAdapter.getParentStatus();
                        if (parentData != null) {
                            for (int i = 0; i < parentData.size(); i++) {
                                if (parentStatus.get(i)) {
                                    filterParentData.add(parentData.get(i));
                                }
                            }
                            Iterator iter = childData.entrySet().iterator();
                            while (iter.hasNext()) {
                                ParentEntity fatherEntity = new ParentEntity();
                                Map.Entry entry = (Map.Entry) iter.next();
                                String key = (String) entry.getKey();
                                List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
                                SparseBooleanArray sparseBooleanArray = childStatusList.get(key);
                                List<ShopCarEntity> mChild = new ArrayList<ShopCarEntity>();
                                for (int i = 0; i < child.size(); i++) {
                                    if (sparseBooleanArray.get(i)) {
                                        mChild.add(child.get(i));
                                    }
                                    GoodsEntity goodsEntity = new GoodsEntity();
                                    goodsEntity.setBUYCOUNT(child.get(i).getBUYCOUNT() + "");
                                    goodsEntity.setGOODSGUID(child.get(i).getGOODSGUID() + "");
                                    jsonData.add(goodsEntity);
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
                            String data = gson.toJson(jsonData);
                            CommitData(data, filterChildData, filterParentData);
                        }
                    } else {
                        Toast.makeText(getActivity(), "购物车为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getShopCarData();
    }

    private void initView(View view) {
        checkAll = (CheckableFrameLayout) view.findViewById(R.id.all_choice);
        sumMoneyView = (TextView) view.findViewById(R.id.tv_all1);
        accountsView = (TextView) view.findViewById(R.id.accounts);
        expandableListView = (ExpandableListView) view.findViewById(R.id.ev);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        UIHelper.updateSwipeRefreshProgressBarTop(getActivity(), refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getShopCarData();
            }
        });
        refreshLayout.setRefreshing(true);
    }


//    public void showDialog() {
//        DialogUtils dialogUtils = new DialogUtils();
//        dialogUtils.show_infor_two("是否删除？", getActivity(), "提示", new DialogUtils.ConfirmListenerCallBack() {
//                    @Override
//                    public void ConfirmListener() {
//                        HashMap<String, List<ShopCarEntity>> childData = shopAdapter.getChildData();
//                        HashMap<String, SparseBooleanArray> childStatusList = shopAdapter.getChildStatusList();
//                        if (childData != null) {
//                            if (shopAdapter.isAllSelected()) {
//                                Iterator iter = childData.entrySet().iterator();
//                                List<ShopCarEntity> filterData = new ArrayList<ShopCarEntity>();
//                                int count = 0;
//                                while (iter.hasNext()) {
//                                    Map.Entry entry = (Map.Entry) iter.next();
//                                    String key = (String) entry.getKey();
//                                    List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
//                                    SparseBooleanArray sparseBooleanArray = childStatusList.get(key);
//                                    for (int i = 0; i < child.size(); i++) {
//                                        filterData.add(child.get(i));
//                                        count = count + child.get(i).getBUYCOUNT();
//                                    }
//                                }
//                                List<DeleteEntity> deleteData = new ArrayList<DeleteEntity>();
//                                for (int i = 0; i < filterData.size(); i++) {
//                                    DeleteEntity deleteEntity = new DeleteEntity();
//                                    deleteEntity.setMERCHANDISEID(filterData.get(i).getGOODSGUID());
//                                    deleteData.add(deleteEntity);
//                                }
//                                Gson gson = new Gson();
//                                String s = gson.toJson(deleteData);
//                                DeleteData(s, count);
//                            } else if (shopAdapter.isAllNotSelected()) {
//                                Toast.makeText(getActivity(), "请至少选择一件商品", Toast.LENGTH_SHORT).show();
//                            } else {
//                                if (childData != null) {
//                                    Iterator iter = childData.entrySet().iterator();
//                                    List<ShopCarEntity> filterData = new ArrayList<ShopCarEntity>();
//                                    int count = 0;
//                                    while (iter.hasNext()) {
//                                        Map.Entry entry = (Map.Entry) iter.next();
//                                        String key = (String) entry.getKey();
//                                        List<ShopCarEntity> child = (List<ShopCarEntity>) entry.getValue();
//                                        SparseBooleanArray sparseBooleanArray = childStatusList.get(key);
//                                        for (int i = 0; i < sparseBooleanArray.size(); i++) {
//                                            if (sparseBooleanArray.get(i)) {
//                                                filterData.add(child.get(i));
//                                                count = count + child.get(i).getBUYCOUNT();
//                                            }
//                                        }
//                                    }
//                                    List<DeleteEntity> deleteData = new ArrayList<DeleteEntity>();
//                                    for (int i = 0; i < filterData.size(); i++) {
//                                        DeleteEntity deleteEntity = new DeleteEntity();
//                                        deleteEntity.setMERCHANDISEID(filterData.get(i).getGOODSGUID());
//                                        deleteData.add(deleteEntity);
//                                    }
//                                    Gson gson = new Gson();
//                                    String s = gson.toJson(deleteData);
//                                    DeleteData(s, count);
//                                } else {
//                                    Toast.makeText(getActivity(), "列表为空", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } else {
//                            return;
//                        }
//                    }
//
//                }, new DialogUtils.CancelListenerCallBack() {
//                    @Override
//                    public void CancelListener() {
//                    }
//                }
//        );
//    }
//

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
                                getShopCarData();
                            } else {
                                Toast.makeText(getActivity(), "出现异常，请您稍后再试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //暂时这样做
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onShoppingCartChanged, -reduceCount);
                        getShopCarData();
                    }
                } else {
                    Log.i("ERROR", errorMsg.msg);
                }

            }
        });
    }

    //获取购物车的信息
    private void getShopCarData() {
        refreshLayout.setRefreshing(true);
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        if (UserConfig.isClientLogined()) {
            args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
        }
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserBuyCarList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {

            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Log.e("GetBuyCarCount", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                refreshLayout.setRefreshing(false);
                needHideProgress();
                try {
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        JSONArray jsonArray = new JSONArray(responseProtocol.getResponse());
                        Log.i("是否执行了-----", responseProtocol.getResponse());
                        resultToJson(jsonArray);
                    } else {
                        resultToJson(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private List<ShopCarEntity> result;
    List<ParentEntity> fatherEntities;//药店名称
    HashMap<String, List<ShopCarEntity>> childData;

    public void resultToJson(JSONArray jsonArray) throws JSONException {
        int count = jsonArray == null ? 0 : jsonArray.length();
        if (count <= 0) {
            List<ParentEntity> parentResult = new ArrayList<ParentEntity>();
            HashMap<String, List<ShopCarEntity>> childResult = new HashMap<String, List<ShopCarEntity>>();
            shopAdapter.bindData(parentResult, childResult);
            shopAdapter.notifyDataSetChanged();
            shopAdapter.setCallBack(new ShopAdapter.AdapterCallBack() {
                @Override
                public void UpdateData() {
                }

                @Override
                public void UpdateMoney(String money) {
                    sumMoneyView.setText("总计：" + UIUtils.getDouvleValue(money));
                }
            });
            checkAll.setChecked(false);
            return;
        }
        List<ShopCarEntity> result = new ArrayList<ShopCarEntity>();
        for (int i = 0; i < count; i++) {
            ShopCarEntity entity = ShopCarEntity.jsonObjectToEntity(jsonArray.getJSONObject(i));
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

        shopAdapter.bindData(fatherEntities, childData);
        shopAdapter.setCallBack(new ShopAdapter.AdapterCallBack() {
            @Override
            public void UpdateData() {
                checkAll.setChecked(shopAdapter.isAllSelected());
            }

            @Override
            public void UpdateMoney(String money) {
                sumMoneyView.setText("总计：" + UIUtils.getDouvleValue(money));
            }
        });

        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopAdapter.switchAllSelect(!checkAll.isChecked());

            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < fatherEntities.size(); i++) {
                    expandableListView.expandGroup(i);
                }
            }
        });


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
                                Intent i = new Intent(getActivity(), CommitOrderActivity.class);
                                i.putExtra("childData", filterChildData);
                                i.putExtra("parentData", filterParentData);
                                startActivity(i);
                                //getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), "出现异常，请您稍后再试", Toast.LENGTH_SHORT).show();
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
            UIOpenHelper.openLoginActivity(getActivity());
            getActivity().finish();
        }
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }

    private ProgressDialog progressDialog;

    public void needShowProgress(String progressText) {
        if (progressDialog != null) {
            return;
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(progressText);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void needHideProgress() {
        if (progressDialog == null) {
            return;
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(getActivity().getPackageName(), e);
        }
        progressDialog = null;
    }


}

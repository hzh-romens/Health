package com.romens.yjk.health.core;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.CollectDataDao;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.model.CollectDataEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/11/26.
 */
public class CollectService extends IntentService {

    public CollectService() {
        super("CollectService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String type = intent.getStringExtra("serviceType");
        UserEntity clientUserEntity = UserConfig.getClientUserEntity();
        if (type.equals("add")) {
            if (clientUserEntity != null) {
                requestCollectData(clientUserEntity.getGuid());
            }
        } else if (type.equals("del")) {
            String serviceMerchandiseId = intent.getStringExtra("serviceMerchandiseId");
            if (serviceMerchandiseId != null && !serviceMerchandiseId.equals("")) {
                CollectDataDao dao = DBInterface.instance().openReadableDb().getCollectDataDao();
                List<CollectDataEntity> entities = dao.queryRaw("where MerchandiseId = ", serviceMerchandiseId);
                requestDelFavour(clientUserEntity.getGuid(), getJSONArray(serviceMerchandiseId), entities);
            } else {
                ArrayList<CollectDataEntity> entities = intent.getParcelableArrayListExtra("serviceListEntity");
                requestDelFavour(clientUserEntity.getGuid(), getJSONArray(entities), entities);
            }
        }
    }

    private String getJSONArray(ArrayList<CollectDataEntity> entities) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < entities.size(); i++) {
            try {
                JSONObject object = new JSONObject();
                object.put("MERCHANDISEID", entities.get(i).getMerchandiseId());
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array.toString();
    }

    public String getJSONArray(String serviceMerchandiseId) {
        JSONArray array = new JSONArray();
        try {
            JSONObject object = new JSONObject();
            object.put("MERCHANDISEID", serviceMerchandiseId);
            array.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array.toString();
    }

    //请求收藏
    private void requestCollectData(String userGuid) {
        int lastTime = DBInterface.instance().getCollectDataLastTime();
        Map<String, Object> args = new HashMap<>();
        args.put("USERGUID", userGuid);
        args.put("LASTTIME", lastTime);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "MyFavouriteBy", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
                .build();
        FacadeClient.request(MyApplication.applicationContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(MyApplication.applicationContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    setQueryData(responseProtocol.getResponse());
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR" + errorMsg.msg);
                }
            }
        });
    }

    private void setQueryData(String response) {
        if (response == null || response.length() < 0) {
            return;
        }
        CollectDataDao dao = DBInterface.instance().openWritableDb().getCollectDataDao();
        List<CollectDataEntity> tempEntityList = dao.loadAll();
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                CollectDataEntity entity = new CollectDataEntity();
                entity.setMerchandiseId(item.getString("MERCHANDISEID"));
                entity.setMedicineName(item.getString("MEDICINENAME"));
                entity.setMedicineSpec(item.getString("MEDICINESPEC"));
                entity.setShopId(item.getString("SHOPID"));
                entity.setShopName(item.getString("SHOPNAME"));
                entity.setPicBig(item.getString("PICBIG"));
                entity.setPicSmall(item.getString("PICSMALL"));
                entity.setPrice(item.getString("PRICE"));
                entity.setMemberPrice(item.getString("MEMBERPRICE"));
                entity.setAssessCount(item.getString("ASSESSCOUNT"));
                entity.setSaleCount(item.getString("SALECOUNT"));
                entity.setUpdated(item.getInt("CREATEDATE"));
                boolean flag = true;
                for (CollectDataEntity itemEntity : tempEntityList) {
                    if (itemEntity.getMerchandiseId().equals(entity.getMerchandiseId())) {
                        flag = false;
                    }
                }
                if (flag) {
                    dao.insert(entity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //从本地删除收藏
    private void deleteDb(List<CollectDataEntity> entityList) {
        CollectDataDao dataDao = DBInterface.instance().openWritableDb().getCollectDataDao();
        for (CollectDataEntity entity : entityList) {
            dataDao.delete(entity);
        }
    }

    //访问删除收藏
    private void requestDelFavour(final String userGuid, String jsonData, final List<CollectDataEntity> entities) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("JSONDATA", jsonData);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DelFavouriate", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(MyApplication.applicationContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(MyApplication.applicationContext, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    Log.e("tag", "--collect--->" + responseProtocol.getResponse());
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                        Log.e("tag", "--requestCode--->" + requestCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        Toast.makeText(MyApplication.applicationContext, "删除成功", Toast.LENGTH_SHORT).show();
                        deleteDb(entities);
                    } else {
                        Toast.makeText(MyApplication.applicationContext, "删除错误", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                    Log.e("tag", "--collect--ERROR-->" + errorMsg.msg);
                }
//                needHideProgress();
            }
        });
    }
}

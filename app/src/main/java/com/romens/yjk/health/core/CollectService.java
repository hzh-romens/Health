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

    private CollectDelCallback callback;

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
}

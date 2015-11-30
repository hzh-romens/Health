package com.romens.yjk.health.core;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.romens.yjk.health.config.UserGuidConfig;
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

import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by anlc on 2015/11/26.
 */
public class CollectHelper {

    private CollectDelCallback callback;

    private CollectHelper() {
    }

    public static CollectHelper getInstance() {
        return new CollectHelper();
    }

    public void addCollect(Context context) {
        Intent intent = new Intent(context,CollectService.class);
        intent.putExtra("serviceType", "add");
        context.startService(intent);
    }

    public void delCollect(Context context, ArrayList<CollectDataEntity> entities) {
        this.callback = (CollectDelCallback) context;
        UserEntity clientUserEntity = UserConfig.getClientUserEntity();
        requestDelFavour(clientUserEntity.getGuid(), getJSONArray(entities), entities);
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

    //从本地删除收藏
    private void deleteDb(List<CollectDataEntity> entityList) {
        CollectDataDao dataDao = DBInterface.instance().openWritableDb().getCollectDataDao();
        for (CollectDataEntity entity : entityList) {
            dataDao.delete(entity);
        }
        callback.delSuccess();
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
                        callback.delError();
                    }
                }
                if (errorMsg == null) {
                } else {
                    callback.delError();
                    Log.e("reqGetAllUsers", "ERROR");
                    Log.e("tag", "--collect--ERROR-->" + errorMsg.msg);
                }
//                needHideProgress();
            }
        });
    }
}

package com.romens.yjk.health.core;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.CollectDataDao;
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
public class CollectHelper {

    private CollectHelper() {
    }

    public static CollectHelper getInstance() {
        return new CollectHelper();
    }

    public void addCollect(Context context) {
        Intent intent = new Intent("com.romens.yjk.health.core.myService");
        intent.putExtra("serviceType", "add");
        context.startService(intent);
    }

    public void delCollectListEntity(Context context, ArrayList<CollectDataEntity> entities) {
        Intent intent = new Intent("com.romens.yjk.health.core.myService");
        intent.putExtra("serviceType", "del");
        intent.putParcelableArrayListExtra("serviceListEntity", entities);
        context.startService(intent);
    }

    public void delCollect(Context context, String merchandiseId) {
        Intent intent = new Intent("com.romens.yjk.health.core.myService");
        intent.putExtra("serviceType", "del");
        intent.putExtra("serviceMerchandiseId", merchandiseId);
        context.startService(intent);
    }

    private String delItem(List<CollectDataEntity> entities) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).isSelect()) {
//                entities.remove(i);
//                i--;
                try {
                    JSONObject object = new JSONObject();
                    object.put("MERCHANDISEID", entities.get(i).getMerchandiseId());
                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("tag", "--delCollect-->" + array.toString());
        return array.toString();
    }
}

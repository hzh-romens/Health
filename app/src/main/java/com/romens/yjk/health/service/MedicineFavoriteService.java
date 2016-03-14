package com.romens.yjk.health.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FavoritesDao;
import com.romens.yjk.health.db.entity.FavoritesEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineFavoriteService extends IntentService {
    public static final int ACTION_SYNC=-1;
    public static final int ACTION_ADD=0;
    public static final int ACTION_REMOVE=1;
    public static final int ACTION_REMOVE_LIST=2;

    public static final String ARGUMENTS_KEY_ACTION = "favorites_action";
    public static final String ARGUMENTS_KEY_MEDICINE_ID = "MEDICINE_ID";
    public static final String ARGUMENTS_KEY_MEDICINE_ID_LIST = "MEDICINE_ID_LIST";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MedicineFavoriteService() {
        super("MedicineFavorite");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int action=intent.getIntExtra(ARGUMENTS_KEY_ACTION, -1);
            if(action==ACTION_ADD){
                String medicineID = intent.getStringExtra(ARGUMENTS_KEY_MEDICINE_ID);
                addMedicineFavorite(medicineID);
            }else if(action==ACTION_REMOVE){
                String medicineID = intent.getStringExtra(ARGUMENTS_KEY_MEDICINE_ID);
                List<String> medicines=new ArrayList<>();
                medicines.add(medicineID);
                removeMedicineFavorite(medicines);
            }else if(action==ACTION_REMOVE_LIST){
                ArrayList<String> medicineIDList=intent.getStringArrayListExtra(ARGUMENTS_KEY_MEDICINE_ID_LIST);
                removeMedicineFavorite(medicineIDList);
            }else if(action==ACTION_SYNC){
                syncFavoritesData(getApplicationContext());
            }
        }
    }

    private void addMedicineFavorite(final String medicineID) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("MERCHANDISEID", medicineID);
        args.put("USERGUID", UserConfig.getInstance().getClientUserEntity().getGuid());
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "AddMyFavour", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onAddMedicineFavorite, medicineID, 0);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                boolean hasError = false;
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String returnMsg = jsonObject.getString("success");
                        if ("yes".equals(returnMsg)) {
                            syncFavoritesData(MyApplication.applicationContext);
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onAddMedicineFavorite, medicineID, 1);
                        } else {
                            hasError = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    hasError = true;
                }
                if (hasError) {
                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onAddMedicineFavorite, medicineID, 0);
                }
            }
        });
    }

    //取消收藏
    private void removeMedicineFavorite(final List<String> medicineID) {
        JSONArray array = new JSONArray();
        try {
            JSONObject object;
            for (String id : medicineID) {
                object= new JSONObject();
                object.put("MERCHANDISEID", id);
                array.put(object);
            }

            Map<String, String> args = new FacadeArgs.MapBuilder().build();
            args.put("USERGUID", UserConfig.getInstance().getClientUserEntity().getGuid());
            args.put("JSONDATA", array.toString());

            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DelFavouriate", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .build();
            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {
                    if(medicineID.size()==1){
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite, medicineID.get(0), 0);
                    }else{
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite);
                    }
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    boolean hasError = false;
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        String response = responseProtocol.getResponse();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String returnMsg = jsonObject.getString("success");
                            if ("yes".equals(returnMsg)) {
                                DBInterface.instance().removeFavoriteFromDB(medicineID);

                                if(medicineID.size()==1){
                                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite, medicineID.get(0), 1);
                                }else{
                                    AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite);
                                }

                            } else {
                                hasError = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        hasError = true;
                    }
                    if (hasError) {
                        if(medicineID.size()==1){
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite, medicineID.get(0), 0);
                        }else{
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite);
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //请求收藏
    private void syncFavoritesData(Context context) {
        long lastTime = DBInterface.instance().getFavoritesDataLastTime();
        Map<String, Object> args = new HashMap<>();
        args.put("USERGUID", UserConfig.getInstance().getClientUserId());
        args.put("LASTTIME", lastTime);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "MyFavouriteBy", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(context, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {

            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg==null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    handleFavoritesData(responseProtocol.getResponse());
                }
            }
        });
    }

    private void handleFavoritesData(String response) {
        if (TextUtils.isEmpty(response)) {
            return;
        }
        List<FavoritesEntity> favoritesEntities=new ArrayList<>();
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                favoritesEntities.add(FavoritesEntity.jsonObjectToEntity(jsonObject));
//                CollectDataEntity entity = new CollectDataEntity();
//                entity.setMerchandiseId(item.getString("MERCHANDISEID"));
//                entity.setMedicineName(item.getString("MEDICINENAME"));
//                entity.setMedicineSpec(item.getString("MEDICINESPEC"));
//                entity.setShopId(item.getString("SHOPID"));
//                entity.setShopName(item.getString("SHOPNAME"));
//                entity.setPicBig(item.getString("PICBIG"));
//                entity.setPicSmall(item.getString("PICSMALL"));
//                entity.setPrice(item.getString("PRICE"));
//                entity.setMemberPrice(item.getString("MEMBERPRICE"));
//                entity.setAssessCount(item.getString("ASSESSCOUNT"));
//                entity.setSaleCount(item.getString("SALECOUNT"));
//                entity.setUpdated(item.getInt("CREATEDATE"));
//                boolean flag = true;
//                for (CollectDataEntity itemEntity : tempEntityList) {
//                    if (itemEntity.getMerchandiseId().equals(entity.getMerchandiseId())) {
//                        flag = false;
//                    }
//                }
//                if (flag) {
//                    dao.insert(entity);
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(favoritesEntities.size()>0){
            FavoritesDao favoritesDao = DBInterface.instance().openWritableDb().getFavoritesDao();
            favoritesDao.insertOrReplaceInTx(favoritesEntities);
            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onMedicineFavoriteChanged);
        }
    }
}

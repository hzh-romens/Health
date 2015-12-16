package com.romens.yjk.health.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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
import com.romens.yjk.health.db.DBInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineFavoriteService extends IntentService {
    public static final String ARGUMENTS_KEY_MEDICINE_ID = "MEDICINE_ID";
    public static final String ARGUMENTS_KEY_MEDICINE_TARGET_FAVORITE = "MEDICINE_TARGET_FAVORITE";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MedicineFavoriteService() {
        super("MedicineFavorite");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String medicineID = intent.getStringExtra(ARGUMENTS_KEY_MEDICINE_ID);
            boolean targetFavoriteValue = intent.getBooleanExtra(ARGUMENTS_KEY_MEDICINE_TARGET_FAVORITE, true);
            if (targetFavoriteValue) {
                addMedicineFavorite(medicineID);
            } else {
                removeMedicineFavorite(medicineID);
            }
        }
    }

    private void addMedicineFavorite(final String medicineID) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("MERCHANDISEID", medicineID);
        args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "AddMyFavour", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("addtofarvite", "ERROR");
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
    private void removeMedicineFavorite(final String medicineID) {
        JSONArray array = new JSONArray();
        try {
            JSONObject object = new JSONObject();
            object.put("MERCHANDISEID", medicineID);
            array.put(object);
            Map<String, String> args = new FacadeArgs.MapBuilder().build();
            args.put("MERCHANDISEID", medicineID);
            args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
            args.put("JSONDATA", array.toString());

            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "DelFavouriate", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .build();
            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {
                    Log.e("addtofarvite", "ERROR");
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
                                AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite, medicineID, 1);
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
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onRemoveMedicineFavorite, medicineID, 0);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

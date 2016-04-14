package com.romens.yjk.health.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;

import com.romens.android.common.UniqueCode;
import com.romens.android.log.FileLog;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/4/14
 * @description
 */
public class UploadPushTokenService extends IntentService {

    public UploadPushTokenService() {
        super("UploadPushTokenService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadPushTokenService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String pushToken = intent.getStringExtra("PUSHTOKEN");

            Map<String, Object> args = new HashMap<>();
            args.put("DEVICEID", UniqueCode.create(getApplicationContext()));
            args.put("DEVICEOS", "Android");
            if (UserConfig.isClientLogined()) {
                args.put("PUSHUSER", UserConfig.getInstance().getClientUserId());
            }
            StringBuilder deviceInfo = new StringBuilder();
            deviceInfo.append("Android " + Build.VERSION.SDK_INT);
            deviceInfo.append(";");
            deviceInfo.append(Build.MANUFACTURER);
            deviceInfo.append(";");
            deviceInfo.append(Build.MODEL);
            args.put("DEVICEINFO", deviceInfo.toString());

            args.put("PUSHTOKEN", pushToken);

            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "UploadAppDeviceInfo", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());

            Connect connect = new RMConnect.Builder(UploadPushTokenService.class)
                    .withProtocol(protocol)
                    .withParser(new JSONNodeParser())
                    .withDelegate(new Connect.AckDelegate() {
                        @Override
                        public void onResult(Message message, Message errorMessage) {
                            if (errorMessage != null) {
                                FileLog.e("Upload Push Token ERROR,cause:" + errorMessage.msg);
                            }
                        }
                    }).build();
            ConnectManager.getInstance().request(this, connect);
        }
    }
}

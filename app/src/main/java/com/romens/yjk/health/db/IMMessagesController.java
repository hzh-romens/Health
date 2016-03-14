package com.romens.yjk.health.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.easemob.applib.controller.HXSDKHelper;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.core.ImageLoader;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.im.IMHXSDKHelper;

/**
 * Created by siery on 15/6/18.
 */
public class IMMessagesController {
    public int fontSize = AndroidUtilities.dp(16);

    private static volatile IMMessagesController Instance = null;

    public static IMMessagesController getInstance() {
        IMMessagesController localInstance = Instance;
        if (localInstance == null) {
            synchronized (IMMessagesController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new IMMessagesController();
                }
            }
        }
        return localInstance;
    }

    public IMMessagesController() {

        ImageLoader.getInstance();

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
        fontSize = preferences.getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);

        IMMessagesStorage.getInstance();
    }


    public static final int UPDATE_MASK_USER_LIST = 1;

    /**
     * 加载本地DB的状态
     * 不管是离线还是在线登陆，loadFromDb 要运行的
     */
    public void onLoadLocalData(Context context) {
        cleanUp();
        String appKey = "romens";
        String userName = UserConfig.getInstance().getClientUserId();
        IMMessagesStorage.getInstance().setupDb(context, appKey, userName);
    }

    public void cleanUp() {

        IMMessagesStorage.getInstance().close();
    }

    public UserEntity getUser(String id) {
        return null;
    }

}

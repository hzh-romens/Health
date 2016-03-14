package com.romens.yjk.health.core;

import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.db.entity.UserEntity;

/**
 * @author Zhou Lisi
 * @create 16/2/23
 * @description
 */
public class UserSession {
    private static volatile UserSession Instance = null;

    public static UserSession getInstance() {
        UserSession localInstance = Instance;
        if (localInstance == null) {
            synchronized (UserSession.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new UserSession();
                }
            }
        }
        return localInstance;
    }

    private UserEntity clientUser;

    private UserSession() {
        onChanged();
    }

    public UserEntity get() {
        return clientUser;
    }

    public void onChanged() {
        clientUser = UserConfig.getInstance().getClientUserEntity();
    }

    public boolean isClientLogin(){
        return UserConfig.isClientLogined();
    }

    public void needLoginOut() {
        clientUser = null;
        UserConfig.clearUser();
        UserConfig.clearConfig();
        FacadeToken.getInstance().expired();
        UserConfig.getInstance().deleteAllAppAccounts();
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.loginOut);
    }
}

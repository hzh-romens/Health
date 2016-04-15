package com.romens.yjk.health.config;

import android.text.TextUtils;

/**
 * Created by zhoulisi on 14-7-14.
 */
public class FacadeToken {
    private String authToken;
    private final static Object sync = new Object();

    private static volatile FacadeToken Instance = null;

    public static FacadeToken getInstance() {
        FacadeToken localInstance = Instance;
        if (localInstance == null) {
            synchronized (FacadeToken.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FacadeToken();
                    Instance.init();
                }
            }
        }
        return localInstance;
    }

    FacadeToken() {
        init();
    }

    public void init() {
        synchronized (sync) {
            authToken = UserConfig.getInstance().createToken();
        }
    }

    public void expired() {
        synchronized (sync) {
            authToken = null;
            Instance = null;
        }
    }

    public String getAuthToken() {
        synchronized (sync) {
            if (TextUtils.isEmpty(authToken)) {
                authToken = UserConfig.getInstance().createToken();
            }
            return authToken;
        }
    }

}
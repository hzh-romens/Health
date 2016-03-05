package com.romens.yjk.health.wx;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by siery on 16/2/4.
 */
public class WXManager {
    private String appId;
    private IWXAPI iwxapi;

    private static volatile WXManager Instance = null;

    public static WXManager getInstance(Context context) {
        WXManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (WXManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new WXManager(context);
                }
            }
        }
        return localInstance;
    }

    private WXManager(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo != null) {
            appId = appInfo.metaData.getString("wx_app_id");
        }
        if (!TextUtils.isEmpty(appId)) {
            regToWX(context, appId);
        }
    }

    public void setAppId(Context context, String id) {
        this.appId = id;
        regToWX(context, id);
    }

    protected void regToWX(Context context, String id) {
        iwxapi = WXAPIFactory.createWXAPI(context, id, true);
        iwxapi.registerApp(id);
    }

    public IWXAPI getWXAPI() {
        return iwxapi;
    }

    /**
     * 分享到朋友
     *
     * @param url   链接
     * @param title 标题
     * @param desc  描述
     * @param thumb 封面
     * @return 是否分享成功
     */
    public boolean shareURLToUser(String url, String title, String desc, byte[] thumb) {
        return shareURL(url, title, desc, thumb, SendMessageToWX.Req.WXSceneSession);
    }

    /**
     * 分享到朋友圈
     *
     * @param url   链接
     * @param title 标题
     * @param desc  描述
     * @param thumb 封面
     * @return 是否分享成功
     */
    public boolean shareURLToTimeline(String url, String title, String desc, byte[] thumb) {
        return shareURL(url, title, desc, thumb, SendMessageToWX.Req.WXSceneTimeline);
    }

    /**
     * 分享到收藏
     *
     * @param url   链接
     * @param title 标题
     * @param desc  描述
     * @param thumb 封面
     * @return 是否分享成功
     */
    public boolean shareURLToFavorite(String url, String title, String desc, byte[] thumb) {
        return shareURL(url, title, desc, thumb, SendMessageToWX.Req.WXSceneFavorite);
    }

    private boolean shareURL(String url, String title, String desc, byte[] thumb, int scene) {
        WXWebpageObject webPageObject = new WXWebpageObject();
        webPageObject.webpageUrl = url;

        WXMediaMessage message = new WXMediaMessage(webPageObject);
        message.title = title;
        message.description = desc;
        if (thumb != null) {
            message.thumbData = thumb;
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = WXHelper.buildTransaction("webpage");
        req.message = message;
        req.scene = scene;
        boolean isSend = iwxapi.sendReq(req);
        return isSend;
    }

}

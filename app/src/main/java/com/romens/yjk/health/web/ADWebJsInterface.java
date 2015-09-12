package com.romens.yjk.health.web;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

/**
 * Created by siery on 15/9/9.
 */
public class ADWebJsInterface  extends JsBaseInterface {
    private Handler handler = new Handler();

    public ADWebJsInterface(Context context) {
        super(context);
    }

    @JavascriptInterface
    public String query() {
//        JSONStringer jsonStringer = new JSONStringer();
//        try {
//            return jsonStringer.object().key("title").value(noticeEntity.getTitle())
//                    .key("content").value(noticeEntity.getValue())
//                    .key("auth").value(String.format("发布人:%s", noticeEntity.getAuthor()))
//                    .key("sendDate").value(noticeEntity.getCreateDate())
//                    .endObject().toString();
//        } catch (JSONException e) {
//            return "";
//        }
        return "";
    }

    @Override
    public void onJavascript() {
        handler.post(new Runnable() {
            public void run() {
                if (webView != null) {
                    String jsName = onCreateJsName();
                    //webView.loadUrl(String.format("javascript:insertJson(%s.query())", jsName));
                }
            }
        });
    }

    @Override
    protected String onCreateJsName() {
        return "adWebJsInterface";
    }
}

package com.romens.yjk.health.web;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by zhoulisi on 15/4/11.
 */
public abstract class JsBaseInterface {
    protected Context context;
    protected WebView webView;

    public JsBaseInterface(Context context) {
        this.context = context;
    }

    public JsBaseInterface withWebView(WebView webView) {
        this.webView = webView;
        return this;
    }
    public abstract void onJavascript();

    @JavascriptInterface
    public String toString() {
        return onCreateJsName();
    }

    @JavascriptInterface
    protected abstract String onCreateJsName();
}

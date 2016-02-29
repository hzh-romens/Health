package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;

/**
 * Created by siery on 15/9/9.
 */
public class ADWebActivity extends WebActivity implements AppNotificationCenter.NotificationCenterDelegate {
    public static final String ARGUMENTS_KEY_TITLE = "title";
    public static final String ARGUMENTS_KEY_TARGET_URL = "target_url";
    public static final String ARGUMENTS_KEY_HTML = "html";

    private JsBaseInterface adWebJsInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        ActionBar actionBar = getMyActionBar();
        if (bundle.containsKey(ARGUMENTS_KEY_TITLE)) {
            String title = bundle.getString(ARGUMENTS_KEY_TITLE);
            setActionBarTitle(actionBar, title);
        } else {
            setActionBarTitle(actionBar, "");
        }

        WebView webView = getWebView();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        adWebJsInterface = new ADWebJsInterface(this)
                .withWebView(webView);
        webView.addJavascriptInterface(adWebJsInterface, adWebJsInterface.toString());

        if (bundle.containsKey(ARGUMENTS_KEY_TARGET_URL)) {
            String url = bundle.getString(ARGUMENTS_KEY_TARGET_URL);
            webView.loadUrl(url);
        } else {
            String html = bundle.getString(ARGUMENTS_KEY_HTML, "");
            webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        }

    }

    @Override
    protected void onWebPageCompleted() {

    }

    @Override
    public void onBackPressed() {
        if (getWebView().canGoBack()) {
            getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.loginSuccess) {
            getWebView().reload();
        }
    }
}

package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;

/**
 * Created by siery on 15/9/9.
 */
public class ADWebActivity extends WebActivity {
    public static final String ARGUMENTS_KEY_TITLE = "title";
    public static final String ARGUMENTS_KEY_TARGET_URL = "target_url";

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

        if (bundle.containsKey(ARGUMENTS_KEY_TARGET_URL)) {
            String url = bundle.getString(ARGUMENTS_KEY_TARGET_URL);
            adWebJsInterface = new ADWebJsInterface(this)
                    .withWebView(webView);
            webView.addJavascriptInterface(adWebJsInterface, adWebJsInterface.toString());
            webView.loadUrl(url);
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
}

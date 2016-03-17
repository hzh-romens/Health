package com.romens.yjk.health.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.romens.android.io.json.JacksonMapper;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.ui.im.HealthConsultActivity;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;

import java.util.HashMap;

/**
 * @author Zhou Lisi
 * @create 2016-03-17 13:29
 * @description
 */
public class AboutActivity extends WebActivity {
    private JsBaseInterface adWebJsInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getMyActionBar();
        actionBar.setBackgroundColor(ResourcesConfig.primaryColor);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setTitle("关于", 0xffffffff);
        ProgressBarDeterminate progress = getWebProgressBar();
        progress.setBackgroundColor(ResourcesConfig.accentColor);

        WebView webView = getWebView();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        adWebJsInterface = new ADWebJsInterface(this)
                .withWebView(webView);
        webView.addJavascriptInterface(adWebJsInterface, adWebJsInterface.toString());

        String url = String.format("%sAbout?user=%s", FacadeConfig.getUrl(), FacadeToken.getInstance().getAuthToken());
        webView.loadUrl(url);
    }


    @Override
    protected void onWebPageCompleted() {
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

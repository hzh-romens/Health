package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.ui.activity.WebActivity;

/**
 * Created by anlc on 2016/2/25.
 */
public class HelpNewActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getMyActionBar();
        setActionBarColor(actionBar, ResourcesConfig.primaryColor);
        actionBar.setTitle("帮助", 0xffffffff);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
        WebView webView = getWebView();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(String.format("http://115.28.244.190/help/help.html?org=%s", UserConfig.getOrgCode()));
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

package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.ui.base.WebActivity;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;

/**
 * @author Zhou Lisi
 * @create 2016-03-17 13:29
 * @description
 */
public class AboutActivity extends WebActivity {
    private JsBaseInterface adWebJsInterface;
    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = onCreateActionBarTitle();
        ActionBar actionBar = getMyActionBar();
        actionBar.setBackgroundColor(ResourcesConfig.primaryColor);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setTitle(title, 0xffffffff);
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

        url=onCreateURL();
        webView.loadUrl(url);
    }

    protected String onCreateURL(){
        return String.format("%sAbout?user=%s", FacadeConfig.getUrl(), FacadeToken.getInstance().getAuthToken());
    }

    protected String onCreateActionBarTitle(){
        return "关于我们";
    }


    @Override
    protected void onWebPageCompleted() {
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected String getActivityName() {
        return "关于";
    }
}

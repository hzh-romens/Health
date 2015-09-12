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

    private JsBaseInterface mADWebJsInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        ActionBar actionBar = getMyActionBar();
        if (bundle.containsKey(ARGUMENTS_KEY_TITLE)) {
            String title = bundle.getString(ARGUMENTS_KEY_TITLE);
            actionBar.setTitle(title, 0xff0f9d58);
        } else {
            actionBar.setTitle("");
        }

        WebView webView = getWebView();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                ProgressBarDeterminate progressBar = getWebProgressBar();
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (mADWebJsInterface != null) {
                        mADWebJsInterface.onJavascript();
                    }
                } else {
                    if (progressBar.getVisibility() == View.INVISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        if (bundle.containsKey(ARGUMENTS_KEY_TARGET_URL)) {
            String url = bundle.getString(ARGUMENTS_KEY_TARGET_URL);
            mADWebJsInterface = new ADWebJsInterface(this)
                    .withWebView(webView);
            webView.addJavascriptInterface(mADWebJsInterface, mADWebJsInterface.toString());
            webView.loadUrl(url);
        }

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

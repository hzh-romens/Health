package com.romens.yjk.health.ui.im;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.ui.activity.WebActivity;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;

/**
 * Created by siery on 16/1/28.
 */
public class IMActivity extends WebActivity {
    private JsBaseInterface adWebJsInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        ActionBar actionBar = getMyActionBar();
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        setActionBarColor(actionBar, 0xff42b8f4);
        needActionBarDivider(actionBar, false);

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

        String url = buildIMURL();
        webView.loadUrl(url);
    }

    private String buildIMURL() {
        String token = FacadeToken.getInstance().getAuthToken();
        String url = String.format("http://139.129.4.154/yyzs/kefu/h5.html?tenantId=247&appkey=liyoujing#huanxinkefu&to=123123&token=%s", token == null ? "" : token);

        return url;
    }

    @Override
    protected void onWebPageCompleted() {

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

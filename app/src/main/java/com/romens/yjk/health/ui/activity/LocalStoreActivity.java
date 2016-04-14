package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.ui.base.WebActivity;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;

/**
 * @author Zhou Lisi
 * @create 2016-04-14 09:50
 * @description
 */
public class LocalStoreActivity extends WebActivity {
    public static final String ARGUMENTS_KEY_LOCAL_STORE_ID = "KEY_LOCAL_STORE_ID";
    public static final String ARGUMENTS_KEY_LOCAL_STORE_NAME = "KEY_LOCAL_STORE_NAME";
    private JsBaseInterface adWebJsInterface;
    private String localStoreId;
    private String localStoreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getIntent().getExtras();
        localStoreId = arguments.getString(ARGUMENTS_KEY_LOCAL_STORE_ID);
        localStoreName = arguments.getString(ARGUMENTS_KEY_LOCAL_STORE_NAME);

        ActionBar actionBar = getMyActionBar();
        actionBar.setBackgroundColor(0xfff0f0f0);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_grey600_24dp);
        needActionBarDivider(actionBar, true);
        setActionBarTitle(actionBar, localStoreName);

        WebView webView = getWebView();
        adWebJsInterface = new ADWebJsInterface(this)
                .withWebView(webView);
        webView.addJavascriptInterface(adWebJsInterface, adWebJsInterface.toString());

        String storeURL = createStoreWebURL();
        webView.loadUrl(storeURL);
    }

    private String createStoreWebURL() {
        String url = String.format("%sPharmacyInfo?orgguid=%s&guid=%s&token=%s", FacadeConfig.getUrl(), UserConfig.getInstance().getOrgCode(), localStoreId, FacadeToken.getInstance().getAuthToken());
        return url;
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
    protected String getActivityName() {
        return localStoreName;
    }
}

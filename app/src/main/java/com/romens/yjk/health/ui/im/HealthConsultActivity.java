package com.romens.yjk.health.ui.im;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fasterxml.jackson.databind.JsonNode;
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
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.ui.base.WebActivity;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;

import java.util.HashMap;

/**
 * Created by siery on 16/1/28.
 */
public class HealthConsultActivity extends WebActivity {
    private final HashMap<String, String> config = new HashMap<>();
    private JsBaseInterface adWebJsInterface;
    private boolean isConnectIMServer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getMyActionBar();
        actionBar.setBackgroundColor(0xfff0f0f0);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_grey600_24dp);
        needActionBarDivider(actionBar, true);
        setActionBarTitle(actionBar, "用药咨询");
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

        loadCallCenterConfig();
    }

    private void loadCallCenterConfigProgress(boolean progress) {
        ActionBar actionBar = getMyActionBar();
        if (progress) {
            setActionBarTitle(actionBar, "");
            setActionBarTitleOverlayText(actionBar, "正在连接药师...");
        } else {
            setActionBarTitle(actionBar, "用药咨询");
            setActionBarTitleOverlayText(actionBar, "");
        }

    }

    protected void loadCallCenterConfig() {
        config.clear();
        loadCallCenterConfigProgress(true);
        HashMap<String, String> args = new HashMap<>();
        args.put("ORGGUID", UserConfig.getInstance().getOrgCode());
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetCustomerURL", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(HealthConsultActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        loadCallCenterConfigProgress(false);
                        String error = null;
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (response.has("ERROR")) {
                                error = response.get("ERROR").asText();
                            } else {
                                onLoadCallCenterConfig(response, error);
                                return;
                            }
                        } else {
                            error = "获取客服配置信息失败!";
                        }
                        onLoadCallCenterConfig(null, error);
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void onLoadCallCenterConfig(JsonNode response, String error) {
        if (TextUtils.isEmpty(error)) {
            config.put("URL", response.get("URL").asText());
        }
        tryConnectCallCenter();
    }

    private void tryConnectCallCenter() {
        isConnectIMServer = false;
        if (config.containsKey("URL")) {
            String url = config.get("URL");
            if (!TextUtils.isEmpty(url)) {
                isConnectIMServer = true;
                getWebView().loadUrl(url);
            }
        }

    }

    @Override
    protected void onWebPageCompleted() {
        ActionBar actionBar = getMyActionBar();
        setActionBarTitle(actionBar, isConnectIMServer ? "用药咨询" : "用药咨询(未连接)");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected String getActivityName() {
        return "用药咨询";
    }
}

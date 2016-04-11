package com.romens.yjk.health.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.BottomSheet;
import com.romens.yjk.health.R;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.ui.base.WebActivity;
import com.romens.yjk.health.ui.components.ToastCell;
import com.romens.yjk.health.web.ADWebJsInterface;
import com.romens.yjk.health.web.JsBaseInterface;
import com.romens.yjk.health.wx.WXHelper;
import com.romens.yjk.health.wx.WXManager;

/**
 * Created by siery on 15/9/9.
 */
public class ADWebActivity extends WebActivity implements AppNotificationCenter.NotificationCenterDelegate {
    public static final String ARGUMENTS_KEY_TITLE = "title";
    public static final String ARGUMENTS_KEY_TARGET_URL = "target_url";
    public static final String ARGUMENTS_KEY_HTML = "html";
    public static final String ARGUMENTS_KEY_ICON_URL = "thumbURL";

    private JsBaseInterface adWebJsInterface;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppNotificationCenter.getInstance().addObserver(this, AppNotificationCenter.loginSuccess);
        bundle = getIntent().getExtras();
        ActionBar actionBar = getMyActionBar();
        ActionBarMenu menu = actionBar.createMenu();
        //menu.addItem(0, R.drawable.ic_share_grey600_24dp);

        if (bundle.containsKey(ARGUMENTS_KEY_TITLE)) {
            String title = bundle.getString(ARGUMENTS_KEY_TITLE);
            setActionBarTitle(actionBar, title);
        } else {
            setActionBarTitle(actionBar, "");
        }

        WebView webView = getWebView();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        AppNotificationCenter.getInstance().removeObserver(this, AppNotificationCenter.loginSuccess);
        super.onDestroy();
    }

    @Override
    public void didReceivedNotification(int i, Object... objects) {
        if (i == AppNotificationCenter.loginSuccess) {
            getWebView().reload();
        }
    }

    @Override
    protected void menuItemClick() {
        super.menuItemClick();
        showShareMenu(bundle);
    }

    private void showShareMenu(final Bundle shareArguments) {
        BottomSheet shareDialog = new BottomSheet.Builder(this)
                .setIsGrid(true)
                .setTitle("分享")
                .setItems(new CharSequence[]{"发送朋友", "分享朋友圈", "收藏"}, new int[]{R.drawable.icon_wx_logo, R.drawable.icon_wx_moments, R.drawable.icon_wx_collect}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        if (WXHelper.checkWXApp()) {
                            if (shareArguments != null) {
                                final String shareURL = "http://www.baidu.com";
                                final String shareTitle = shareArguments.getString(ARGUMENTS_KEY_TITLE);
                                final String shareDesc = shareArguments.getString(ARGUMENTS_KEY_HTML);
                                if (shareArguments.containsKey("thumb")) {
                                    byte[] shareThumb = shareArguments.getByteArray("thumb");
                                    shareToWX(which, shareURL, shareTitle, shareDesc, null);
                                } else if (shareArguments.containsKey("thumbURL")) {
                                    new ThumbURLAsyncTask(which, shareURL, shareTitle, shareDesc).execute(shareArguments.getString("thumbURL", ""));
                                }
                            }
                        } else {
                            ToastCell.toast(ADWebActivity.this, "未安装微信");
                        }
                    }
                }).create();
        shareDialog.show();
    }

    /**
     * 异步获取封面图标
     */
    private class ThumbURLAsyncTask extends AsyncTask<String, Void, byte[]> {
        private final int which;
        private final String shareURL;
        private final String shareTitle;
        private final String shareDesc;

        public ThumbURLAsyncTask(int which, String url, String title, String desc) {
            this.which = which;
            this.shareURL = url;
            this.shareTitle = title;
            this.shareDesc = desc;
        }

        @Override
        protected byte[] doInBackground(String... params) {
            final byte[] shareThumb = WXHelper.getHtmlByteArray(params[0]);
            return shareThumb;
        }

        @Override
        protected void onPostExecute(byte[] result) {
            shareToWX(which, shareURL, shareTitle, shareDesc, result);
        }
    }

    private void shareToWX(int type, String shareURL, String shareTitle, String shareDesc, byte[] shareThumb) {
        boolean isSuccess = false;
        if (type == 0) {
            isSuccess = WXManager.getInstance(ADWebActivity.this).shareURLToUser(shareURL, shareTitle, shareDesc, shareThumb);
        } else if (type == 1) {
            isSuccess = WXManager.getInstance(ADWebActivity.this).shareURLToTimeline(shareURL, shareTitle, shareDesc, shareThumb);
        } else if (type == 2) {
            isSuccess = WXManager.getInstance(ADWebActivity.this).shareURLToFavorite(shareURL, shareTitle, shareDesc, shareThumb);
        }
        if (isSuccess) {
            Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.romens.yjk.health.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.gc.materialdesign.views.ProgressBarDeterminate;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/5/5.
 */
public class WebActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private ProgressBarDeterminate mWebProgress;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        setContentView(content);
        actionBar = new ActionBar(this);
        actionBar.setBackgroundColor(Color.WHITE);
        actionBar.setItemsBackground(R.drawable.bar_selector);
        actionBar.setBackButtonImage(R.drawable.ic_clear_grey600_24dp);

        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        FrameLayout frameLayout = new FrameLayout(this);
        content.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));


        mWebView = new WebView(this);
        content.addView(mWebView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        WebSettings settings = mWebView.getSettings();
        settings.setAllowFileAccess(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);


        mWebProgress = new ProgressBarDeterminate(this, null);
        mWebProgress.setBackgroundColor(0xff1E88E5);
        mWebProgress.setMinimumHeight(AndroidUtilities.dp(2));
        mWebProgress.setMax(AndroidUtilities.dp(2));
        mWebProgress.setMax(100);
        mWebProgress.setMin(0);

        frameLayout.addView(mWebProgress, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));
    }


    protected ActionBar getMyActionBar() {
        return actionBar;
    }

    protected WebView getWebView() {
        return this.mWebView;
    }

    protected ProgressBarDeterminate getWebProgressBar() {
        return this.mWebProgress;
    }
}

package com.romens.yjk.health.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.extend.scanner.CaptureActivity;
import com.romens.extend.scanner.ViewfinderView;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/5/13.
 */
public class ScannerActivity extends CaptureActivity {

    private static final String TAG = ScannerActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    protected void onSetupContentView() {
        FrameLayout content = new FrameLayout(this);
        setContentView(content);
        surfaceView = new SurfaceView(this);
        content.addView(surfaceView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        viewfinderView = new ViewfinderView(this, null);
        viewfinderView.setLaserColor(Color.RED);
        viewfinderView.setResultPointColor(Color.RED);
        viewfinderView.setBorderColor(Color.RED);
        content.addView(viewfinderView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));
        actionBar.setBackgroundColor(Color.TRANSPARENT);
        actionBar.setItemsBackground(R.drawable.bar_selector);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                }
            }
        });
        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_other);
        item.addSubItem(1, "帮助", 0);
    }
}

package com.romens.yjk.health.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.romens.android.log.FileLog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/8/10.
 */
public class BaseActivity extends AppCompatActivity {
    private ActionBar actionBar;


    private Dialog visibleDialog = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
    }

    protected void setContentView(int contentResId, int actionBarResId) {
        setContentView(contentResId);
        if (actionBarResId != -1) {
            actionBar = (ActionBar) findViewById(actionBarResId);
            onSetupActionBar(actionBar);
        } else {
            actionBar = null;
        }
    }

    protected void setContentView(View contentView, ActionBar bar) {
        setContentView(contentView);
        if (bar != null) {
            actionBar = bar;
            onSetupActionBar(actionBar);
        } else {
            actionBar = null;
        }
    }

    protected void onSetupActionBar(ActionBar actionBar) {
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setItemsBackground(R.drawable.bar_selector);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
    }

    protected ActionBarMenuItem addActionBarSearchItem(ActionBarMenu menu, int id, int iconResId, ActionBarMenuItem.ActionBarMenuItemSearchListener listener) {
        return menu.addItem(id, iconResId).setIsSearchField(true).setActionBarMenuItemSearchListener(listener);
    }

    protected ActionBar getMyActionBar() {
        return actionBar;
    }

    public void needShowProgress(String progressText) {
        if (progressDialog != null) {
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(progressText);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void needHideProgress() {
        if (progressDialog == null) {
            return;
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(getPackageName(), e);
        }
        progressDialog = null;
    }


    public Dialog showDialog(Dialog dialog) {
        if (dialog == null) {
            return null;
        }
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(getPackageName(), e);
        }
        try {
            visibleDialog = dialog;
            visibleDialog.setCanceledOnTouchOutside(true);
            visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    visibleDialog = null;
                    onDialogDismiss();
                }
            });
            visibleDialog.show();
            return visibleDialog;
        } catch (Exception e) {
            FileLog.e(getPackageName(), e);
        }
        return null;
    }

    protected void onDialogDismiss() {

    }

    protected boolean enableResetNotifier() {
        return true;
    }

    protected void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域
                    int srollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;
                    root.scrollTo(0, srollHeight);
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
        });
    }

}

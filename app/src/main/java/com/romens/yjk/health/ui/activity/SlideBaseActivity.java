package com.romens.yjk.health.ui.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.BaseActivity;
import com.romens.yjk.health.ui.components.SlideView;

import java.util.Set;

/**
 * Created by siery on 15/9/17.
 */
public abstract class SlideBaseActivity extends BaseActivity {

    private int currentViewNum = 0;
    private int pageCount;
    private SlideView[] views;
    private ProgressDialog progressDialog;
    private final static int done_button = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer contentView = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        contentView.addView(actionBar);

        ScrollView fragmentView = new ScrollView(this);
        ScrollView scrollView = fragmentView;
        scrollView.setFillViewport(true);
        contentView.addView(fragmentView);

        FrameLayout frameLayout = new FrameLayout(this);
        scrollView.addView(frameLayout);
        ScrollView.LayoutParams layoutParams = (ScrollView.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.width = ScrollView.LayoutParams.MATCH_PARENT;
        layoutParams.height = ScrollView.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        frameLayout.setLayoutParams(layoutParams);

        pageCount = getPageCount();
        views = new SlideView[pageCount];
        for (int i = 0; i < pageCount; i++) {
            views[i] = onCreatePage(i);
        }
        for (int a = 0; a < pageCount; a++) {
            views[a].setVisibility(a == 0 ? View.VISIBLE : View.GONE);
            frameLayout.addView(views[a]);
            FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) views[a].getLayoutParams();
            layoutParams1.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams1.height = a == 0 ? FrameLayout.LayoutParams.WRAP_CONTENT : FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams1.leftMargin = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26 : 18);
            layoutParams1.rightMargin = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26 : 18);
            layoutParams1.topMargin = AndroidUtilities.dp(30);
            layoutParams1.gravity = Gravity.TOP | Gravity.LEFT;
            views[a].setLayoutParams(layoutParams1);
        }
        currentViewNum = getStartPagePosition();
        actionBar.setTitle(views[currentViewNum].getHeaderName());
        for (int a = 0; a < views.length; a++) {
            if (currentViewNum == a) {
                actionBar.setBackButtonImage(views[a].needBackButton() ? R.drawable.ic_ab_back : 0);
                views[a].setVisibility(View.VISIBLE);
                views[a].onShow();
            } else {
                views[a].setVisibility(View.GONE);
            }
        }


        setContentView(contentView, actionBar);
        actionBar = getMyActionBar();
        actionBar.setTitle(getString(R.string.app_name));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == done_button) {
                    views[currentViewNum].onNextPressed();
                } else if (id == -1) {
                    onBackPressed();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));
    }

    protected abstract int getPageCount();

    protected abstract SlideView onCreatePage(int position);

    protected abstract int getStartPagePosition();

    @Override
    protected boolean enableResetNotifier() {
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        for (SlideView v : views) {
            if (v != null) {
                v.onDestroyActivity();
            }
        }
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e("romens", e);
            }
            progressDialog = null;
        }
    }

    private void putBundleToEditor(Bundle bundle, SharedPreferences.Editor editor, String prefix) {
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            Object obj = bundle.get(key);
            if (obj instanceof String) {
                if (prefix != null) {
                    editor.putString(prefix + "_|_" + key, (String) obj);
                } else {
                    editor.putString(key, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (prefix != null) {
                    editor.putInt(prefix + "_|_" + key, (Integer) obj);
                } else {
                    editor.putInt(key, (Integer) obj);
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, key);
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isFinish = false;
        boolean isPageBack = onPageBackPressed(currentViewNum);
        if (!isPageBack) {
            for (SlideView v : views) {
                if (v != null) {
                    v.onDestroyActivity();
                }
            }
            isFinish = true;
        } else {
            views[currentViewNum].onBackPressed();
        }
        if (isFinish) {
            needFinishActivity();
            finish();
        }
    }

    protected abstract boolean onPageBackPressed(int position);

    public void needShowAlert(String title, String text) {
        if (text == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton("确定", null);
        showDialog(builder.create());
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        if (Build.VERSION.SDK_INT > 13) {
            final SlideView outView = views[currentViewNum];
            final SlideView newView = views[page];
            currentViewNum = page;
            ActionBar actionBar = getMyActionBar();
            actionBar.setBackButtonImage(newView.needBackButton() ? R.drawable.ic_ab_back : 0);

            newView.setParams(params);
            actionBar.setTitle(newView.getHeaderName());
            newView.onShow();
            newView.setX(back ? -AndroidUtilities.displaySize.x : AndroidUtilities.displaySize.x);
            outView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    outView.setVisibility(View.GONE);
                    outView.setX(0);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            }).setDuration(300).translationX(back ? AndroidUtilities.displaySize.x : -AndroidUtilities.displaySize.x).start();
            newView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    newView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            }).setDuration(300).translationX(0).start();
        } else {
            ActionBar actionBar = getMyActionBar();
            actionBar.setBackButtonImage(views[page].needBackButton() ? R.drawable.ic_ab_back : 0);
            views[currentViewNum].setVisibility(View.GONE);
            currentViewNum = page;
            views[page].setParams(params);
            views[page].setVisibility(View.VISIBLE);
            actionBar.setTitle(views[page].getHeaderName());
            views[page].onShow();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSelfArgs(outState);
    }

    public void saveSelfArgs(Bundle outState) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", currentViewNum);
            for (int a = 0; a <= currentViewNum; a++) {
                SlideView v = views[a];
                if (v != null) {
                    v.saveStateParams(bundle);
                }
            }
            SharedPreferences preferences = MyApplication.applicationContext.getSharedPreferences("logininfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            putBundleToEditor(bundle, editor, null);
            editor.commit();
        } catch (Exception e) {
            FileLog.e("romens", e);
        }
    }

    public abstract void needFinishActivity();
}

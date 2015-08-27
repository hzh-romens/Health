package com.romens.yjk.health.ui.utils;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;

import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/8/13.
 */
public class UIHelper {
    public static void setupSwipeRefreshLayoutProgress(SwipeRefreshLayout refreshLayout) {
        if (refreshLayout != null) {
            refreshLayout.setColorSchemeResources(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3);
        }
    }

    public static void updateSwipeRefreshProgressBarTop(Context context, SwipeRefreshLayout refreshLayout) {
        if (refreshLayout == null) {
            return;
        }
        int end = AndroidUtilities.getCurrentActionBarHeight();
        refreshLayout.setProgressViewOffset(false, 0, end);
    }
}

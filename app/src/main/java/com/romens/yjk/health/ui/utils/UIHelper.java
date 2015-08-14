package com.romens.yjk.health.ui.utils;

import android.support.v4.widget.SwipeRefreshLayout;

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
}

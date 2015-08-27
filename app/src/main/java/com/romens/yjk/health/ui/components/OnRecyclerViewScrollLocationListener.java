package com.romens.yjk.health.ui.components;

import android.support.v7.widget.RecyclerView;

/**
 * Created by hzh on 2015/8/12.
 */
public interface OnRecyclerViewScrollLocationListener {
    void onTopWhenScrollIdle(RecyclerView recyclerView);

    void onBottomWhenScrollIdle(RecyclerView recyclerView);
}

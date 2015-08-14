package com.romens.yjk.health.ui.components;

/**
 * 普通的滚动监听，可以在LayoutManager中获取到RecyclerViewScrollManager后add多个
 * Created by romens007 on 2015/8/12.
 */
public interface OnRecyclerViewScrollListener {
    public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState);

    public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy);
}

package com.romens.yjk.health.ui.components;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by siery on 15/8/18.
 */
public interface FlowLayoutCallback {
    int getCount();

    View getView(int position, ViewGroup container);
}

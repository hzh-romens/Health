package com.romens.yjk.health.ui.activity;

import com.romens.android.ui.base.BaseActionBarActivity;
import com.tencent.stat.StatService;

/**
 * @author Zhou Lisi
 * @create 16/2/20
 * @description
 */
public class BaseActionBarActivityWithAnalytics extends BaseActionBarActivity {

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);      //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
}

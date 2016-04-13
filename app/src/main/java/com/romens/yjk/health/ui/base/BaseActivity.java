package com.romens.yjk.health.ui.base;

import com.romens.android.ui.base.BaseActionBarActivity;
import com.tencent.stat.StatService;

/**
 * Created by siery on 15/12/14.
 */
public abstract class BaseActivity extends BaseActionBarActivity {
    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);      //统计时长
        StatService.trackBeginPage(this,getActivityName());
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
        StatService.trackEndPage(this,getActivityName());
    }

    protected abstract String getActivityName();
}

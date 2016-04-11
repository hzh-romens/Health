package com.romens.yjk.health.ui.base;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.android.ui.base.BaseActionBarActivity;
import com.romens.yjk.health.R;
import com.tencent.stat.StatService;

/**
 * Created by siery on 15/10/23.
 */
public class LightActionBarActivity extends BaseActionBarActivity {

    @Override
    protected void onSetupActionBar(ActionBar actionBar) {
        actionBar.setBackgroundColor(0xfff0f0f0);
        actionBar.setItemsBackground(R.drawable.bar_selector);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_grey600_24dp);
        needActionBarDivider(actionBar, true);
    }

    protected ActionBarMenuItem addActionBarSearchItem(ActionBarMenu menu, int id, ActionBarMenuItem.ActionBarMenuItemSearchListener listener) {
        return menu.addItem(id, R.drawable.ic_search_grey600_24dp).setIsSearchField(0xff000000,0x88000000, R.drawable.search_carret, R.drawable.ic_clear_grey600_24dp).setActionBarMenuItemSearchListener(listener);
    }

    @Override
    protected ActionBarMenuItem addActionBarSearchItem(ActionBarMenu menu, int id, int iconResId, ActionBarMenuItem.ActionBarMenuItemSearchListener listener) {
        return menu.addItem(id, iconResId).setIsSearchField(0xff000000, 0x88000000, R.drawable.search_carret, R.drawable.ic_clear_grey600_24dp).setActionBarMenuItemSearchListener(listener);
    }

    protected void needActionBarDivider(ActionBar actionBar, boolean divider) {
        actionBar.needDivider(divider);
    }

    protected void setActionBarTitle(ActionBar actionBar, CharSequence title) {
        if (actionBar != null) {
            actionBar.setTitle(title, 0xff212121);
        }
    }

    protected void setActionBarTitleOverlayText(ActionBar actionBar, String title) {
        if (actionBar != null) {
            actionBar.setTitleOverlayText(title, 0xff212121);
        }
    }

    protected void setActionBarSubTitle(ActionBar actionBar, CharSequence title) {
        if (actionBar != null) {
            actionBar.setTitle(title, 0x8a000000);
        }
    }

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

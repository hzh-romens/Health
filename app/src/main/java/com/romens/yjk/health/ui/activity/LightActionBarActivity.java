package com.romens.yjk.health.ui.activity;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.base.BaseActionBarActivity;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/10/23.
 */
public class LightActionBarActivity extends BaseActionBarActivity {

    @Override
    protected void onSetupActionBar(ActionBar actionBar) {
        actionBar.setBackgroundColor(0xffeeeeee);
        actionBar.setItemsBackground(R.drawable.bar_selector);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_grey600_24dp);
        needActionBarDivider(actionBar, true);
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
}

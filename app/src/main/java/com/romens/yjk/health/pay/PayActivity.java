package com.romens.yjk.health.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;

/**
 * @author Zhou Lisi
 * @create 16/3/1
 * @description
 */
public abstract class PayActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENTS_KEY_FROM_PAY_PREPARE = "yjk_key_from_pay_prepare";
    public static final String ARGUMENTS_KEY_PAY_PARAMS = "yjk_key_pay_params";

    protected boolean isFromPayPrepare = false;
    protected Bundle payParams;

    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    needFinish();
                }
            }
        });
        actionBar.setBackButtonImage(R.drawable.ic_close_white_24dp);
        FrameLayout listContainer = new FrameLayout(this);
        content.addView(listContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new ListView(this);
        listContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setSelector(R.drawable.list_selector);
    }

    protected void onCreateAfter() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(ARGUMENTS_KEY_FROM_PAY_PREPARE)) {
            isFromPayPrepare = extras.getBoolean(ARGUMENTS_KEY_FROM_PAY_PREPARE, false);
        } else {
            isFromPayPrepare = false;
        }
        if (isFromPayPrepare) {
            payParams = extras.getBundle(ARGUMENTS_KEY_PAY_PARAMS);
            onPayRequest(payParams);
        } else {
            onPayResponse(intent);
        }
        updateAdapter();
    }

    protected abstract void onPayRequest(Bundle payParams);

    protected abstract void onPayResponse(Intent intent);

    protected abstract void onCheckPayState();

    @Override
    public void onBackPressed() {
        needFinish();
    }


    protected abstract void needFinish();

    @Override
    public void onResume() {
        super.onResume();
        if (isFromPayPrepare) {
            onCheckPayState();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        onPayResponse(intent);
    }

    protected void updateAdapter() {
        rowCount = 0;
    }

    protected int rowCount;

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}

package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.ui.components.CardSelector;

/**
 * @author Zhou Lisi
 * @create 16/2/24
 * @description
 */
public class CommitOrderActivity extends BaseActionBarActivityWithAnalytics {
    private RecyclerView listView;
    private ListAdapter adapter;

    private FrameLayout bottomBar;
    private TextView amountDescView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        content.setBackgroundColor(0xffffffff);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);
        actionBar.setTitle("提交订单");
        FrameLayout dataContainer = new FrameLayout(this);
        content.addView(dataContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView = new RecyclerView(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        dataContainer.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 64));

        bottomBar= new FrameLayout(this);
        bottomBar.setBackgroundColor(0xfff0f0f0);
        dataContainer.addView(bottomBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 64, Gravity.BOTTOM));
        amountDescView = new TextView(this);
        amountDescView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        AndroidUtilities.setMaterialTypeface(amountDescView);
        amountDescView.setTextColor(0xff212121);
        amountDescView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        amountDescView.setText("小计(3个商品) ￥10000");
        bottomBar.addView(amountDescView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 8, 96, 8));

        LinearLayout commitBtn = new LinearLayout(this);
        commitBtn.setOrientation(LinearLayout.HORIZONTAL);
        commitBtn.setGravity(Gravity.CENTER_VERTICAL);
        commitBtn.setClickable(true);
        commitBtn.setBackgroundResource(R.drawable.btn_primary);
        commitBtn.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        ImageView commitIcon = new ImageView(this);
        commitIcon.setScaleType(ImageView.ScaleType.CENTER);
        commitIcon.setImageResource(R.drawable.ic_done_all_white_24dp);
        commitBtn.addView(commitIcon, LayoutHelper.createLinear(24, 24));

        TextView commitText = new TextView(this);
        commitText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        AndroidUtilities.setMaterialTypeface(commitText);
        commitText.setTextColor(0xffffffff);
        commitText.setGravity(Gravity.CENTER);
        commitText.setText("结算");
        commitBtn.addView(commitText, LayoutHelper.createLinear(56, LayoutHelper.WRAP_CONTENT));
        bottomBar.addView(commitBtn, LayoutHelper.createFrame(88, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 8, 8, 8));

        adapter = new ListAdapter();
        listView.setAdapter(adapter);

        updateAdapter();
    }

    private int rowCount;
    private int a;
    private int b;
    private int c;

    private void updateAdapter() {
        rowCount = 0;
        a = rowCount++;
        b = rowCount++;
        c = rowCount++;
    }


    class ListAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextSettingsCell cell = new TextSettingsCell(parent.getContext());
            return new Holder(cell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextSettingsCell cell = (TextSettingsCell) holder.itemView;
            if (position == 0) {
                CardSelector.set(cell, CardSelector.Mode.BEGIN);
            } else if (position == (getItemCount() - 1)) {
                CardSelector.set(cell, CardSelector.Mode.END);
            } else {
                CardSelector.set(cell, CardSelector.Mode.MIDDLE);
            }

            cell.setTextAndValue("a", "a", true);
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}

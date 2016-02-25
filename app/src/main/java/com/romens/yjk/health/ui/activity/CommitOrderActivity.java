package com.romens.yjk.health.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.ui.components.CardSelector;

/**
 * @author Zhou Lisi
 * @create 16/2/24
 * @description
 */
public class CommitOrderActivity extends BaseActionBarActivityWithAnalytics {
    private RecyclerView listView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        content.setBackgroundColor(0xfff0f0f0);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        listView = new RecyclerView(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 8, 16, 8, 16));

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

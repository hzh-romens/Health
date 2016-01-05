package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.model.TimesAdapterCallBack;
import com.romens.yjk.health.ui.adapter.TimesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/11/16.
 */
public class AddRemindTimesActivity extends BaseActivity implements TimesAdapterCallBack {

    private ListView listView;
    private TimesAdapter timesAdapter;

    private ArrayList<String> timesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        container.setBackgroundResource(R.color.line_color);
        setContentView(container, actionBar);
        actionBarEvent();

        initData();

        FrameLayout frameLayout = new FrameLayout(this);
        container.addView(frameLayout, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView = new ListView(this);
        listView.setVerticalScrollBarEnabled(false);
        TextInfoCell cell=new TextInfoCell(this);
        cell.setBackgroundColor(Color.WHITE);
        cell.setText("长按删除单条记录");
        listView.addFooterView(cell);
        timesAdapter = new TimesAdapter(timesData, this, this);
        listView.setAdapter(timesAdapter);
        listView.setSelector(R.drawable.list_selector);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        FloatingActionButton actionButton = new FloatingActionButton(this);
        actionButton.setImageResource(R.drawable.ic_add_white_24dp);
        actionButton.setColorNormal(getResources().getColor(R.color.theme_primary));
        actionButton.setColorPressed(getResources().getColor(R.color.line_color));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timesData.size() < 5) {
                    timesData.add("08:30");
                }
                timesAdapter.notifyDataSetChanged();
            }
        });
        FrameLayout.LayoutParams actionButtonLp = LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        actionButtonLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        actionButtonLp.bottomMargin = AndroidUtilities.dp(16);
        actionButtonLp.rightMargin = AndroidUtilities.dp(16);
        actionButton.setLayoutParams(actionButtonLp);
        frameLayout.addView(actionButton);
    }

    private void initData() {
        timesData = getIntent().getStringArrayListExtra("timesDataList");
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("服药时间");
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setBackgroundResource(R.color.theme_primary);
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.checkbig);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    Intent intent = new Intent(AddRemindTimesActivity.this, AddNewRemindActivity.class);
                    intent.putStringArrayListExtra("resultTimesDataList", timesData);
                    setResult(UserGuidConfig.RESPONSE_REMIND_TIMES_TO_NEW_REMIND, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void setTimesData(List<String> data) {
        timesData = (ArrayList<String>) data;
    }
}

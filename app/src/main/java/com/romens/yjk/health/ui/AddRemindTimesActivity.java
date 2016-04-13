package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.library.datetimepicker.time.RadialPickerLayout;
import com.romens.android.library.datetimepicker.time.TimePickerDialog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.model.TimesAdapterCallBack;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.adapter.TimesAdapter;
import com.romens.yjk.health.ui.components.SwipeDismissListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by anlc on 2015/11/16.
 */
public class AddRemindTimesActivity extends DarkActionBarActivity implements TimesAdapterCallBack {

    private SwipeDismissListView listView;
    private TimesAdapter timesAdapter;

    private ArrayList<String> timesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_add_time, R.id.action_bar);
        actionBarEvent();

        initData();

        listView = (SwipeDismissListView) findViewById(R.id.remind_add_time_list);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
//        listView.addFooterView(new HintViewCell(this));
        timesAdapter = new TimesAdapter(timesData, this, this);
        listView.setAdapter(timesAdapter);
        listView.setSelector(R.drawable.list_selector);
        listView.setOnDismissCallback(new SwipeDismissListView.OnDismissCallback() {
            @Override
            public void onDismiss(int dismissPosition) {
                //删除本条记录
                if (timesData.size() > 1) {
                    timesData.remove(dismissPosition);
                    timesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "默认选项不能删除", Toast.LENGTH_SHORT).show();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        String time = amendTime(hourOfDay) + ":" + amendTime(minute);
                        timesData.remove(position);
                        timesData.add(position, time);
                        timesAdapter.notifyDataSetChanged();
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
                timePickerDialog.setVibrate(true);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(AddRemindTimesActivity.this.getSupportFragmentManager(), "timepicker");
            }
        });

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.remind_add_time_addbtn);
        actionButton.setImageResource(R.drawable.ic_add_white_24dp);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timesData.size() < 5) {
                    int hour = Integer.parseInt(timesData.get(timesData.size() - 1).split(":")[0]);
                    if (hour < 23) {
                        hour++;
                    } else {
                        hour = 0;
                    }
                    int minute = Integer.parseInt(timesData.get(timesData.size() - 1).split(":")[1]);
                    TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                            String time = amendTime(hourOfDay) + ":" + amendTime(minute);
                            timesData.add(time);
                            timesAdapter.notifyDataSetChanged();
                        }
                    }, hour, minute, false, false);
                    timePickerDialog.setVibrate(true);
                    timePickerDialog.setCloseOnSingleTapMinute(false);
                    timePickerDialog.show((AddRemindTimesActivity.this).getSupportFragmentManager(), TimesAdapter.TIMEPICKER_TAG);
                }
            }
        });
        FrameLayout.LayoutParams actionButtonLp = LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        actionButtonLp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        actionButtonLp.bottomMargin = AndroidUtilities.dp(16);
        actionButtonLp.rightMargin = AndroidUtilities.dp(16);
        actionButton.setLayoutParams(actionButtonLp);
    }

    private void initData() {
        timesData = getIntent().getStringArrayListExtra("timesDataList");
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("服药时间");
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    Intent intent = new Intent(AddRemindTimesActivity.this, AddNewRemindActivity.class);
                    intent.putStringArrayListExtra("resultTimesDataList", timesData);
                    setResult(UserGuidConfig.RESPONSE_REMIND_TIMES_TO_NEW_REMIND, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(AddRemindTimesActivity.this, AddNewRemindActivity.class);
            intent.putStringArrayListExtra("resultTimesDataList", timesData);
            setResult(UserGuidConfig.RESPONSE_REMIND_TIMES_TO_NEW_REMIND, intent);
            finish();
        }
        return false;
    }

    @Override
    protected String getActivityName() {
        return "新增用药提醒时间";
    }

    class HintViewCell extends FrameLayout {

        public HintViewCell(Context context) {
            super(context);
            TextView textView = new TextView(context);
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setTextColor(getResources().getColor(R.color.theme_sub_title));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            textView.setText("左划删除单条记录");
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine(true);
            textView.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(8), AndroidUtilities.dp(20), AndroidUtilities.dp(8));
            FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
            addView(textView, layoutParams);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
        }
    }

    @Override
    public void setTimesData(List<String> data) {
        timesData = (ArrayList<String>) data;
    }

    public String amendTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return time + "";
        }
    }
}

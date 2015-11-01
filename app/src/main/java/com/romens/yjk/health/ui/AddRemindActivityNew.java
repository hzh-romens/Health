package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.cells.AddRemindTimesDailog;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/30.
 */
public class AddRemindActivityNew extends BaseActivity implements View.OnClickListener {

    private EditText drugEditTxt;
    private TextView dosageCountTxtView;
    private TextView dosageTextView;
    private TextView addTimeTxtView;
    private TextView dayTimersTxtView;
    private EditText remarkEditTxt;

    private List<String> timesData;
    private List<String> timesDataTemp;
    private int oldTimes = 1;
    private int intervalDay;

    private String chooseDate;
    private int isRemind = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind, R.id.action_bar);
        actionBarEvent();
        initData();
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remind_dosage:
                showChoosDosageView(dosageTextView);
                break;
            case R.id.remind_time_add:
                new AddRemindTimesDailog(this, timesDataTemp, oldTimes).show();
                break;
            case R.id.remind_day_timers:
                showIntervalDayDialog();
                break;
        }
    }

    private void initView() {
        drugEditTxt = (EditText) findViewById(R.id.remind_edit_drug);
        dosageTextView = (TextView) findViewById(R.id.remind_dosage);
        dosageCountTxtView = (TextView) findViewById(R.id.remind_count);
        addTimeTxtView = (TextView) findViewById(R.id.remind_time_add);
        dayTimersTxtView = (TextView) findViewById(R.id.remind_day_timers);
        remarkEditTxt = (EditText) findViewById(R.id.remind_edit_remark);


        addTimeTxtView.setOnClickListener(this);
        dayTimersTxtView.setOnClickListener(this);
        dosageTextView.setOnClickListener(this);
        dosageCountTxtView.setText("1");
        addTimeTxtView.setText(timesDataTemp.get(0));
        dayTimersTxtView.setText(intervalDay == 1 ? "每天" : "每" + intervalDay + "天");
    }

    private void initData() {
        timesData = new ArrayList<>();
        timesDataTemp = new ArrayList<>();
        timesDataTemp.add("08:30");
        for (int i = 0; i < 5; i++) {
            timesData.add("-1");
        }

        intervalDay = 1;
        chooseDate = getCurrentDate();
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("用药提醒");
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
                    Toast.makeText(AddRemindActivityNew.this, "click", Toast.LENGTH_SHORT).show();
                    String userStr = drugEditTxt.getText().toString().trim();
                    if (userStr.equals("请输入药品") || userStr.equals("")) {
                        Toast.makeText(AddRemindActivityNew.this, "请输入药品", Toast.LENGTH_SHORT).show();
                    } else {
                        saveAddFinish();
                    }
                }
            }
        });
    }

    public String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(System.currentTimeMillis());
    }

    public void showChoosDosageView(View view) {
        final List<String> listData = new ArrayList<>();
        listData.add("片");
        listData.add("粒");
        listData.add("丸");
        listData.add("ML");
        listData.add("L");

        FrameLayout layout = new FrameLayout(this);
        ListView listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelection(R.drawable.list_selector);
        layout.addView(listView, LayoutHelper.createFrame(AndroidUtilities.dp(60), LayoutHelper.WRAP_CONTENT));
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.popupwindow_list_itme, listData));

        final PopupWindow popupWindow = new PopupWindow(layout, AndroidUtilities.dp(62), LayoutHelper.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.bg_white));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tag", "---->" + listData.get(position) + "");
                dosageTextView.setText(listData.get(position));
                popupWindow.dismiss();
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(view);

    }

    //保存到数据库并退出当前的activity
    private void saveAddFinish() {
        for (int i = 0; i < timesDataTemp.size(); i++) {
            timesData.set(i, timesDataTemp.get(i));
        }
        RemindEntity entity = new RemindEntity();
        entity.setUser("我");
        entity.setDrug(drugEditTxt.getText().toString().trim());
        entity.setIntervalDay(intervalDay);
        entity.setStartDate(chooseDate);
        int times = timesData.size();
        entity.setFirstTime(timesData.get(0));
        entity.setSecondtime(times > 1 ? timesData.get(1) : "-1");
        entity.setThreeTime(times > 2 ? timesData.get(2) : "-1");
        entity.setFourTime(times > 3 ? timesData.get(3) : "-1");
        entity.setFiveTime(times > 4 ? timesData.get(4) : "-1");
        entity.setTimesInDay(timesDataTemp.size());
        entity.setIsRemind(isRemind);
        entity.setRemark(remarkEditTxt.getText().toString().trim());
        entity.setDosage(dosageCountTxtView.getText().toString() + dosageTextView.getText().toString());
//        if (isRemind == 1) {
//            setRemind(entity);
//        }
//        if (isFromDetail) {
//            updateDb(entity);
//            Intent toRemindIntent = new Intent(AddRemindActivity.this, RemindActivity.class);
//            toRemindIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(toRemindIntent);
//        } else {
        writeDb(entity);
        finish();
//        }
    }

    private void writeDb(RemindEntity entity) {
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        remindDao.insert(entity);
    }

    public void showIntervalDayDialog() {
        String items[] = new String[]{"每天", "每2天", "每3天", "每4天", "每5天", "每6天"};
        new AlertDialog.Builder(this).setTitle("选择间隔天数").setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalDay = which + 1;
                dayTimersTxtView.setText(intervalDay == 1 ? "每天" : "每" + intervalDay + "天");
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void setTimesData(List<String> timesData) {
        this.timesDataTemp = timesData;
        if (timesData.size() > 4) {
            addTimeTxtView.setText(timesDataTemp.get(0) + "  " + timesDataTemp.get(1) + "  " + timesDataTemp.get(2) + "...");
        } else {
            String timesDataStr = "";
            for (int i = 0; i < timesData.size(); i++) {
                timesDataStr += timesDataTemp.get(i) + "  ";
            }
            addTimeTxtView.setText(timesDataStr.trim());
        }
    }

    public void setTimesInDay(int timesInDay) {
    }

}

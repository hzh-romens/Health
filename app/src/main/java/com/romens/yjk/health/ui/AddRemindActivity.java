package com.romens.yjk.health.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.library.datetimepicker.date.DatePickerDialog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.cells.AddRemindTimesDailog;
import com.romens.yjk.health.ui.cells.ChooseUserPopupWindow;
import com.romens.yjk.health.ui.components.AddRemindCallback;
import com.romens.yjk.health.ui.components.RemindReceiver;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by anlc on 2015/8/21.
 * 添加提醒页面
 */
public class AddRemindActivity extends BaseActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, AddRemindCallback {

    private RelativeLayout dayLayout;
    private RelativeLayout countLayout;
    private RelativeLayout remindLayout;
    private RelativeLayout startDateLayout;
    private TextView chooseUser;
    private TextView chooseDrug;
    private TextView timesHint;
    private TextView days;
    private TextView counts;
    private TextView startDateView;
    private EditText editUser;
    private EditText editDrug;
    private Switch remindFlag;
    private Button deleteBtn;

    private List<String> data;
    private int day = 1;
    private int oldDay = 1;
    private int oldTimes = 1;

    private int startRemindYear;
    private int startRemindMouth;
    private int startRemindDay;
    private int startRemindHour;
    private int startRemindMinute;

    private int isRemind = 0;
    private List<String> timesData;
    private List<String> timesDataTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind, R.id.add_remind_actionbar);
        ActionBar actionBar = getMyActionBar();

        initData();
        initView();
        actionBarEvent(actionBar);

        isRemindDetailFrom();
    }

    /**
     * 判断intent是否有值
     * 有：说明从详情页跳转过来，要编辑的
     * 无：说明是新增的
     */
    private void isRemindDetailFrom() {
        Intent intent = getIntent();
        RemindEntity entity = (RemindEntity) intent.getSerializableExtra("editEntity");
        if (entity != null) {
            viewSetData(entity);
        }
    }

    private void viewSetData(final RemindEntity entity) {
        deleteBtn = (Button) findViewById(R.id.add_remind_deleteBtn);
        deleteBtn.setVisibility(View.VISIBLE);
        editUser.setText(entity.getUser());
        editDrug.setText(entity.getDrug());
        timesHint.setText(entity.getCount());
        startDateView.setText(TransformDateUitls.getYearDate(Long.parseLong(entity.getStartDate())));
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDb(entity);
                cancelRemind(entity);
                Intent toRemindIntent = new Intent(AddRemindActivity.this, RemindActivity.class);
                toRemindIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toRemindIntent);
            }
        });
        timesData = new ArrayList<>();
        timesData.add(entity.getFirstTime());
        timesDateAdd(entity.getSecondtime());
        timesDateAdd(entity.getThreeTime());
        timesDateAdd(entity.getFourTime());
        timesDateAdd(entity.getFiveTime());
        counts.setText(timesData.size() + "次");
        timesDataTemp.clear();
        for (int j = 0; j < timesData.size(); j++) {
            if (!timesData.get(j).equals("-1")) {
                timesDataTemp.add(timesData.get(j));
            }
        }
    }

    private void timesDateAdd(String timeStr) {
        if (!timeStr.equals("-1")) {
            timesData.add(timeStr);
        }
    }

    private void actionBarEvent(ActionBar actionBar) {
        actionBar.setTitle("新增提醒");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.checkbig);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    String userStr = editUser.getText().toString().trim();
                    String drugStr = editDrug.getText().toString().trim();
                    if (userStr.equals("输入服药者") || userStr.equals("")) {
                        Toast.makeText(AddRemindActivity.this, "请输入服药者", Toast.LENGTH_SHORT).show();
                    } else if (drugStr.equals("获取药物") || drugStr.equals("")) {
                        Toast.makeText(AddRemindActivity.this, "请输入药物", Toast.LENGTH_SHORT).show();
                    } else {
                        saveAddFinish();
                    }
                }
            }
        });
    }

    //保存到数据库并退出当前的activity
    private void saveAddFinish() {
        for (int i = 0; i < timesDataTemp.size(); i++) {
            timesData.set(i, timesDataTemp.get(i));
        }
        RemindEntity entity = new RemindEntity();
        entity.setUserIcon(R.drawable.person_image_empty);
        entity.setUser(editUser.getText().toString());
        entity.setDrug(editDrug.getText().toString());
        entity.setCount("每" + day + "天服用" + oldTimes + "次");
        entity.setStartDate(getStartDate() + "");
        int times = timesData.size();
        entity.setFirstTime(timesData.get(0));
        entity.setSecondtime(timesData.get(1));
        entity.setThreeTime(timesData.get(2));
        entity.setFourTime(timesData.get(3));
        entity.setFiveTime(timesData.get(4));
        entity.setTimes(times);
        entity.setIsRemind(isRemind);
        if (isRemind == 1) {
            setRemind(entity);
        }
        writeDb(entity);
        finish();
    }

    private void initView() {
        chooseUser = (TextView) findViewById(R.id.add_remind_chooseuser);
        chooseDrug = (TextView) findViewById(R.id.add_remind_choosedrug);
        editUser = (EditText) findViewById(R.id.add_remind_edituser);
        editDrug = (EditText) findViewById(R.id.add_remind_editdrug);
        days = (TextView) findViewById(R.id.add_remind_days);
        counts = (TextView) findViewById(R.id.add_remind_counts);
        timesHint = (TextView) findViewById(R.id.add_remind_timeshint);
        startDateView = (TextView) findViewById(R.id.add_remind_startdate);
        remindFlag = (Switch) findViewById(R.id.add_remind_remindflag);
        dayLayout = (RelativeLayout) findViewById(R.id.day_layout);
        countLayout = (RelativeLayout) findViewById(R.id.count_layout);
        remindLayout = (RelativeLayout) findViewById(R.id.remind_layout);
        startDateLayout = (RelativeLayout) findViewById(R.id.start_date_layout);
        chooseDrug.setOnClickListener(this);
        chooseUser.setOnClickListener(this);
        dayLayout.setOnClickListener(this);
        countLayout.setOnClickListener(this);
        remindLayout.setOnClickListener(this);
        startDateLayout.setOnClickListener(this);
        remindFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isRemind = 1;
                }
            }
        });
        startDateView.setText(getCurrentTime());
    }

    public String getStartDate() {
        //需在设置完每个时间的字段后调用
        long startDate_long = 0;
        String startDate_Str = startRemindYear + "-" + startRemindMouth + "-" + startRemindDay + " " + startRemindHour + ":" + startRemindMinute;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = format.parse(startDate_Str);
            startDate_long = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate_long + "";
    }

    //获得当前时间
    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        startRemindYear = calendar.get(Calendar.YEAR);
        startRemindMouth = calendar.get(Calendar.MONTH) + 1;
        startRemindDay = calendar.get(Calendar.DAY_OF_MONTH);
        startRemindHour = calendar.get(Calendar.HOUR_OF_DAY);
        startRemindMinute = calendar.get(Calendar.MINUTE);
        return startRemindYear + "-" + startRemindMouth + "-" + startRemindDay;
    }

    //添加提醒到系统中
    public void setRemind(RemindEntity entity) {
        Date startDateTemp = TransformDateUitls.getDate(Long.parseLong(entity.getStartDate()));
        String startDateStr = TransformDateUitls.getLong(startDateTemp);
        long startDateLong = TransformDateUitls.getDate(startDateStr);

        setRemindTime(startDateLong, entity.getFirstTime());
        setRemindTime(startDateLong, entity.getSecondtime());
        setRemindTime(startDateLong, entity.getThreeTime());
        setRemindTime(startDateLong, entity.getFourTime());
        setRemindTime(startDateLong, entity.getFiveTime());
    }

    public void setRemindTime(long startDateLong, String timeStr) {
        Calendar currentDate = Calendar.getInstance();
        long intervalTime = 1000 * 60 * 60 * 24 * day;
        if (!timeStr.equals("-1")) {
            long time = TransformDateUitls.getTimeLong(timeStr);
            long remindTime = (startDateLong + time) + currentDate.getTimeZone().getRawOffset();
            Intent intent = new Intent(AddRemindActivity.this, RemindReceiver.class);
            intent.putExtra("type", (int)remindTime);
            startAlarmRemind(intent, remindTime, intervalTime, (int) remindTime);
        }
    }

    public void startAlarmRemind(Intent intent, long startTime, long intervalTime, int type) {
        PendingIntent sender = PendingIntent.getBroadcast(this, type, intent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, sender);
    }

    public void cancelRemind(RemindEntity entity) {
        Date startDateTemp = TransformDateUitls.getDate(Long.parseLong(entity.getStartDate()));
        String startDateStr = TransformDateUitls.getLong(startDateTemp);
        long startDateLong = TransformDateUitls.getDate(startDateStr);

        setCancelRemindTime(startDateLong, entity.getFirstTime());
        setCancelRemindTime(startDateLong, entity.getSecondtime());
        setCancelRemindTime(startDateLong, entity.getThreeTime());
        setCancelRemindTime(startDateLong, entity.getFourTime());
        setCancelRemindTime(startDateLong, entity.getFiveTime());
    }

    public void setCancelRemindTime(long startDateLong, String timeStr) {
        Calendar currentDate = Calendar.getInstance();
        if (!timeStr.equals("-1")) {
            long time = TransformDateUitls.getTimeLong(timeStr);
            long remindTime = (startDateLong + time) + currentDate.getTimeZone().getRawOffset();
            Intent intent = new Intent(AddRemindActivity.this, RemindReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, (int)remindTime, intent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            manager.cancel(sender);
        }
    }

    //设置标题中的内容
    public void setTimesHint() {
        timesHint.setText("每" + day + "天服用" + oldTimes + "次");
    }

    private void initData() {
        data = new ArrayList<>();
        data.add("我");
        timesData = new ArrayList<>();
        timesDataTemp = new ArrayList<>();
        timesDataTemp.add("08:30");
        for (int i = 0; i < 5; i++) {
            timesData.add("-1");
        }
    }

    private void writeDb(RemindEntity entity) {
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        remindDao.insert(entity);
    }

    private void updateDb(RemindEntity entity) {
        RemindDao deleteDao = DBInterface.instance().openReadableDb().getRemindDao();
        deleteDao.delete(entity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_remind_chooseuser:
                ChooseUserPopupWindow popupWindow = new ChooseUserPopupWindow(this, data);
                popupWindow.show(chooseUser);
                break;
            case R.id.add_remind_choosedrug:
                chooseUserDialog();
                break;
            case R.id.day_layout:
                showCountDialog();
                break;
            case R.id.count_layout:
                new AddRemindTimesDailog(this, timesDataTemp, oldTimes).show();
                break;
            case R.id.remind_layout:
                break;
            case R.id.start_date_layout:
                showDatePickerDialog();
                startDateLayout.setClickable(false);
                break;
        }
    }

    //选择用户的dialog
    private void chooseUserDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_search_drug, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddRemindActivity.this, SearchActivity.class));
            }
        });
        simpleDialog(view);
    }

    //选择间隔几天提示dialog
    public void showCountDialog() {
        oldDay = day;
        final View dayview = LayoutInflater.from(this).inflate(R.layout.dialog_day_count, null);
        final Dialog dialog = simpleDialog(dayview);
        RadioGroup group = (RadioGroup) dayview.findViewById(R.id.dialog_day_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton) dayview.findViewById(checkedId);
                String str = (String) button.getHint();
                day = Integer.parseInt(str);
                Toast.makeText(AddRemindActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });

        dayview.findViewById(R.id.dialog_day_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = oldDay;
                dialog.dismiss();
            }
        });

        dayview.findViewById(R.id.dialog_day_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day != 1) {
                    days.setText("每" + day + "天");
                } else {
                    days.setText("每天");
                }
                setTimesHint();
                dialog.dismiss();
            }
        });
    }

    public static final String DATEPICKER_TAG = "datepicker";
    private String dateStr;

    //选择日期的dialog
    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        datePickerDialog.setVibrate(true);
        datePickerDialog.setYearRange(1985, 2028);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
        datePickerDialog.setOnDateSetListener(this);
    }

    @Override
    public void setEditUserText(String str) {
        editUser.setText(str);
    }

    @Override
    public void setCountsText(int oldTimes) {
        this.oldTimes = oldTimes;
        counts.setText(oldTimes + "次");
    }

    @Override
    public void setTimesHintText(int oldTimes) {
        this.oldTimes = oldTimes;
        setTimesHint();
    }

    @Override
    public void setTimesDate(List<String> timesData) {
        this.timesDataTemp = timesData;
    }

    @Override//选择完日期调用的方法
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        startRemindYear = year;
        startRemindMouth = month + 1;
        startRemindDay = day;
        dateStr = TransformDateUitls.getYearDate(Long.parseLong(getStartDate()));
        startDateView.setText(dateStr);
        startDateLayout.setClickable(true);
    }

    public Dialog simpleDialog(View view) {
        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        return dialog;
    }
}
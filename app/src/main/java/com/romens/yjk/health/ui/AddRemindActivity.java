package com.romens.yjk.health.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.library.datetimepicker.date.DatePickerDialog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.adapter.BaseFragmentAdapter;
import com.romens.android.ui.cells.HeaderCell;
import com.romens.android.ui.cells.TextCheckCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.cells.AddRemindTimesDailog;
import com.romens.yjk.health.ui.cells.AvatarEditTextCell;
import com.romens.yjk.health.ui.components.RemindReceiver;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by anlc on 2015/9/10.
 * 添加新的提醒页面
 */
public class AddRemindActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private ListView listView;
    private ListAdapter adapter;

    private AvatarEditTextCell editUsercell;
    private AvatarEditTextCell editDrugCell;

    private int rowCount;
    private int titleHintRow;
    private int timesHintRow;
    private int intervalDayRow;
    private int chooseTimeRow;
    private int chooseDateRow;
    private int isRemindRow;
    private int deleteBtnRow;

    private List<String> timesData;
    private List<String> timesDataTemp;
    private int oldTimes = 1;

    private int intervalDay;
    private int timesInDay;
    private String chooseDate;
    private int isRemind;

    private boolean isFromDetail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        container.addView(getChooseUserView("输入用药者姓名", false), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        container.addView(getChooseDrugView("输入药品名称", true), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        setContentView(container);
        actionBarEven(actionBar);
        initData();
        setRaws();
        isRemindDetailFrom();

        adapter = new ListAdapter(this);
        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private void initData() {
        timesData = new ArrayList<>();
        timesDataTemp = new ArrayList<>();
        timesDataTemp.add("08:30");
        for (int i = 0; i < 5; i++) {
            timesData.add("-1");
        }

        chooseDate = getCurrentDate();
        timesInDay = 1;
        intervalDay = 1;
    }

    private RemindEntity fromDetailEntity;

    private void isRemindDetailFrom() {
        Intent intent = getIntent();
        fromDetailEntity = (RemindEntity) intent.getSerializableExtra("editEntity");
        if (fromDetailEntity != null) {
            viewSetData(fromDetailEntity);
            isFromDetail = true;
        }
    }

    private void viewSetData(final RemindEntity entity) {
        deleteBtnRow = rowCount++;
//        deleteBtn = (Button) findViewById(R.id.add_remind_deleteBtn);
//        deleteBtn.setVisibility(View.VISIBLE);
        editUsercell.setValue(entity.getUser());
        editDrugCell.setValue(entity.getDrug());
        chooseDate = entity.getStartDate();
        intervalDay = entity.getIntervalDay();
        timesInDay = entity.getTimesInDay();
        isRemind = entity.getIsRemind();
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteDb(entity);
//                cancelRemind(entity);
//                Intent toRemindIntent = new Intent(AddRemindActivity.this, RemindActivity.class);
//                toRemindIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(toRemindIntent);
//            }
//        });
        timesData = new ArrayList<>();
        timesData.add(entity.getFirstTime());
        timesDateAdd(entity.getSecondtime());
        timesDateAdd(entity.getThreeTime());
        timesDateAdd(entity.getFourTime());
        timesDateAdd(entity.getFiveTime());
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

    public void itemClick(int position) {
        if (position == intervalDayRow) {
            showIntervalDayDialog();
        } else if (position == chooseTimeRow) {
            new AddRemindTimesDailog(this, timesDataTemp, oldTimes).show();
        } else if (position == chooseDateRow) {
            showDatePickerDialog();
        } else if (position == isRemindRow) {
            TextCheckCell cell = (TextCheckCell) adapter.getItem(position);
            if (isRemind == 0) {
                isRemind = 1;
                cell.setChecked(true);
            } else if (isRemind == 1) {
                isRemind = 0;
                cell.setChecked(false);
            }
            adapter.notifyDataSetChanged();
//        } else if (position == deleteBtnRow) {
//            View view = (View) adapter.getItem(position);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (fromDetailEntity != null) {
//                        deleteDb(fromDetailEntity);
//                        cancelRemind(fromDetailEntity);
//                        Intent toRemindIntent = new Intent(AddRemindActivity.this, RemindActivity.class);
//                        toRemindIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(toRemindIntent);
//                    }
//                }
//            });
//            adapter.notifyDataSetChanged();
        }
    }

    public void cancelRemind(RemindEntity entity) {
        long startDateLong = TransformDateUitls.getDate(entity.getStartDate());
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
            PendingIntent sender = PendingIntent.getBroadcast(this, (int) remindTime, intent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            manager.cancel(sender);
        }
    }

    public static final String DATEPICKER_TAG = "datepicker";

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

    public void showIntervalDayDialog() {
        String items[] = new String[]{"每天", "每2天", "每3天", "每4天", "每5天", "每6天"};
        new AlertDialog.Builder(this).setTitle("选择间隔天数").setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalDay = which + 1;
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void setRaws() {
        rowCount = 0;
        titleHintRow = rowCount++;
        timesHintRow = rowCount++;
        intervalDayRow = rowCount++;
        chooseTimeRow = rowCount++;
        chooseDateRow = rowCount++;
        isRemindRow = rowCount++;
    }

    private void actionBarEven(ActionBar actionBar) {
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
                    String userStr = editUsercell.getValue();
                    String drugStr = editDrugCell.getValue();
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
        entity.setUser(editUsercell.getValue());
        entity.setDrug(editDrugCell.getValue());
        entity.setIntervalDay(intervalDay);
        entity.setStartDate(chooseDate);
        int times = timesData.size();
        entity.setFirstTime(timesData.get(0));
        entity.setSecondtime(times > 1 ? timesData.get(1) : "-1");
        entity.setThreeTime(times > 2 ? timesData.get(2) : "-1");
        entity.setFourTime(times > 3 ? timesData.get(3) : "-1");
        entity.setFiveTime(times > 4 ? timesData.get(4) : "-1");
        entity.setTimesInDay(timesInDay);
        entity.setIsRemind(isRemind);
        if (isRemind == 1) {
            setRemind(entity);
        }
        if (isFromDetail) {
            updateDb(entity);
            Intent toRemindIntent = new Intent(AddRemindActivity.this, RemindActivity.class);
            toRemindIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(toRemindIntent);
        } else {
            writeDb(entity);
            finish();
        }
    }

    private void writeDb(RemindEntity entity) {
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        remindDao.insert(entity);
    }

    private void updateDb(RemindEntity entity) {
        RemindDao deleteDao = DBInterface.instance().openReadableDb().getRemindDao();
        deleteDao.update(entity);
    }

    private void deleteDb(RemindEntity entity) {
        RemindDao deleteDao = DBInterface.instance().openReadableDb().getRemindDao();
        deleteDao.delete(entity);
    }

    //添加提醒到系统中
    public void setRemind(RemindEntity entity) {
        long startDateLong = TransformDateUitls.getDate(entity.getStartDate());

        setRemindTime(startDateLong, entity.getFirstTime(), entity);
        setRemindTime(startDateLong, entity.getSecondtime(), entity);
        setRemindTime(startDateLong, entity.getThreeTime(), entity);
        setRemindTime(startDateLong, entity.getFourTime(), entity);
        setRemindTime(startDateLong, entity.getFiveTime(), entity);
    }

    public void setRemindTime(long startDateLong, String timeStr, RemindEntity entity) {
        Calendar currentDate = Calendar.getInstance();
        long intervalTime = 1000 * 60 * 60 * 24 * intervalDay;
        if (!timeStr.equals("-1")) {
            long time = TransformDateUitls.getTimeLong(timeStr);
            long remindTime = (startDateLong + time) + currentDate.getTimeZone().getRawOffset();
            Intent intent = new Intent(AddRemindActivity.this, RemindReceiver.class);
            intent.putExtra("type", (int) remindTime);
            intent.putExtra("remindInfoEntity", entity);
            startAlarmRemind(intent, remindTime, intervalTime, (int) remindTime);
        }
    }

    public void startAlarmRemind(Intent intent, long startTime, long intervalTime, int type) {
        PendingIntent sender = PendingIntent.getBroadcast(this, type, intent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, sender);
    }

    private AvatarEditTextCell getChooseUserView(String hintText, boolean needDivider) {
        editUsercell = new AvatarEditTextCell(this);
        editUsercell.setDivider(needDivider, AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        editUsercell.setActionImageResource(R.drawable.ic_add_grey600_24dp);
        editUsercell.setValueHint(hintText);
        editUsercell.setAvatarEditTextCellDelegate(new AvatarEditTextCell.AvatarEditTextCellDelegate() {
            @Override
            public void onValueChanged(String value) {

            }

            @Override
            public void onAction() {
                Intent intent = new Intent(AddRemindActivity.this, ChooseUserActivity.class);
                startActivityForResult(intent, 11);
            }
        });
        return editUsercell;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        if (requestCode == 11) {
            editUsercell.setValue(intent.getStringExtra("userName"));
        }
    }

    private AvatarEditTextCell getChooseDrugView(String hintText, boolean needDivider) {
        editDrugCell = new AvatarEditTextCell(this);
        editDrugCell.setDivider(needDivider, AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        editDrugCell.setActionImageResource(R.drawable.ic_search_grey600_24dp);
        editDrugCell.setValueHint(hintText);
        editDrugCell.setAvatarEditTextCellDelegate(new AvatarEditTextCell.AvatarEditTextCellDelegate() {
            @Override
            public void onValueChanged(String value) {

            }

            @Override
            public void onAction() {
                chooseUserDialog();
            }
        });
        return editDrugCell;
    }

    //选择用户的dialog
    private void chooseUserDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_search_drug, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddRemindActivity.this, SearchActivityNew.class));
            }
        });
        simpleDialog(view);
    }

    public Dialog simpleDialog(View view) {
        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        return dialog;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        chooseDate = year + "-" + (month + 1) + "-" + day;
        adapter.notifyDataSetChanged();
    }

    public String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(System.currentTimeMillis());
    }

    public void setTimesData(List<String> timesData) {
        this.timesDataTemp = timesData;
        adapter.notifyDataSetChanged();
    }

    public void setTimesInDay(int timesInDay) {
        this.timesInDay = timesInDay;
        adapter.notifyDataSetChanged();
    }

    class ListAdapter extends BaseFragmentAdapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == titleHintRow || position == timesHintRow) {
                return 0;
            } else if (position == intervalDayRow || position == chooseTimeRow || position == chooseDateRow) {
                return 1;
            } else if (position == deleteBtnRow) {
                return 3;
            }
            return 2;
        }

        @Override
        public Object getItem(int i) {
            return getView(i, null, null);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new HeaderCell(context);
                }
                HeaderCell cell = (HeaderCell) view;
                if (i == titleHintRow) {
                    cell.setTextColor(ResourcesConfig.primaryColor);
                    cell.setText("服药周期");
                } else if (i == timesHintRow) {
                    cell.setTextColor(ResourcesConfig.primaryColor);
                    cell.setText("每" + intervalDay + "天服用" + timesInDay + "次");
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextSettingsCell(context);
                }
                TextSettingsCell cell = (TextSettingsCell) view;
                if (i == intervalDayRow) {
                    cell.setTextAndValue("重复", intervalDay == 1 ? "每天" : "每" + intervalDay + "天", false);
                } else if (i == chooseTimeRow) {
                    cell.setTextAndValue("次数", timesInDay + "次", false);
                } else if (i == chooseDateRow) {
                    cell.setTextAndValue("开始时间", chooseDate, true);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextCheckCell(context);
                }
                final TextCheckCell cell = (TextCheckCell) view;
                if (isRemind == 0) {
                    cell.setTextAndCheck("是否默认地址", false, true);
                } else if (isRemind == 1) {
                    cell.setTextAndCheck("是否默认地址", true, true);
                }
            } else if (type == 3) {
                TextView deleteBtn = new TextView(context);
                deleteBtn.setTextColor(getResources().getColor(R.color.theme_primary));
                deleteBtn.setText("删  除");
                deleteBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                deleteBtn.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20), AndroidUtilities.dp(20));
                view = deleteBtn;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fromDetailEntity != null) {
                            deleteDb(fromDetailEntity);
                            cancelRemind(fromDetailEntity);
                            Intent toRemindIntent = new Intent(AddRemindActivity.this, RemindActivity.class);
                            toRemindIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(toRemindIntent);
                        }
                    }
                });
            }
            return view;
        }
    }
}

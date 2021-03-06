package com.romens.yjk.health.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.RemindTimesDailogCallBack;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.ActionCell;
import com.romens.yjk.health.ui.cells.KeyAndImgCell;
import com.romens.yjk.health.ui.cells.KeyAndViewCell;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anlc on 2015/11/10.
 */
public class AddNewRemindActivity extends DarkActionBarActivity implements RemindTimesDailogCallBack {

    private ListView listView;
    private List<String> timesDataTemp;
    private List<String> timesData;
    private AddRemindAdapter adapter;

    private String user = "点击选择用户";
    private String drug = "点击选择药品";
    private String dayTimersTxt = "每天";
    private String remark = "请输入备注";
    private String dosage = "编辑";

    private int intervalDay;
    private String chooseDate;
    private int isRemind = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        actionBarEvent();
        initData();

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelector(R.drawable.list_selector);
        adapter = new AddRemindAdapter(this, timesDataTemp);
        listView.setAdapter(adapter);
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 1f));
        ActionCell savebtn = new ActionCell(AddNewRemindActivity.this) ;
        savebtn.setValue("保存");
        savebtn.setClickable(true);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.equals("点击选择用户") || user.equals("")) {
                    Toast.makeText(AddNewRemindActivity.this, "请选择用户", Toast.LENGTH_SHORT).show();
                } /*else if (drug.equals("点击选择药品") || drug.equals("")) {
                        Toast.makeText(AddNewRemindActivity.this, "请选择药品", Toast.LENGTH_SHORT).show();
                    }*/ else {
                    saveAddFinish();
                }
            }
        });

        container.addView(savebtn);
    }

    private void initData() {
        timesData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            timesData.add("-1");
        }
        timesDataTemp = new ArrayList<>();
        timesDataTemp.add("08:30");
        setRow();

        intervalDay = 1;
        chooseDate = TransformDateUitls.getYearDate(new Date().getTime());
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("用药提醒");
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
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
        entity.setUser(user);
        entity.setDrug(drug);
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
        entity.setRemark(remark);
        entity.setDosage(dosage);
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
        RemindDao remindDao = DBInterface.instance().openWritableDb().getRemindDao();
        entity.setIsAdd(true);
        remindDao.insert(entity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserGuidConfig.RESPONSE_MEMBER_TO_REMIND) {
            user = data.getStringExtra("remindUser");
        } else if (resultCode == UserGuidConfig.RESPONSE_DRUGGROUP_TO_REMIND) {
            drug = data.getStringExtra("drugGroup_drug");
        } else if (resultCode == UserGuidConfig.RESPONSE_REMIND_TIMES_TO_NEW_REMIND) {
            this.timesDataTemp = data.getStringArrayListExtra("resultTimesDataList");
            setRow();
            adapter.setTimesData(timesDataTemp);
        }
        adapter.notifyDataSetChanged();
    }

    private int rowCount;
    private int drugRow;
    private int dosageRow;
    private int remindTimeRow;
    private int timersRow;
    private int remarkRow;
    private int userRow;

    public void setRow() {
        rowCount = 0;
        userRow = rowCount++;
        drugRow = rowCount++;
        dosageRow = rowCount++;
        remindTimeRow = rowCount++;
        for (int i = 1; i < timesDataTemp.size(); i++) {
            rowCount++;
        }
        rowCount++;
        timersRow = rowCount++;
        remarkRow = -1;
    }

    @Override
    public void setTimesData(List<String> timesData) {
        this.timesDataTemp = timesData;
        setRow();
        adapter.setTimesData(timesData);
    }

    @Override
    protected String getActivityName() {
        return "新增用药提醒";
    }

    class AddRemindAdapter extends BaseAdapter {

        private Context context;
        private List<String> timesData;

        public void setTimesData(List<String> timesData) {
            this.timesData = timesData;
            notifyDataSetChanged();
        }

        public AddRemindAdapter(Context context, List<String> timesData) {
            this.context = context;
            this.timesData = timesData;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return timesData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == remindTimeRow) {
                return 1;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (convertView == null) {
                    convertView = new KeyAndViewCell(context);
                }
                KeyAndViewCell cell = (KeyAndViewCell) convertView;
                if (position == userRow) {
                    cell.setKeyAndLeftText("称呼", user, true);
                    cell.setOnTextViewClickListener(new KeyAndViewCell.OnTextViewClickListener() {
                        @Override
                        public void textViewClick() {
                            UIOpenHelper.openFamilyMemberActivity((Activity) context);
                        }
                    });
                } else if (position == drugRow) {
                    cell.setKeyAndLeftText("药品", drug, true);
                    cell.setOnTextViewClickListener(new KeyAndViewCell.OnTextViewClickListener() {
                        @Override
                        public void textViewClick() {
                            UIOpenHelper.openFamilyDrugGroupActivity((Activity) context);
                        }
                    });
                } else if (position == dosageRow) {
                    cell.setKeyAndRightText("剂量", dosage, true);
                    cell.setOnTextViewClickListener(new KeyAndViewCell.OnTextViewClickListener() {
                        @Override
                        public void textViewClick() {
                            showChooseDosageDialog();
                        }
                    });
                } else if (position == timersRow) {
                    cell.setKeyAndRightText("重复", dayTimersTxt, true);
                    cell.setOnTextViewClickListener(new KeyAndViewCell.OnTextViewClickListener() {
                        @Override
                        public void textViewClick() {
                            showIntervalDayDialog();
                        }
                    });
                } else if (position == remarkRow) {
                    cell.setKeyAndEditHint("备注", remark, true);
                    cell.setOnEditViewChangeListener(new KeyAndViewCell.OnEditViewChangeListener() {
                        @Override
                        public void editViewChange(String editResult) {
                            remark = editResult;
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    int index = position - remindTimeRow - 1;
                    cell.setKeyAndRightText("提醒时间", timesData.get(index), true);
                    cell.setOnTextViewClickListener(null);
                }
            } else if (type == 1) {
                if (convertView == null) {
                    convertView = new KeyAndImgCell(context);
                }
                KeyAndImgCell cell = (KeyAndImgCell) convertView;
                cell.setBackgroundColor(Color.WHITE);
                cell.setInfo("提醒时间", R.drawable.remind_time_add, true);
                cell.setKeyColor(getResources().getColor(R.color.theme_primary));
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIOpenHelper.openAddRemindTimesActivity((Activity) context, (ArrayList<String>) timesData);
                    }
                });
            }
            return convertView;
        }
    }

    public void showIntervalDayDialog() {
        String items[] = new String[]{"每天", "每2天", "每3天", "每4天", "每5天", "每6天"};
        new AlertDialog.Builder(this).setTitle("选择间隔天数").setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalDay = which + 1;
                dayTimersTxt = intervalDay == 1 ? "每天" : "每" + intervalDay + "天";
                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private TextView dosageTextView;

    public void showChooseDosageDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_dosage, null);
        dosageTextView = (TextView) view.findViewById(R.id.remind_dosage);
        dosageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosDosageView(dosageTextView);
            }
        });
        final MaterialEditText editText = (MaterialEditText) view.findViewById(R.id.choose_dosage_edit);
        editText.setFocusable(true);
        editText.setEnabled(true);
        final TextView textView = (TextView) view.findViewById(R.id.choose_dosage_confirm);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dosage = editText.getText().toString() + " " + dosageTextView.getText().toString();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
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
        FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(AndroidUtilities.dp(60), LayoutHelper.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layout.addView(listView, layoutParams);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.popupwindow_list_itme, listData));

        final PopupWindow popupWindow = new PopupWindow(layout, AndroidUtilities.dp(62), LayoutHelper.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.bg_white));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dosageTextView.setText(listData.get(position));
                popupWindow.dismiss();
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(view);
    }
}

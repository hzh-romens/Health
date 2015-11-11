package com.romens.yjk.health.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.library.datetimepicker.date.DatePickerDialog;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.FamilyMemberDao;
import com.romens.yjk.health.db.entity.FamilyMemberEntity;
import com.romens.yjk.health.ui.cells.KeyAndViewCell;

import java.util.Calendar;

/**
 * Created by anlc on 2015/11/9.
 */
public class FamilyMemberAddActivity extends BaseActivity {

    private ListView listView;

    private String name;
    private String sex = "男";
    private String birthday;
    private String age = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        container.setBackgroundResource(R.color.line_color);
        setContentView(container, actionBar);
        actionBarEvent();
        setRow();

        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setSelection(R.drawable.list_selector);
        listView.setAdapter(new MemberAddAdapter(this));
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("新增成员");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(0, R.drawable.checkbig);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    if (name == null || name.equals("")) {
                        Toast.makeText(FamilyMemberAddActivity.this, "请输入称呼", Toast.LENGTH_SHORT).show();
                    } else if (birthday == null || birthday.equals("")) {
                        Toast.makeText(FamilyMemberAddActivity.this, "请输入生日", Toast.LENGTH_SHORT).show();
                    } else {
                        FamilyMemberEntity entity = new FamilyMemberEntity();
                        entity.setName(name);
                        entity.setBirthday(birthday);
                        entity.setAge(age);
                        entity.setSex(sex);
                        insertDb(entity);
                        finish();
                    }
                }
            }
        });
    }

    private void insertDb(FamilyMemberEntity entity) {
        FamilyMemberDao dao = DBInterface.instance().openWritableDb().getFamilyMemberDao();
        dao.insert(entity);
    }

    private int rowCount;
    private int nameRow;
    private int sexRow;
    private int birthdayRow;
    private int ageRow;

    public void setRow() {
        nameRow = rowCount++;
        sexRow = rowCount++;
        birthdayRow = rowCount++;
        ageRow = rowCount++;
    }

    class MemberAddAdapter extends BaseAdapter implements DatePickerDialog.OnDateSetListener {

        private Context context;
        private String textViewHint = "点击选择";

        public MemberAddAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new KeyAndViewCell(context);
            }
            final KeyAndViewCell cell = (KeyAndViewCell) convertView;
            cell.setBackgroundColor(Color.WHITE);
            if (position == nameRow) {
                cell.setKeyAndEditHint("称呼", "输入姓名或称呼", true);
                cell.setOnEditViewChangeListener(new KeyAndViewCell.OnEditViewChangeListener() {
                    @Override
                    public void editViewChange(String editResult) {
                        name = editResult;
                    }
                });
            } else if (position == sexRow) {
                cell.setKeyAndRadio("性别", 0, true);
                cell.setOnRadioViewClickListener(new KeyAndViewCell.OnRadioViewClickListener() {
                    @Override
                    public void radioSelect(String result) {
                        sex = result;
                    }
                });
            } else if (position == birthdayRow) {
                cell.setKeyAndRightText("生日", textViewHint, true);
                cell.setOnTextViewClickListener(new KeyAndViewCell.OnTextViewClickListener() {
                    @Override
                    public void textViewClick() {
                        showDatePickerDialog();
                    }
                });
            } else if (position == ageRow) {
                cell.setKeyAndEditHint("年龄", "0", true);
                cell.setOnEditViewChangeListener(new KeyAndViewCell.OnEditViewChangeListener() {
                    @Override
                    public void editViewChange(String editResult) {
                        age = editResult;
                    }
                });
            }
            return convertView;
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

        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
            birthday = year + "年" + (month + 1) + "月" + day + "日出生";
            textViewHint = birthday.substring(0, birthday.length() - 2);
            notifyDataSetChanged();
        }
    }
}

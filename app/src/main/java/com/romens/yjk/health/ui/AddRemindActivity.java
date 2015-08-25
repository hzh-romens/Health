package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.RemindEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by anlc on 2015/8/21.
 * 添加提醒页面
 */
public class AddRemindActivity extends BaseActivity implements View.OnClickListener {

    private TextView chooseUser;
    private TextView chooseDrug;
    private RelativeLayout dayLayout;
    private RelativeLayout countLayout;
    private RelativeLayout remindLayout;
    private RelativeLayout startDateLayout;
    private TextView timesHint;
    private TextView days;
    private TextView counts;
    private TextView startDate;
    private EditText editUser;
    private EditText editDrug;
    private Switch remindFlag;
    private List<String> data;
    private int day = 1;
    private int oldDay = 1;
    private int oldTimes = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind, R.id.add_remind_actionbar);
        ActionBar actionBar = getMyActionBar();

        initData();

        chooseUser = (TextView) findViewById(R.id.add_remind_chooseuser);
        chooseDrug = (TextView) findViewById(R.id.add_remind_choosedrug);
        editUser = (EditText) findViewById(R.id.add_remind_edituser);
        editDrug = (EditText) findViewById(R.id.add_remind_editdrug);
        days = (TextView) findViewById(R.id.add_remind_days);
        counts = (TextView) findViewById(R.id.add_remind_counts);
        timesHint = (TextView) findViewById(R.id.add_remind_timeshint);
        startDate = (TextView) findViewById(R.id.add_remind_startdate);
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
                if(isChecked){
                    Toast.makeText(AddRemindActivity.this,"-->"+isChecked,Toast.LENGTH_SHORT).show();
                    setRemind();
                }
            }
        });

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
                    if (editUser.getText().toString().trim().equals("") && editUser.getText() == null) {
                        Toast.makeText(AddRemindActivity.this, "请输入服药者", Toast.LENGTH_SHORT).show();
                    } else if (editDrug.getText().toString().trim().equals("") && editDrug.getText() == null) {
                        Toast.makeText(AddRemindActivity.this, "请输入药物", Toast.LENGTH_SHORT).show();
                    } else {
                        RemindEntity entity = new RemindEntity();
                        entity.setUserIcon(R.drawable.person_image_empty);
                        entity.setUser(editUser.getText().toString());
                        entity.setDrug(editDrug.getText().toString());
                        entity.setCount(day + "天" + oldTimes + "次");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("entity", entity);
                        Intent intent = new Intent(AddRemindActivity.this, RemindActivity.class);
                        intent.putExtra("bundle", bundle);
                        setResult(0, intent);
                        finish();
                    }
                }
            }
        });
    }

    public void setRemind(){

    }

    public void setTimesHint() {
        timesHint.setText("每" + day + "天服用" + oldTimes + "次");
    }

    private void initData() {
        data = new ArrayList<>();
        data.add("其他人");
        data.add("我");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_remind_chooseuser:
                MyPopupWindow popupWindow = new MyPopupWindow(this, data);
                popupWindow.show(chooseUser);
                break;
            case R.id.add_remind_choosedrug:
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_search_drug, null);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AddRemindActivity.this, SearchActivity.class));
                    }
                });
                simpleDialog(view);
                break;
            case R.id.day_layout:
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
                break;
            case R.id.count_layout:
                timesDialog();
                break;
            case R.id.remind_layout:
                break;
            case R.id.start_date_layout:
                SimpleCalendarDialogFragment fragment = new SimpleCalendarDialogFragment();
                fragment.show(getSupportFragmentManager(), "calendar_dialog");
                break;
        }
    }
    private Date startDateTime;
    class SimpleCalendarDialogFragment extends DialogFragment implements OnDateChangedListener {
        DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
        private Button finish;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_calendar, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);
            widget.setOnDateChangedListener(this);
            finish = (Button) findViewById(R.id.calendar_finish);
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDate.setText(startDateTime+"");
                    dismiss();
                }
            });
        }

        @Override
        public void onDateChanged(@NonNull MaterialCalendarView widget, CalendarDay date) {

            startDateTime=date.getDate();
        }
    }

    public Dialog simpleDialog(View view) {
        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        return dialog;
    }

    public void timesDialog() {
        //设置1天的提醒次数的dialog
        final List<String> timesData = new ArrayList<>();
        timesData.add("08:30");

        final Dialog timesCountDialog = new AlertDialog.Builder(this).create();
        View timesCountView = LayoutInflater.from(this).inflate(R.layout.dialog_times_count, null);
        ListView timesList = (ListView) timesCountView.findViewById(R.id.times_count_list);
        final TimesAdapter adapter = new TimesAdapter(timesData, this);
        timesList.setAdapter(adapter);

        ImageView minusBtn = (ImageView) timesCountView.findViewById(R.id.times_count_minus);
        ImageView addBtn = (ImageView) timesCountView.findViewById(R.id.times_count_add);
        TextView cancel = (TextView) timesCountView.findViewById(R.id.times_count_cancel);
        TextView confirm = (TextView) timesCountView.findViewById(R.id.times_count_confirm);
        final TextView times = (TextView) timesCountView.findViewById(R.id.times_count_count);
        oldTimes = timesData.size();
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timesData.size() > 0) {
                    timesData.remove(timesData.size());
                    times.setText(timesData.size() + "次");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesData.add("08:30");
                times.setText(timesData.size() + "次");
                adapter.notifyDataSetChanged();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counts.setText(oldTimes + "次");
                timesCountDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldTimes = timesData.size();
                counts.setText(oldTimes + "次");
                setTimesHint();
                timesCountDialog.dismiss();
                Toast.makeText(AddRemindActivity.this, "click_ok", Toast.LENGTH_SHORT).show();
            }
        });

        timesCountDialog.show();
        timesCountDialog.getWindow().setContentView(timesCountView);
    }

    class TimesAdapter extends BaseAdapter {
        //设置1天的提醒次数的适配器
        private List<String> data;
        private Context context;

        public TimesAdapter(List<String> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TimesViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_text, null);
                holder = new TimesViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.list_item_text);
                convertView.setTag(holder);
            } else {
                holder = (TimesViewHolder) convertView.getTag();
            }
            holder.textView.setText(data.get(position));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }
    }

    class TimesViewHolder {
        private TextView textView;
    }

    class MyPopupWindow {
        //选择提醒用户的弹窗
        private Context context;
        private ListView listView;
        private LinearLayout layout;
        private List<String> data;
        private PopupAdapter adapter;
        private PopupWindow popupWindow;

        public MyPopupWindow(Context context, final List<String> data) {
            this.context = context;
            this.data = data;
            listView = new ListView(context);
            listView.setDividerHeight(0);
            listView.setDivider(null);
            listView.setScrollbarFadingEnabled(false);
            layout = new LinearLayout(context);
            layout.addView(listView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
            adapter = new PopupAdapter();
            listView.setAdapter(adapter);

            popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int width = layout.getMeasuredWidth();
            popupWindow.setWidth(width);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editUser.setText(data.get(position));
                    popupWindow.dismiss();
                }
            });
        }

        public void show(View view) {
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.drug_type_child));
            popupWindow.showAsDropDown(view);
        }

        class PopupAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout linearLayout = new LinearLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(context);
                textView.setText(data.get(position));
                textView.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
                textView.setTextSize(AndroidUtilities.dp(14));
                textView.setLayoutParams(params);
                textView.setGravity(Gravity.CENTER);
                linearLayout.addView(textView);
                return linearLayout;
            }
        }
    }
}
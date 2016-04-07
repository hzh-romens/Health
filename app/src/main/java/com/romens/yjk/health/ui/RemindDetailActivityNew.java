package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.RemindUtils;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.cells.ImgAndValueCell;
import com.romens.yjk.health.ui.cells.RemindItemCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/10/30.
 */
public class RemindDetailActivityNew extends BaseActionBarActivityWithAnalytics {

    private ListView listView;
    private DetailListViewAdapter adapter;

    private int rowCount;
    private int drugRow;
    private int countRow;
    private int timesRow;
    private int remarkRow;
    private String dosage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarLayout.LinearLayoutContainer container = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        container.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(container, actionBar);
        actionBarEvent();
        getData();
        setRow();
        listView = new ListView(this);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        adapter = new DetailListViewAdapter(this);
        listView.setAdapter(adapter);
        container.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private RemindEntity detailEntity;

    public void getData() {
        Intent intent = getIntent();
//        Bundle bundle = intent.getBundleExtra("detailEntity");
//        detailEntity = (RemindEntity) bundle.getSerializable("detailBundle");
        detailEntity = (RemindEntity) intent.getSerializableExtra("detailEntity");
    }

    public void setRow() {
        drugRow = rowCount++;
        countRow = rowCount++;
        timesRow = rowCount++;
//        remarkRow = rowCount++;
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("用药提醒");
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setBackgroundResource(R.color.theme_primary);
//        ActionBarMenu actionBarMenu = actionBar.createMenu();
//        actionBarMenu.addItem(0, R.drawable.checkbig);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                }
            }
        });
    }

    class DetailListViewAdapter extends BaseAdapter {

        private Context context;

        public DetailListViewAdapter(Context context) {
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
        public int getItemViewType(int position) {
            if (position == drugRow) {
                return 1;
            } else if(position == countRow){
                return 2;
            } else{
                return 3;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 1) {
                if (convertView == null) {
                    convertView = new RemindItemCell(context);
                }
                final RemindItemCell cell = (RemindItemCell) convertView;
                cell.setData(R.drawable.remind_drug, detailEntity.getDrug(), true);
                if (detailEntity.getIsRemind() == 0) {
                    cell.setCheck(false);
                } else {
                    cell.setCheck(true);
                }
                cell.setOnSwitchClickLinstener(new RemindItemCell.onSwitchClickLinstener() {
                    @Override
                    public void onSwitchClick() {
                        if (detailEntity.getIsRemind() == 0) {
                            cell.setCheck(true);
                            detailEntity.setIsRemind(1);
                            RemindUtils.setRemind(detailEntity, context);
                        } else {
                            cell.setCheck(false);
                            detailEntity.setIsRemind(0);
                            RemindUtils.cancelRemind(detailEntity, context);
                        }
                        DBInterface.instance().openWritableDb().getRemindDao().insertOrReplace(detailEntity);
                    }
                });
            } else if (type == 2) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_remind_measure, null);
                }
                final TextView name = (TextView) convertView.findViewById(R.id.remind_measure_text);
                name.setText(detailEntity.getDosage());
                TextView edit = (TextView) convertView.findViewById(R.id.remind_measure_edit);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChooseDosageDialog(detailEntity);
                    }
                });
            }else if(type == 3){
                if (convertView == null) {
                    convertView = new ImgAndValueCell(context);
                }
                ImgAndValueCell cell = (ImgAndValueCell) convertView;
                if (position == timesRow) {
                    String timeStr = "";
                    timeStr += detailEntity.getFirstTime() + "  ";
                    if (!detailEntity.getSecondtime().equals("-1")) {
                        timeStr += detailEntity.getSecondtime() + "  ";
                    }
                    if (!detailEntity.getThreeTime().equals("-1")) {
                        timeStr += detailEntity.getThreeTime() + "  ";
                    }
                    if (!detailEntity.getFourTime().equals("-1")) {
                        timeStr += detailEntity.getFourTime() + "  ";
                    }
                    if (!detailEntity.getFiveTime().equals("-1")) {
                        timeStr += detailEntity.getFiveTime();
                    }
                    cell.setData(R.drawable.remind_time, timeStr, true);
                } else if (position == remarkRow) {
                    cell.setData(R.drawable.remind_remark, detailEntity.getRemark(), true);
                }
            }
            return convertView;
        }

        private TextView dosageTextView;

        public void showChooseDosageDialog(final RemindEntity entity) {
            final AlertDialog dialog = new AlertDialog.Builder(context).create();
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_dosage, null);
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
                    entity.setDosage(dosage);
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

            FrameLayout layout = new FrameLayout(context);
            ListView listView = new ListView(context);
            listView.setDivider(null);
            listView.setDividerHeight(0);
            listView.setVerticalScrollBarEnabled(false);
            listView.setSelection(R.drawable.list_selector);
            FrameLayout.LayoutParams layoutParams = LayoutHelper.createFrame(AndroidUtilities.dp(60), LayoutHelper.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layout.addView(listView, layoutParams);
            listView.setAdapter(new ArrayAdapter<String>(context, R.layout.popupwindow_list_itme, listData));

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
}

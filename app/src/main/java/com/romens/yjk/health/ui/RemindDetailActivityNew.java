package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.cells.ImgAndValueCell;
import com.romens.yjk.health.ui.cells.RemindItemCell;

/**
 * Created by anlc on 2015/10/30.
 */
public class RemindDetailActivityNew extends BaseActivity {

    private ListView listView;
    private DetailListViewAdapter adapter;

    private int rowCount;
    private int drugRow;
    private int countRow;
    private int timesRow;
    private int remarkRow;

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
            } else {
                return 2;
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
                        } else {
                            cell.setCheck(false);
                        }
                    }
                });
            } else if (type == 2) {
                if (convertView == null) {
                    convertView = new ImgAndValueCell(context);
                }
                ImgAndValueCell cell = (ImgAndValueCell) convertView;
                if (position == countRow) {
                    cell.setData(R.drawable.remind_count, detailEntity.getDosage(), true);
                } else if (position == timesRow) {
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
    }
}

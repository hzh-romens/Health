package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/10/18.
 */
public class CollectDrawerCell extends LinearLayout {

    private ListView listView;
    private CellAdapter adapter;
    //    private FrameLayout listViewLayout;

    private int rowCount;
    private int totalRow;
    private int lineRow;
    private int haveRow;
    private int lineRow2;
    private int firstRow;
    private int secondRow;
    private int threeRow;
    private int fourRow;
    private int deleteRow;

    public onActionBarClickListener onActionBarClickListener;

    public void setOnActionBarClickListener(onActionBarClickListener onActionBarClickListener) {
        this.onActionBarClickListener = onActionBarClickListener;
    }

    public interface onActionBarClickListener {
        void onItemClick(int itemIndex);
    }

    public CollectDrawerCell(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setRow();
        ActionBar actionBar = new ActionBar(context);
        actionBarEvent(actionBar);
        addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setBackgroundResource(R.drawable.bg_light_gray);
        addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        adapter = new CellAdapter(context);
        listView.setAdapter(adapter);
    }

    private void actionBarEvent(ActionBar actionBar) {
        actionBar.setTitle("筛选");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.filter_right_img);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (onActionBarClickListener != null) {
                    if (i == -1) {
                        onActionBarClickListener.onItemClick(-1);
                    } else if (i == 0) {
                        onActionBarClickListener.onItemClick(0);
                    }
                }
            }
        });
    }

    public void setRow() {
        totalRow = rowCount++;
        lineRow = rowCount++;
        haveRow = rowCount++;
        lineRow2 = rowCount++;
        firstRow = rowCount++;
        secondRow = rowCount++;
        threeRow = rowCount++;
        fourRow = rowCount++;
        deleteRow = rowCount++;
    }

    class CellAdapter extends BaseAdapter {

        private Context context;

        public CellAdapter(Context context) {
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
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == totalRow) {
                return 0;
            } else if (position == lineRow || position == lineRow2) {
                return 1;
            } else if (position == haveRow) {
                return 2;
            } else if (position == firstRow || position == secondRow || position == threeRow || position == fourRow) {
                return 3;
            } else if (position == deleteRow) {
                return 4;
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {
                if (convertView == null) {
                    convertView = new TextSettingsCell(context);
                }
                TextSettingsCell cell = (TextSettingsCell) convertView;
                cell.setTextAndValue("全部", "2件", true);
                cell.setBackgroundColor(Color.WHITE);
            } else if (type == 1) {
                if (convertView == null) {
                    convertView = new ShadowSectionCell(context);
                }
            } else if (type == 2) {
                if (convertView == null) {
                    convertView = new TextSettingsCell(context);
                }
                TextSettingsCell cell = (TextSettingsCell) convertView;
                cell.setTextAndValue("全部", "测试", true);
                cell.setBackgroundColor(Color.WHITE);
            } else if (type == 3) {
                if (convertView == null) {
                    convertView = new TextSettingsCell(context);
                }
                TextSettingsCell cell = (TextSettingsCell) convertView;
                cell.setBackgroundColor(Color.WHITE);
                if (position == firstRow) {
                    cell.setTextAndValue("疏肝养胃", "1件", true);
                } else if (position == secondRow) {
                    cell.setTextAndValue("日常必备药", "1件", true);
                } else if (position == threeRow) {
                    cell.setTextAndValue("心血管疾病", "1件", true);
                } else if (position == fourRow) {
                    cell.setTextAndValue("感冒头疼", "1件", true);
                }
            } else if (type == 4) {
                if (convertView == null) {
                    convertView = new FrameLayout(context);
                }
                FrameLayout linearLayout = (FrameLayout) convertView;
                TextView textView = new TextView(context);
                textView.setText("清除已选");
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(R.drawable.bg_white);
                textView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.setMargins(AndroidUtilities.dp(0), AndroidUtilities.dp(32), AndroidUtilities.dp(0), AndroidUtilities.dp(0));
                textView.setLayoutParams(params);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                linearLayout.addView(textView);
            }
            return convertView;
        }
    }
}

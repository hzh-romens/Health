package com.romens.yjk.health.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;
import com.romens.yjk.health.ui.cells.RemindItemCell;
import com.romens.yjk.health.ui.components.RemindReceiver;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by anlc on 2015/8/21.
 * 用药提醒列表页面
 */
public class RemindActivity extends BaseActivity {

    private ActionBar actionBar;
    private RecyclerView listView;
    private List<RemindEntity> data;
    private RemindAdapter adapter;

    private ImageAndTextCell imgAndTxtCell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind, R.id.action_bar);
        data = new ArrayList<>();
        adapter = new RemindAdapter(data, this);
        actionBar = getMyActionBar();
        listView = (RecyclerView) findViewById(R.id.remind_list);
        listView.setAdapter(adapter);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.addItemDecoration(new RemindItemDecoration(AndroidUtilities.dp(10), AndroidUtilities.dp(20), data.size()));

        actionBar.setTitle("用药提醒");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(1, R.drawable.ic_add_white_24dp);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 1) {
                    UIOpenHelper.openAddNewRemindActivity(RemindActivity.this);
                }
            }
        });

        imgAndTxtCell = (ImageAndTextCell) findViewById(R.id.remind_no_item);
        imgAndTxtCell.setImageAndText(R.drawable.remind_no_item_bg, "您还没有添加提醒  点击右上角添加提醒");
        LinearLayout.LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        imgAndTxtCell.setPadding(AndroidUtilities.dp(0), AndroidUtilities.dp(100), AndroidUtilities.dp(0), AndroidUtilities.dp(0));
        imgAndTxtCell.setLayoutParams(layoutParams);

        initData();
    }

    public void refershContentView() {
        if (data != null && data.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            imgAndTxtCell.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            imgAndTxtCell.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    private void initData() {
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        data = remindDao.readDb(remindDao);
        refershContentView();
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    class RemindItemDecoration extends RecyclerView.ItemDecoration {
        private int horMargin;
        private int verMargin;
        private int last;

        public RemindItemDecoration(int horMargin, int verMargin, int last) {
            this.horMargin = horMargin;
            this.verMargin = verMargin;
            this.last = last;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = horMargin;
            outRect.right = horMargin;
            outRect.top = verMargin;
            outRect.bottom = 0;
            if (parent.getChildPosition(view) == last) {
                outRect.bottom = verMargin;
            }
        }
    }

    class RemindAdapter extends RecyclerView.Adapter<ADHolder> {

        private List<RemindEntity> data;
        private Context context;

        public void setData(List<RemindEntity> data) {
            this.data = data;
        }

        public RemindAdapter(List<RemindEntity> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RemindItemCell cell = new RemindItemCell(context);
            return new ADHolder(cell);
        }

        @Override
        public void onBindViewHolder(ADHolder viewHolder, final int position) {
            final RemindEntity entity = data.get(position);
            final RemindItemCell cell = (RemindItemCell) viewHolder.itemView;
            LinearLayout.LayoutParams layoutParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
            cell.setLayoutParams(layoutParams);
            cell.setData(R.drawable.remind_drug, entity.getDrug(), true);
            if (entity.getIsRemind() == 0) {
                cell.setCheck(false);
            } else {
                cell.setCheck(true);
            }
            cell.setOnSwitchClickLinstener(new RemindItemCell.onSwitchClickLinstener() {
                @Override
                public void onSwitchClick() {
                    if (entity.getIsRemind() == 0) {
                        cell.setCheck(true);
                        entity.setIsRemind(1);
                        setRemind(entity);
                    } else {
                        cell.setCheck(false);
                        entity.setIsRemind(0);
                        cancelRemind(entity);
                    }
                    DBInterface.instance().openWritableDb().getRemindDao().insertOrReplace(entity);
                }
            });
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RemindActivity.this, RemindDetailActivityNew.class);
                    intent.putExtra("detailEntity", entity);
                    startActivity(intent);
                }
            });
            cell.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeDialogView(entity);
                    return false;
                }
            });
        }

        public void removeDialogView(final RemindEntity entity) {
            final AlertDialog dialog = new AlertDialog.Builder(context).create();
            TextView textView = new TextView(context);

            textView.setBackgroundResource(R.drawable.bg_white);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER);
            textView.setText("删除");
            textView.setTextColor(getResources().getColor(R.color.theme_primary));
            textView.setPadding(AndroidUtilities.dp(32), AndroidUtilities.dp(8), AndroidUtilities.dp(32), AndroidUtilities.dp(8));
            LinearLayout.LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
            infoViewParams.weight = 1;
            infoViewParams.gravity = Gravity.CENTER;
            textView.setLayoutParams(infoViewParams);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    deleteDb(entity);
                    cancelRemind(entity);
                    initData();
                }
            });

            dialog.show();
            dialog.setContentView(textView);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private void deleteDb(RemindEntity entity) {
        RemindDao deleteDao = DBInterface.instance().openReadableDb().getRemindDao();
        deleteDao.delete(entity);
        initData();
        adapter.setData(data);
        adapter.notifyDataSetChanged();
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
        long intervalTime = 1000 * 60 * 60 * 24 * entity.getIntervalDay();
        if (!timeStr.equals("-1")) {
//            long time = TransformDateUitls.getTimeLong(timeStr);
//            long remindTime = (startDateLong+time) + currentDate.getTimeZone().getRawOffset();
            long startTime = TransformDateUitls.getTimeLong(timeStr) + startDateLong;
            long remindTime = startTime + currentDate.getTimeZone().getRawOffset();
            if (remindTime < currentDate.getTimeInMillis()) {
                remindTime += intervalTime;
            }
            Intent intent = new Intent(RemindActivity.this, RemindReceiver.class);
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
            Intent intent = new Intent(RemindActivity.this, RemindReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, (int) remindTime, intent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            manager.cancel(sender);
        }
    }
}

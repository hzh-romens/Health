package com.romens.yjk.health.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.BottomSheet;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.RemindUtils;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.ImageAndTextCell;
import com.romens.yjk.health.ui.cells.RemindItemCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/8/21.
 * 用药提醒列表页面
 */
public class RemindActivity extends DarkActionBarActivity {

    private ActionBar actionBar;
    private RecyclerView listView;
    private List<RemindEntity> data;
    private RemindAdapter adapter;

    private ImageAndTextCell imgAndTxtCell;

    private Dialog editDialog;

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
        listView.addItemDecoration(new RemindItemDecoration(AndroidUtilities.dp(8), AndroidUtilities.dp(8), data.size()));

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

    @Override
    public void onPause() {
        if (editDialog != null && editDialog.isShowing()) {
            editDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected String getActivityName() {
        return "用药提醒";
    }

    private void initData() {
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        data = remindDao.readDb(remindDao);
        refershContentView();
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    public void amendEntity(RemindEntity entity){
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        remindDao.insertOrReplace(entity) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (resultCode == UserGuidConfig.RESPONSE_REMIND_TIMES_TO_REMIND){
            int position = i.getIntExtra("position",-1) ;
            RemindEntity entity = (RemindEntity)i.getSerializableExtra("detailEntity");
            data.remove(position) ;
            data.add(position, entity);
            adapter.notifyDataSetChanged();
            amendEntity(entity);
        }
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
            if (viewType == 1) {
                TextInfoCell cell = new TextInfoCell(context);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new ADHolder(cell);
            } else {
                RemindView cell = new RemindView(context);
                cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                return new ADHolder(cell);
            }
        }

        @Override
        public void onBindViewHolder(ADHolder viewHolder, final int position) {
            int itemType = getItemViewType(position);
            if (itemType == 1) {
                TextInfoCell cell = (TextInfoCell) viewHolder.itemView;
                cell.setText("小提示:长按可以删除提醒哦");
            } else {
                final RemindEntity entity = data.get(position);
//                final RemindItemCell cell = (RemindItemCell) viewHolder.itemView;
                final RemindView cell = (RemindView) viewHolder.itemView;
                String timeStr = "";
                timeStr += entity.getFirstTime() + "  ";
                if (!entity.getSecondtime().equals("-1")) {
                    timeStr += entity.getSecondtime() + "  ";
                }
                if (!entity.getThreeTime().equals("-1")) {
                    timeStr += entity.getThreeTime() + "  ";
                }
                if (!entity.getFourTime().equals("-1")) {
                    timeStr += entity.getFourTime() + "  ";
                }
                if (!entity.getFiveTime().equals("-1")) {
                    timeStr += entity.getFiveTime();
                }
                cell.setData(entity.getDrug(), timeStr, entity);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RemindActivity.this, RemindDetailActivityNew.class);
                        intent.putExtra("detailEntity", entity);
                        intent.putExtra("position",position) ;
                        startActivityForResult(intent, 1);
                    }
                });
                cell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onDeleteRemind(entity);
                        return false;
                    }
                });
            }

        }

        @Override
        public int getItemViewType(int position) {
            return position >= data.size() ? 1 : 0;
        }

        @Override
        public int getItemCount() {
            int size = data.size();
            return size > 0 ? (size + 1) : 0;
        }
    }

    public void onDeleteRemind(final RemindEntity entity) {
        editDialog = new BottomSheet.Builder(this)
                .setTitle("用药提醒")
                .setItems(new CharSequence[]{"删除提醒"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        if (which == 0) {
                            dialog.dismiss();
                            deleteDb(entity);
                            RemindUtils.cancelRemind(entity, RemindActivity.this);
                            initData();
                        }
                    }
                }).create();
        editDialog.show();
    }

    private void deleteDb(RemindEntity entity) {
        RemindDao deleteDao = DBInterface.instance().openReadableDb().getRemindDao();
        deleteDao.delete(entity);
        initData();
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    class RemindView extends LinearLayout {

        RemindItemCell cell;
        TextView remindTimeView;
        Context context;
        Paint paint;

        public RemindView(Context context) {
            super(context);
            if (paint == null) {
                paint = new Paint();
                paint.setColor(0xffd9d9d9);
                paint.setStrokeWidth(1);
            }
            this.context = context;
            setOrientation(VERTICAL);
            cell = new RemindItemCell(context);
            addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            remindTimeView = new TextView(context);
            remindTimeView.setSingleLine();
            remindTimeView.setEllipsize(TextUtils.TruncateAt.END);
            remindTimeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            remindTimeView.setTextColor(getResources().getColor(R.color.theme_sub_title));
            remindTimeView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(16));
            addView(remindTimeView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        public void setData(String title, final String remindTime, final RemindEntity entity) {
            cell.setData(R.drawable.remind_drug, title, false);
            remindTimeView.setText(remindTime);
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
                        RemindUtils.setRemind(entity, context);
                    } else {
                        cell.setCheck(false);
                        entity.setIsRemind(0);
                        RemindUtils.cancelRemind(entity, context);
                    }
                    DBInterface.instance().openWritableDb().getRemindDao().insertOrReplace(entity);
                }
            });
            setWillNotDraw(false);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }

//    //添加提醒到系统中
//    public void setRemind(RemindEntity entity) {
//        long startDateLong = TransformDateUitls.getDate(entity.getStartDate());
//
//        setRemindTime(startDateLong, entity.getFirstTime(), entity);
//        setRemindTime(startDateLong, entity.getSecondtime(), entity);
//        setRemindTime(startDateLong, entity.getThreeTime(), entity);
//        setRemindTime(startDateLong, entity.getFourTime(), entity);
//        setRemindTime(startDateLong, entity.getFiveTime(), entity);
//    }
//
//    public void setRemindTime(long startDateLong, String timeStr, RemindEntity entity) {
//        Calendar currentDate = Calendar.getInstance();
//        long intervalTime = 1000 * 60 * 60 * 24 * entity.getIntervalDay();
//        if (!timeStr.equals("-1")) {
////            long time = TransformDateUitls.getTimeLong(timeStr);
////            long remindTime = (startDateLong+time) + currentDate.getTimeZone().getRawOffset();
//            long startTime = TransformDateUitls.getTimeLong(timeStr) + startDateLong;
//            long remindTime = startTime + currentDate.getTimeZone().getRawOffset();
//            if (remindTime < currentDate.getTimeInMillis()) {
//                remindTime += intervalTime;
//            }
//            Intent intent = new Intent(RemindActivity.this, RemindReceiver.class);
//            intent.putExtra("type", (int) remindTime);
//            intent.putExtra("remindInfoEntity", entity);
//            startAlarmRemind(intent, remindTime, intervalTime, (int) remindTime);
//        }
//    }
//
//    public void startAlarmRemind(Intent intent, long startTime, long intervalTime, int type) {
//        PendingIntent sender = PendingIntent.getBroadcast(this, type, intent, 0);
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, sender);
//    }
//
//    public void cancelRemind(RemindEntity entity) {
//        long startDateLong = TransformDateUitls.getDate(entity.getStartDate());
//        setCancelRemindTime(startDateLong, entity.getFirstTime());
//        setCancelRemindTime(startDateLong, entity.getSecondtime());
//        setCancelRemindTime(startDateLong, entity.getThreeTime());
//        setCancelRemindTime(startDateLong, entity.getFourTime());
//        setCancelRemindTime(startDateLong, entity.getFiveTime());
//    }
//
//    public void setCancelRemindTime(long startDateLong, String timeStr) {
//        Calendar currentDate = Calendar.getInstance();
//        if (!timeStr.equals("-1")) {
//            long time = TransformDateUitls.getTimeLong(timeStr);
//            long remindTime = (startDateLong + time) + currentDate.getTimeZone().getRawOffset();
//            Intent intent = new Intent(RemindActivity.this, RemindReceiver.class);
//            PendingIntent sender = PendingIntent.getBroadcast(this, (int) remindTime, intent, 0);
//            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//            manager.cancel(sender);
//        }
//    }
}

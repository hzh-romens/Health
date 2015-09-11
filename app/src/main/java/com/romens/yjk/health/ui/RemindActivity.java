package com.romens.yjk.health.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.RemindDao;
import com.romens.yjk.health.db.entity.RemindEntity;

import java.util.List;

/**
 * Created by anlc on 2015/8/21.
 */
public class RemindActivity extends BaseActivity {

    private ActionBar actionBar;
    private RecyclerView listView;
    private List<RemindEntity> data;
    private RemindAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind, R.id.remind_actionbar);
        initData();

        adapter = new RemindAdapter(data, this);
        actionBar = getMyActionBar();
        listView = (RecyclerView) findViewById(R.id.remind_list);
        listView.setAdapter(adapter);
        setListVewBackground();

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.addItemDecoration(new RemindItemDecoration(AndroidUtilities.dp(10), AndroidUtilities.dp(20), data.size()));

        actionBar.setTitle("用药提醒");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu menu = actionBar.createMenu();
        menu.addItem(1, R.drawable.addcontact_blue);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 1) {
                    startActivity(new Intent(RemindActivity.this, NewAddRemindActivity.class));
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
        adapter.setData(data);
        adapter.notifyDataSetChanged();
        setListVewBackground();
    }

    private void initData() {
        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
        data = remindDao.readDb(remindDao);
    }

//    private List<RemindEntity> readDb() {
//        RemindDao remindDao = DBInterface.instance().openReadableDb().getRemindDao();
//        List<RemindEntity> entities = remindDao.queryBuilder().orderDesc(RemindDao.Properties.Id).list();
//        return entities;
//    }

    private void setListVewBackground() {
        if (data.size() <= 0) {
            listView.setBackgroundResource(R.drawable.guide_1);
        } else {
            listView.setBackgroundColor(0xdddddd);
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

    class RemindAdapter extends RecyclerView.Adapter<RemindViewHolder> {

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
        public RemindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_remind, null);
            return new RemindViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RemindViewHolder holder, final int position) {
            final RemindEntity entity = data.get(position);
            holder.user.setText(entity.getUser());
            holder.drug.setText(entity.getDrug());
            holder.intervalDay.setText(entity.getIntervalDay()+"次");
            holder.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(RemindActivity.this,RemindDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("detailEntity",data.get(position));
                    intent.putExtra("detailBundle", bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class RemindViewHolder extends RecyclerView.ViewHolder {

        private ImageView userIcon;
        private TextView user;
        private TextView drug;
        private TextView intervalDay;
        private TextView detail;

        public RemindViewHolder(View itemView) {
            super(itemView);
            userIcon = (ImageView) itemView.findViewById(R.id.remind_item_usericon);
            user = (TextView) itemView.findViewById(R.id.remind_item_user);
            drug = (TextView) itemView.findViewById(R.id.remind_item_drug);
            intervalDay = (TextView) itemView.findViewById(R.id.remind_item_count);
            detail = (TextView) itemView.findViewById(R.id.remind_item_details);
        }
    }
}

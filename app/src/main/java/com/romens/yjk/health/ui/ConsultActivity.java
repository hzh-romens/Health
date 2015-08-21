package com.romens.yjk.health.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.cells.NewsCell;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by anlc on 2015/8/13.
 */
public class ConsultActivity extends BaseActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private ConsultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult, R.id.action_bar);

        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("咨询");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu menuView = actionBar.createMenu();
        menuView.addItem(0, R.drawable.ic_menu_search);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                }
            }
        });

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.consult_card_view);
        recyclerView = (RecyclerView) findViewById(R.id.conslut_list);
        adapter = new ConsultAdapter(this, onHanldreDate());
        adapter.setLinstener(new ConsultAdapter.onItemClickLinstener() {
            @Override
            public void onClickLinstener(View view, int postion) {
                Toast.makeText(ConsultActivity.this, "item-->" + postion, Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ConsultTask().execute();
            }
        });
    }

    class ConsultTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refreshLayout.setRefreshing(false);
        }
    }

    static class ConsultAdapter extends RecyclerView.Adapter<ADHolder> {

        private Context context;
        private SparseArray<Control> controls;

        private onItemClickLinstener linstener;

        public void setLinstener(onItemClickLinstener linstener) {
            this.linstener = linstener;
        }

        public interface onItemClickLinstener {
            void onClickLinstener(View view, int postion);
        }

        public ConsultAdapter(Context context, SparseArray<Control> controls) {
            this.context = context;
            this.controls = controls;
        }

        @Override
        public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            NewsCell cell = new NewsCell(context);
            cell.setBackgroundResource(R.drawable.list_selector);
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new ADHolder(cell);
        }

        @Override
        public void onBindViewHolder(ADHolder holder, int position) {
            NewsCell cell = (NewsCell) holder.itemView;
            Control control = controls.get(position);
            cell.setValue(control.getInfo(), control.getIconUrl(), true);
            setItemUpdate(holder, position);
        }

        @Override
        public int getItemCount() {
            return controls.size();
        }

        private void setItemUpdate(final RecyclerView.ViewHolder viewHolder, int position) {
            if (linstener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        linstener.onClickLinstener(viewHolder.itemView, viewHolder.getLayoutPosition());
                    }
                });
            }
        }
    }

    private SparseArray<Control> onHanldreDate() {
        SparseArray<Control> control = new SparseArray<>();
        for (int i = 0; i < 20; i++) {
            control.append(i, new Control(getRandomJianHan(20), "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg"));
        }
        return control;
    }

    class Control {
        private String info;
        private String iconUrl;

        public Control(String info, String iconUrl) {
            this.info = info;
            this.iconUrl = iconUrl;
        }

        public String getInfo() {
            return info;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }

    public static String getRandomJianHan(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); //获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); //获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBk"); //转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret += str;
        }
        return ret;
    }

}

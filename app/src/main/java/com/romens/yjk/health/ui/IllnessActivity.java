package com.romens.yjk.health.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.adapter.IllnessAdapter;
import com.romens.yjk.health.ui.adapter.StoreAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollListener;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollLocationListener;
import com.romens.yjk.health.ui.fragment.HomeStoreFragment;

import java.util.ArrayList;
import java.util.List;

/*
create 2015.8.14
hzh
疾病列表页面,
 */
public class IllnessActivity extends BaseActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<String> datas;
    private IllnessAdapter illnessAdapter;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            swipeRefreshLayout.setRefreshing(false);
            super.handleMessage(msg);
            switch (msg.what){
                case HomeStoreFragment.CURRENT:
                    datas= (ArrayList<String>) msg.obj;
                    illnessAdapter =new IllnessAdapter(datas,IllnessActivity.this,null);
                    recyclerView.setAdapter(illnessAdapter);
                    break;
                case HomeStoreFragment.NEXT:

                    ArrayList<String> next= (ArrayList<String>) msg.obj;
                    datas.addAll(next);
                    illnessAdapter.notifyDataSetChanged();
                    break;
                case HomeStoreFragment.EMPTY:
                    Toast.makeText(IllnessActivity.this, "数据为空...", Toast.LENGTH_SHORT).show();
                    break;
                case HomeStoreFragment.ERROR:
                    Toast.makeText(IllnessActivity.this,"数据获取异常...",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illness);
        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swiperefreshlayout);
        recyclerView= (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //下拉刷新加载
            @Override
            public void onRefresh() {
                new Thread(run).start();
            }
        });

        final ABaseLinearLayoutManager layoutManager=new ABaseLinearLayoutManager(IllnessActivity.this, LinearLayoutManager.VERTICAL,false);


        layoutManager.setOnRecyclerViewScrollLocationListener(recyclerView, new OnRecyclerViewScrollLocationListener() {
            @Override
            public void onTopWhenScrollIdle(RecyclerView recyclerView) {

            }

            @Override
            public void onBottomWhenScrollIdle(RecyclerView recyclerView) {

                //跑一个线程，获取下一页数据
                new Thread(nextRun).start();
            }
        });

        layoutManager.getRecyclerViewScrollManager().addScrollListener(recyclerView, new OnRecyclerViewScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                swipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        //获取数据，暂时用这个
        new Thread(run).start();

        //加分割线


        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                //   int curPosition = layoutManager.findFirstVisibleItemPosition();
                // View curItemView = layoutManager.findViewByPosition(curPosition);
                // LayoutInflater.from(getActivity()).inflate(R.layo.);
                Paint paint = new Paint();
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    float x = child.getWidth() + child.getX();
                    float y = child.getHeight() + child.getY();
                    c.drawLine(child.getX(),
                            y,
                            child.getX() + child.getWidth(),
                            y,
                            paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = HomeStoreFragment.dip2px(IllnessActivity.this, 1);
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

    }

    //数据获取的方法
    //获取数据的方法，暂定为假数据
    private void initData() {
        datas=new ArrayList<String>();
        for (int i=0;i<30;i++){
            datas.add("item"+i);
        }
    }

    //第一次加载或者下拉刷新
    private Runnable run=new Runnable() {
        @Override
        public void run() {
            initData();
            handler.sendMessageDelayed(handler.obtainMessage(HomeStoreFragment.CURRENT,datas),0);
        }
    };
    //翻页加载
    private Runnable nextRun=new Runnable() {
        @Override
        public void run() {
            //addData();
            List<String> second=new ArrayList<String>();
            for (int i=0;i<10;i++){
                second.add("item"+(i+30));
            }
            handler.sendMessageDelayed(handler.obtainMessage(HomeStoreFragment.NEXT,second),0);
        }
    };


}

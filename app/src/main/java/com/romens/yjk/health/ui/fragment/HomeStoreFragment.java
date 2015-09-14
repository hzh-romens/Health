package com.romens.yjk.health.ui.fragment;



import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.TestEntity;

import com.romens.yjk.health.ui.IllnessActivity;
import com.romens.yjk.health.ui.adapter.FlowlayoutAdapter;
import com.romens.yjk.health.ui.adapter.StoreAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollListener;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollLocationListener;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by siery on 15/8/13.
 * hzh
 */
public class HomeStoreFragment extends BaseFragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlowLayout flowLayout;
    private RecyclerView recyclerView;
    private List<TestEntity> datas;
    private FlowlayoutAdapter flowlayoutAdapter;
    private StoreAdapter storeAdapter;
    //加载当前页成功的标志
    public  static final int CURRENT=1;
    //加载下一页的标志
    public  static  final int NEXT=2;
    //数据为空的标志
    public  static final int EMPTY=0;
    //数据异常的标志
    public  static final int ERROR=3;
    private List<String> mDatas = new ArrayList<String>();
    View footView;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            swipeRefreshLayout.setRefreshing(false);
            super.handleMessage(msg);
            switch (msg.what){
                case CURRENT:
                    datas= (ArrayList<TestEntity>) msg.obj;
                    storeAdapter =new StoreAdapter(datas,getActivity(),null,footView);
                    recyclerView.setAdapter(storeAdapter);
                    break;
                case NEXT:
                    footView.setVisibility(View.GONE);
                    ArrayList<TestEntity> next= (ArrayList<TestEntity>) msg.obj;
                    datas.addAll(next);
                    storeAdapter.notifyDataSetChanged();
                    break;
                case EMPTY:
                    Toast.makeText(getActivity(), "数据为空...", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Toast.makeText(getActivity(),"数据获取异常...",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home_store, null);
        initView(view);
        mDatas.add("感冒");
        mDatas.add("发烧");
        mDatas.add("咳嗽");
        mDatas.add("慢性胃炎");
        mDatas.add("多少分开始");
        //mDatas.add("第三方鉴定师");
        flowlayoutAdapter=new FlowlayoutAdapter(flowLayout,getActivity(),mDatas);
        flowlayoutAdapter.andTextView();
        flowlayoutAdapter.ItemClickListener(new FlowlayoutAdapter.FlowLayoutItemClick() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), mDatas.get(position), Toast.LENGTH_SHORT)
                        .show();
                Intent i=new Intent(getActivity(), IllnessActivity.class);
                getActivity().startActivity(i);
            }
        });
       // andTextView(flowLayout,getActivity(),datas);
        footView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_view_item_type_footer, null);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        footView.setLayoutParams(lp);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        swipeRefreshLayout.setProgressViewOffset(false, 0, AndroidUtilities.getCurrentActionBarHeight());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //下拉刷新加载
            @Override
            public void onRefresh() {
                new Thread(run).start();
            }
        });

        final ABaseLinearLayoutManager layoutManager=new ABaseLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);


        layoutManager.setOnRecyclerViewScrollLocationListener(recyclerView, new OnRecyclerViewScrollLocationListener() {
            @Override
            public void onTopWhenScrollIdle(RecyclerView recyclerView) {

            }

            @Override
            public void onBottomWhenScrollIdle(RecyclerView recyclerView) {
                footView.setVisibility(View.VISIBLE);
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
                outRect.bottom= AndroidUtilities.dp(1);

                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //newState:0 1 2
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //向下滑动
                if (dy > 0) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    Log.i("出现在屏幕上的第一个Item的id-----2", firstVisibleItemPosition + "");
                } else {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    Log.i("出现在屏幕上的第一个Item的id-----3",firstVisibleItemPosition+"");
                }
            }
        });
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        Log.i("出现在屏幕上的第一个Item的id1-----", firstVisibleItemPosition + "");

        return view;
    }



    private void initView(View view) {
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        flowLayout= (FlowLayout) view.findViewById(R.id.flowlayout);
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerview);
    }








    //获取数据的方法，暂定为假数据
    private void initData() {
        datas=new ArrayList<TestEntity>();
        datas.add(new TestEntity(1, "九九感冒灵", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥10"));
        datas.add(new TestEntity(2, "感康泰克", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥20"));
        datas.add(new TestEntity(3, "江东健胃消食片", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥30"));
        datas.add(new TestEntity(4, "青霉素", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥40"));
        datas.add(new TestEntity(5, "云南白药", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥50"));
        datas.add(new TestEntity(6, "急支糖浆", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥60"));
        datas.add(new TestEntity(7, "心必通", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥70"));
        datas.add(new TestEntity(8, "泰诺", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥80"));
        datas.add(new TestEntity(9, "艾畅", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥90"));
        datas.add(new TestEntity(10, "创口贴", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥100"));
    }

    //第一次加载或者下拉刷新
    private Runnable run=new Runnable() {
        @Override
        public void run() {
            swipeRefreshLayout.setRefreshing(true);
            initData();
            handler.sendMessageDelayed(handler.obtainMessage(CURRENT,datas),0);
        }
    };
    //翻页加载
    private Runnable nextRun=new Runnable() {
        @Override
        public void run() {
            //addData();
            List<TestEntity> second=new ArrayList<TestEntity>();
            second.add(new TestEntity(11, "碘伏", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥60"));
            second.add(new TestEntity(12, "百多邦", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥70"));
            second.add(new TestEntity(13, "百服宁", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥80"));
            second.add(new TestEntity(14, "美林", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥90"));
            second.add(new TestEntity(15, "新康泰克", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "￥100"));
            handler.sendMessageDelayed(handler.obtainMessage(NEXT,second),0);
        }
    };







    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }


}
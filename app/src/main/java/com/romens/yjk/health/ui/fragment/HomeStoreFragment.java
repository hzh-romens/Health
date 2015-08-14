package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.IllnessActivity;
import com.romens.yjk.health.ui.adapter.StoreAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollListener;
import com.romens.yjk.health.ui.components.OnRecyclerViewScrollLocationListener;
import com.romens.yjk.health.ui.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by siery on 15/8/10.
 */
public class HomeStoreFragment extends BaseFragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlowLayout flowLayout;
    private RecyclerView recyclerView;
    private List<String> datas;
    private StoreAdapter storeAdapter;
    //加载当前页成功的标志
    public  static final int CURRENT=1;
    //加载下一页的标志
    public  static  final int NEXT=2;
    //数据为空的标志
    public  static final int EMPTY=0;
    //数据异常的标志
    public  static final int ERROR=3;

    View footView;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            swipeRefreshLayout.setRefreshing(false);
            super.handleMessage(msg);
            switch (msg.what){
                case CURRENT:
                    datas= (ArrayList<String>) msg.obj;
                    storeAdapter =new StoreAdapter(datas,getActivity(),null,footView);
                    recyclerView.setAdapter(storeAdapter);
                    break;
                case NEXT:
                    footView.setVisibility(View.GONE);
                    ArrayList<String> next= (ArrayList<String>) msg.obj;
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
        andTextView();
        footView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_view_item_type_footer, null);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        footView.setLayoutParams(lp);

        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);

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
                outRect.bottom = dip2px(getActivity(), 1);
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

        return view;
    }



    private void initView(View view) {
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        flowLayout= (FlowLayout) view.findViewById(R.id.flowlayout);
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerview);
//        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        recyclerView.setLayoutParams(layoutParams);
    }




    /**
     * 添加带自定义背景的TextView
     */
    int n=7;
    int temp =0;

    private List<String> mDatas = new ArrayList<String>();
    private void andTextView() {

        for (int i = temp; i <temp+n; i++) {

            mDatas.add("字符串" + i);

        }
        temp=temp+n;



        // mFlowLayout = new FlowLayout(MainActivity.this);
        int layoutPadding = UIUtils.dip2px(13);
        flowLayout.setPadding(layoutPadding, layoutPadding, layoutPadding,
                layoutPadding);
        flowLayout.setHorizontalSpacing(layoutPadding);
        flowLayout.setVerticalSpacing(layoutPadding);

        int textPaddingV = UIUtils.dip2px(4);
        int textPaddingH = UIUtils.dip2px(10);
        int backColor = 0xffcecece;
        int radius = UIUtils.dip2px(5);
        // 代码动态创建一个图片
        GradientDrawable pressDrawable = createDrawable(
                backColor, backColor, radius);
        Random mRdm = new Random();
        for (int i = 0; i < mDatas.size(); i++) {
            TextView tv = new TextView(UIUtils.getContext());
            // 随机颜色的范围0x202020~0xefefef
            int red = 32 + mRdm.nextInt(208);
            int green = 32 + mRdm.nextInt(208);
            int blue = 32 + mRdm.nextInt(208);
            //   int color = 0xff000000 | (red << 16) | (green << 8) | blue;
            int color=getActivity().getResources().getColor(R.color.md_blue_900);
            // 创建背景图片选择器
            GradientDrawable normalDrawable = createDrawable(
                    color, backColor, radius);
            StateListDrawable selector = createSelector(
                    normalDrawable, pressDrawable);
            tv.setBackgroundDrawable(selector);

            final String text = mDatas.get(i);
            tv.setText(text);
            tv.setTextColor(Color.rgb(255, 255, 255));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(textPaddingH, textPaddingV, textPaddingH,
                    textPaddingV);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT)
                            .show();
                    Intent i=new Intent(getActivity(), IllnessActivity.class);
                    getActivity().startActivity(i);

                }
            });
            flowLayout.addView(tv);
        }
    }


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
            handler.sendMessageDelayed(handler.obtainMessage(CURRENT,datas),3000);
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
            handler.sendMessageDelayed(handler.obtainMessage(NEXT,second),3000);
        }
    };







    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 创建一个图片
     * @param contentColor 内部填充颜色
     * @param strokeColor  描边颜色
     * @param radius       圆角
     */
    public static GradientDrawable createDrawable(int contentColor, int strokeColor, int radius) {
        GradientDrawable drawable = new GradientDrawable(); // 生成Shape
        drawable.setGradientType(GradientDrawable.RECTANGLE); // 设置矩形
        drawable.setColor(contentColor);// 内容区域的颜色
        drawable.setStroke(1, strokeColor); // 四周描边,描边后四角真正为圆角，不会出现黑色阴影。如果父窗体是可以滑动的，需要把父View设置setScrollCache(false)
        drawable.setCornerRadius(radius); // 设置四角都为圆角
        return drawable;
    }
    /**
     * 创建一个图片选择器
     * @param normalState  普通状态的图片
     * @param pressedState 按压状态的图片
     */
    public static StateListDrawable createSelector(Drawable normalState, Drawable pressedState) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressedState);
        bg.addState(new int[]{android.R.attr.state_enabled}, normalState);
        bg.addState(new int[]{}, normalState);
        return bg;
    }


}
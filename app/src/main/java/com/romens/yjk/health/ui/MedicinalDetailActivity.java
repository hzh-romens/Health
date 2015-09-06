package com.romens.yjk.health.ui;


import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.TextView;


import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.model.TestEntity;
import com.romens.yjk.health.ui.adapter.MedicinalDetailAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;

import com.romens.yjk.health.ui.components.OnRecyclerViewScrollLocationListener;
import com.romens.yjk.health.ui.components.RecyclerViewScrollManager;
import com.romens.yjk.health.ui.fragment.HomeStoreFragment;
import com.romens.yjk.health.ui.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class MedicinalDetailActivity extends BaseActivity implements AppNotificationCenter.NotificationCenterDelegate{
    private RecyclerView recyclerView;
    private TextView tv_favorite, tv_buy,ask;
    private TextView shopcar;
    private ABaseLinearLayoutManager layoutManager;
    private MedicinalDetailAdapter medicinalDetailAdapter;
    private List<TestEntity> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicinal_detail, R.id.action_bar);
        initView();

        data = new ArrayList<TestEntity>();
        //(int type,String json,String imageUrl,String infor)
        data.add(new TestEntity(0, "", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", ""));
        data.add(new TestEntity(1, "药品成分", "", "甲酸铵"));
        //data.add(new TestEntity(2,))
        data.add(new TestEntity(3, "", "", ""));
        data.add(new TestEntity(4, "", "", ""));
        data.add(new TestEntity(5, "在线药店", "", ""));
        data.add(new TestEntity(6, "一号店", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "主要经营感冒药等"));
        data.add(new TestEntity(6, "二号店", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "主要经营感冒药等"));
        data.add(new TestEntity(7, "更多", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "主要经营感冒药等"));
        data.add(new TestEntity(4, "", "", ""));
        data.add(new TestEntity(5, "推荐药品", "", ""));
        data.add(new TestEntity(8, "青霉素", "http://img.yy960.com/2013/03/20130326132534.JPG", "￥40"));
        medicinalDetailAdapter = new MedicinalDetailAdapter(data, MedicinalDetailActivity.this);
        recyclerView.setAdapter(medicinalDetailAdapter);

        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                updateShoppingCartCount(3);
            }
        }, 3000);
        float translationY = recyclerView.getTranslationY();


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
                    int top = recyclerView.getChildAt(0).getTop();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                   // if( recyclerView.getChildAt(firstVisibleItemPosition).getTop()!=0) {
                     //   int top1 = recyclerView.getChildAt(firstVisibleItemPosition).getTop();
                       // Log.i("出现在屏幕上的第一个Item的id-----3",firstVisibleItemPosition+"-----top"+top+"--------top1"+top1);
                    //}

                }
            }
        });
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        Log.i("出现在屏幕上的第一个Item的id1-----", firstVisibleItemPosition + "");

    }
    ActionBar actionBar;
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        initLayoutManager();


        tv_favorite = (TextView) findViewById(R.id.favorite);
        tv_buy = (TextView) findViewById(R.id.tv_buy);

        shopcar = (TextView) findViewById(R.id.shopcar);
        shopcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MedicinalDetailActivity.this,ShopCarActivity.class);
                startActivity(i);
            }
        });
        ask= (TextView) findViewById(R.id.ask);


       actionBar = getMyActionBar();
        actionBar.setTitle("某某药品");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                }
            }
        });
    }

    private void initLayoutManager() {
        layoutManager = new ABaseLinearLayoutManager(MedicinalDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                Paint paint = new Paint();
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    float x = child.getWidth() + child.getX();
                    float y = child.getHeight() + child.getY();
//                    c.drawLine(child.getX(),
//                            y,
//                            child.getX() + child.getWidth(),
//                            y,
//                            paint);

                }

            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom= AndroidUtilities.dp(1);
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

    }
    private void updateShoppingCartCount(int count) {
        Bitmap shoppingCartCount = ShoppingCartUtils.createShoppingCartIcon(this, R.drawable.ic_shopping_cart_white_24dp, count);
        //BitmapDrawable bd=new BitmapDrawable(shoppingCartCount)
        //Bitmap bmp = BitmapFactory.decodeFile(getResources().getResourceName(R.drawable.ic_shopping_cart_white_24dp));
        DisplayMetrics dm =getResources().getDisplayMetrics();
        shoppingCartCount.setDensity(dm.densityDpi);
        final BitmapDrawable bd = new BitmapDrawable(this.getResources(), shoppingCartCount);
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        shopcar.setCompoundDrawables(null, bd, null, null);
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == AppNotificationCenter.shoppingCartCountChanged) {
            int count = (int) args[0];
            updateShoppingCartCount(count);
        }
    }

}

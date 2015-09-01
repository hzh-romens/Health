package com.romens.yjk.health.ui;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DiscoveryDao;
import com.romens.yjk.health.db.dao.ShopCarDao;
import com.romens.yjk.health.model.ShopCarTestEntity;
import com.romens.yjk.health.ui.adapter.ShopCarAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;

import java.util.List;


public class ShopCarActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ShopCarAdapter shopCarAdapter;
    private SparseArray<ShopCarTestEntity> data;
    private CheckableFrameLayout all_choice;
    private TextView tv_all1, tv_all2, tv_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car, R.id.action_bar);
        initView();
        ActionBar actionBar = getMyActionBar();
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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        final ABaseLinearLayoutManager layoutManager = new ABaseLinearLayoutManager(ShopCarActivity.this, LinearLayoutManager.VERTICAL, false);
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
                    c.drawRect(child.getX(),
                            y,
                            child.getX() + child.getWidth(),
                            y,
                            paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                //   outRect.bottom = AndroidUtilities.dp(1);
                outRect.set(0, 0, 0, AndroidUtilities.dp(5));
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        //获取数据
        getData();
        shopCarAdapter = new ShopCarAdapter(this, data);
        shopCarAdapter.AdapterListener(new ShopCarAdapter.AdapterCallback() {
            @Override
            public void onDataChanged() {
                all_choice.setChecked(shopCarAdapter.isAllSelected());
            }

            @Override
            public void sumMoneyChange(String money) {
                tv_all1.setText("合计$" + money);
                tv_all2.setText("合计$" + money);
            }
        });
        recyclerView.setAdapter(shopCarAdapter);

        all_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopCarAdapter.switchAllSelect(!all_choice.isChecked());
            }
        });


    }

    private void initView() {
        all_choice = (CheckableFrameLayout) findViewById(R.id.all_choice);
        tv_all1 = (TextView) findViewById(R.id.tv_all1);
        tv_all2 = (TextView) findViewById(R.id.tv_all2);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送请求，并将其存到数据库
               // if(data)
                Toast.makeText(ShopCarActivity.this,"功能暂未开放",Toast.LENGTH_SHORT).show();
                List<ShopCarTestEntity> datas = shopCarAdapter.getDatas();
                if(datas!=null){
                    ShopCarDao shopCarDao = DBInterface.instance().openWritableDb().getShopCarDao();
                    shopCarDao.insertOrReplaceInTx(datas);

            }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getData() {
        data = new SparseArray<ShopCarTestEntity>();
        data.append(0, new ShopCarTestEntity("1",1, "青霉素", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "同仁药房", "true", 22.0, 1));
        data.append(1, new ShopCarTestEntity("2",2, "新康泰克", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "青岛药房", "false", 22.0, 2));
        data.append(2, new ShopCarTestEntity("3",3, "九九感冒灵", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "山东药房", "true", 22.0, 3));
        data.append(3, new ShopCarTestEntity("4",4, "罗素1", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "江西药房", "false", 22.0, 4));
        data.append(4, new ShopCarTestEntity("5",4, "罗素2", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "广州药房", "true", 22.0, 3));
        data.append(5, new ShopCarTestEntity("6",4, "罗素3", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "河南药房", "false", 22.0, 5));
    }


}

package com.romens.yjk.health.ui;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ShopCarTestEntity;
import com.romens.yjk.health.model.TestEntity;
import com.romens.yjk.health.ui.adapter.ShopCarAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;


import java.util.ArrayList;
import java.util.List;

public class ShopCarActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ShopCarAdapter shopCarAdapter;
    private List<ShopCarTestEntity> data;
    private CheckBox all_choice;
    private TextView tv_all1,tv_all2,tv_pay;

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
        recyclerView= (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        final ABaseLinearLayoutManager layoutManager=new ABaseLinearLayoutManager(ShopCarActivity.this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
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
        shopCarAdapter=new ShopCarAdapter(this,data);
        recyclerView.setAdapter(shopCarAdapter);
        //计算价钱

        shopCarAdapter.AdapterListener(new ShopCarAdapter.AdapterCallback() {
            @Override
            public void onCheckStateChanged(int position, boolean checked) {
                Toast.makeText(ShopCarActivity.this, "sahdkahsksah", Toast.LENGTH_SHORT).show();
                data.get(position).setCheck(checked);
                boolean allSelected = shopCarAdapter.isAllSelected();
                if (allSelected) {
                    all_choice.setChecked(true);
                } else {
                    all_choice.setChecked(false);
                }
            }
        });

    }

    private void initView() {
        all_choice= (CheckBox) findViewById(R.id.all_choice);
        tv_all1= (TextView) findViewById(R.id.tv_all1);
        tv_all2= (TextView) findViewById(R.id.tv_all2);
        tv_pay= (TextView) findViewById(R.id.tv_pay);
        all_choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < data.size(); i++) {
                        if (!data.get(i).getCheck()) {
                            data.get(i).setCheck(true);
                        }
                    }
                    try {
                        shopCarAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("onCheckChanged", e.getMessage());
                    }

                }else{
                    if(shopCarAdapter.isAllSelected()) {
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).setCheck(false);
                        }
                        try {
                            shopCarAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e("onCheckChanged", e.getMessage());
                        }
                    }


                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getData() {
        data=new ArrayList<ShopCarTestEntity>();
        data.add(new ShopCarTestEntity(1, "青霉素", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "同仁药房",true,"22.0"));
        data.add(new ShopCarTestEntity(2, "新康泰克", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "青岛药房", false, "22.0"));
        data.add(new ShopCarTestEntity(3, "九九感冒灵", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "山东药房", true, "22.0"));
        data.add(new ShopCarTestEntity(4, "罗素", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "江西药房", false, "22.0"));
        data.add(new ShopCarTestEntity(4, "罗素", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "广州药房", true, "22.0"));
        data.add(new ShopCarTestEntity(4, "罗素", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "河南药房", false, "22.0"));
    }


}

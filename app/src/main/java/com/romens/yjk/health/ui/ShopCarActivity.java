package com.romens.yjk.health.ui;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DiscoveryDao;
import com.romens.yjk.health.db.dao.ShopCarDao;
import com.romens.yjk.health.db.entity.DiscoveryEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.model.ShopCarTestEntity;
import com.romens.yjk.health.ui.adapter.ShopCarAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class ShopCarActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ShopCarAdapter shopCarAdapter;
    private CheckableFrameLayout all_choice;
    private TextView tv_all1, tv_all2, tv_pay;
    private List<ShopCarEntity> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car, R.id.action_bar);
        initView();
        shopCarAdapter = new ShopCarAdapter(this);
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
        recyclerView.setAdapter(shopCarAdapter);
        //获取数据
        requestShopCarDataChanged();

        shopCarAdapter.AdapterListener(new ShopCarAdapter.AdapterCallback() {
            @Override
            public void onDataChanged() {
                all_choice.setChecked(shopCarAdapter.isAllSelected());
            }

            @Override
            public void sumMoneyChange(String money) {
                int i = money.indexOf(".");
                String sumMoney = money.substring(0, i + 2);
                tv_all1.setText("合计$" + sumMoney);
                tv_all2.setText("合计$" + sumMoney);
            }
        });


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
                //
                Toast.makeText(ShopCarActivity.this, "功能暂未开放", Toast.LENGTH_SHORT).show();
                List<ShopCarEntity> datas = shopCarAdapter.getDatas();
                if (datas != null) {
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
    //获取购物车的信息
    private void requestShopCarDataChanged() {
         int lastTime = DBInterface.instance().getShopCarDataLastTime();
        //将更新时间加上去
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("LASTTIME", lastTime).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserBuyCarList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetBuyCarCount", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    onResponseShopCarData(responseProtocol.getResponse());
                } else {
                    Log.e("GetBuyCarCount", "ERROR");
                }
            }
        });
    }

    public void onResponseShopCarData(List<LinkedTreeMap<String, String>> ShopCarData) {
        int count = ShopCarData == null ? 0 : ShopCarData.size();
        if (count <= 0) {
            return;
        }
        List<ShopCarEntity> result=new ArrayList<ShopCarEntity>();
        for (LinkedTreeMap<String, String> item : ShopCarData) {
            ShopCarEntity entity = ShopCarEntity.mapToEntity(item);
            entity.setCHECK("true");
            entity.setCreatedTime((int) Calendar.getInstance().getTimeInMillis());
            entity.setUpdatedTime((int) Calendar.getInstance().getTimeInMillis());
            result.add(entity);
        }
        //暂且不做数据库插入操作
        if (result.size() > 0) {
            ShopCarDao shopCarDao =DBInterface.instance().openWritableDb().getShopCarDao();
            shopCarDao.insertOrReplaceInTx(result);
        }
       bindData();

    }

    private void bindData() {
        List<ShopCarEntity> result = DBInterface.instance().loadAllShopCar();
        Log.i("数据库数量----",result.size()+"");
        shopCarAdapter.SetData(result);
       // recyclerView.setAdapter(shopCarAdapter);
    }


}

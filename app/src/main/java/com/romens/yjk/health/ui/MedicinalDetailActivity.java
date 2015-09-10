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

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import com.romens.yjk.health.model.TestEntity;
import com.romens.yjk.health.model.WeiShopEntity;
import com.romens.yjk.health.ui.adapter.MedicinalDetailAdapter;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.squareup.okhttp.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedicinalDetailActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView tv_favorite, tv_buy, ask;
    private TextView shopcar;
    private LinearLayout menu_bottom;
    private ABaseLinearLayoutManager layoutManager;
    private MedicinalDetailAdapter medicinalDetailAdapter;
    private List<TestEntity> data;
    private WeiShopEntity weiShopEntity;
    private List<String> urls;
    private static String PRICE = "";
    private String GUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicinal_detail, R.id.action_bar);
        GUID = getIntent().getStringExtra("guid");
        initView();
        requestShopCarCountChanged();
        requestStoreData();
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
        tv_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBuy();
            }
        });

        shopcar = (TextView) findViewById(R.id.shopcar);
        shopcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MedicinalDetailActivity.this, ShopCarActivity.class);
                startActivity(i);
            }
        });
        ask = (TextView) findViewById(R.id.ask);


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
                outRect.bottom = AndroidUtilities.dp(1);
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

    }

    //更新购物车数量
    private void updateShoppingCartCount(int count) {
        Bitmap shoppingCartCount = ShoppingCartUtils.createShoppingCartIcon(this, R.drawable.ic_shopping_cart_white_24dp, count);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        shoppingCartCount.setDensity(dm.densityDpi);
        final BitmapDrawable bd = new BitmapDrawable(this.getResources(), shoppingCartCount);
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
        shopcar.setCompoundDrawables(null, bd, null, null);
    }

    //购物车数据请求
    private void requestShopCarCountChanged() {
        // int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("", "").build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetBuyCarCount", args);
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
                    List<LinkedTreeMap<String, String>> response = responseProtocol.getResponse();
                    LinkedTreeMap<String, String> stringStringLinkedTreeMap = response.get(0);
                    final String buycount = stringStringLinkedTreeMap.get("BUYCOUNT");
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            updateShoppingCartCount(Integer.parseInt(buycount));
                        }
                    });
                } else {
                    Log.e("GetBuyCarCount", "ERROR");
                }
            }
        });
    }

    //商品列表信息数据请求
    private void requestStoreData() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("GUID", "0111B165-8675-4546-AD46-8F95CEB01D15").build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder().withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message message) {
                Log.e("GetStoreData1", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String wshop = jsonObject.getString("wshop");
                        //JSONArray wshop = jsonObject.getJSONArray("wshop");
                        JSONArray jsonArray = new JSONArray(wshop);
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        weiShopEntity = new WeiShopEntity();
                        weiShopEntity.setBARCODE(jsonObject1.getString("BARCODE"));
                        weiShopEntity.setCD(jsonObject1.getString("CD"));
                        weiShopEntity.setCODE(jsonObject1.getString("CODE"));
                        weiShopEntity.setDETAILDESCRIPTION(jsonObject1.getString("DETAILDESCRIPTION"));
                        weiShopEntity.setGOODSSORTGUID(jsonObject1.getString("GOODSSORTGUID"));
                        weiShopEntity.setGUID(jsonObject1.getString("GUID"));
                        weiShopEntity.setMARKETPRICE(jsonObject1.getString("MARKETPRICE"));
                        weiShopEntity.setNAME(jsonObject1.getString("NAME"));
                        weiShopEntity.setPZWH(jsonObject1.getString("PZWH"));
                        weiShopEntity.setSPEC(jsonObject1.getString("SPEC"));
                        PRICE = jsonObject1.getString("USERPRICE");
                        weiShopEntity.setUSERPRICE(jsonObject1.getString("USERPRICE"));
                        if (jsonObject1.isNull("GOODSURL")) {
                            weiShopEntity.setGOODSURL("");
                        } else {
                            weiShopEntity.setGOODSURL(jsonObject1.getString("GOODSURL"));
                        }
                        String goodurl = jsonObject.getString("goodurl");
                        JSONArray jsonArray1 = new JSONArray(goodurl);
                        urls = new ArrayList<String>();
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                            String url = jsonObject2.getString("URL");
                            urls.add(url);
                        }
                        data = new ArrayList<TestEntity>();
                        if ("".equals(weiShopEntity.getGOODSURL())) {
                            data.add(new TestEntity(0, "", urls.get(0), ""));
                        } else {
                            data.add(new TestEntity(0, "", weiShopEntity.getGOODSURL(), ""));
                        }
                        data.add(new TestEntity(1, "药品名称", "", weiShopEntity.getNAME()));
                        data.add(new TestEntity(1, "会员价", "", weiShopEntity.getMARKETPRICE()));
                        data.add(new TestEntity(1, "用户价", "", weiShopEntity.getUSERPRICE()));
                        data.add(new TestEntity(1, "药品描述", "", weiShopEntity.getDETAILDESCRIPTION()));
                        //data.add(new TestEntity(2,))
                        data.add(new TestEntity(3, "", "", ""));
                        data.add(new TestEntity(4, "", "", ""));
                        data.add(new TestEntity(5, "在线药店", "", ""));
                        data.add(new TestEntity(6, "一号店", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "主要经营感冒药等"));
                        data.add(new TestEntity(6, "二号店", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "主要经营感冒药等"));
                        data.add(new TestEntity(7, "更多", "http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg", "主要经营感冒药等"));
                        data.add(new TestEntity(4, "", "", ""));
                        data.add(new TestEntity(5, "推荐药品", "", ""));
                        if (urls.size() != 0) {
                            data.add(new TestEntity(8, "", "true", ""));
                        }
                        medicinalDetailAdapter = new MedicinalDetailAdapter(data, MedicinalDetailActivity.this);
                        medicinalDetailAdapter.setUrls(urls);
                        recyclerView.setAdapter(medicinalDetailAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("GetStoreData2", "ERROR");
                }
            }
        });
    }

    //加入购物车
    private void requestBuy() {
        int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("GOODGUID", "06B6830C-E839-49E0-ACB6-123C6662C754");
        args.put("BUYCOUNT", "1");
        args.put("PRICE", PRICE);
        Log.i("价钱----",PRICE);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "InsertIntoCar", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("InsertIntoCar", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Log.i("加入购物车",response);
                    if("ERROE".equals(response)){
                        Toast.makeText(MedicinalDetailActivity.this,"加入购物车异常",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MedicinalDetailActivity.this,"成功加入购物车",Toast.LENGTH_SHORT).show();
                       requestShopCarCountChanged();

                    }
                } else {
                   // ResponseProtocol<String> responseProtocol= (ResponseProtocol<String>) errorMsg.protocol;
                    //String response = responseProtocol.getResponse();
                    Log.e("InsertIntoCar", errorMsg.toString()+"===="+errorMsg.msg);
                }
            }
        });
    }


}

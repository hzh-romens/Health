package com.romens.yjk.health.ui;
import android.content.Context;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.model.TestEntity;
import com.romens.yjk.health.model.WeiShopEntity;
import com.romens.yjk.health.ui.adapter.MedicinalDetailAdapter;
import com.romens.yjk.health.ui.cells.PopWindowCell;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
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
        String s = getIntent().getStringExtra("guid");
        if(s!=null){
            GUID=s;
        }else{
            GUID="";
        }
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
               // Intent i = new Intent(MedicinalDetailActivity.this, ShopCarActivity.class);
                //startActivity(i);
            }
        });

        ask = (TextView) findViewById(R.id.ask);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出一个PopWindow
                getPopWindowInstance();
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                mPopupWindow.showAtLocation(v, Gravity.LEFT | Gravity.BOTTOM, location[0], v.getHeight());
            }
        });


        actionBar = getMyActionBar();
        actionBar.setTitle("药品详情");
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
                .put("GUID","0BF7C472-78C5-4C0D-84FA-A2D8643173B5").build();
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
                    data = new ArrayList<TestEntity>();
                    Log.i("数据-----++","------"+response);
                    if(response!=null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String wshop = jsonObject.getString("wshop");
                            if(new JSONArray(wshop).length()!=0) {
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
                                PRICE = jsonObject1.getString("MARKETPRICE");
                                weiShopEntity.setUSERPRICE(jsonObject1.getString("USERPRICE"));
                                if (jsonObject1.isNull("GOODSURL")) {
                                    weiShopEntity.setGOODSURL("");
                                } else {
                                    weiShopEntity.setGOODSURL(jsonObject1.getString("GOODSURL"));
                                }
                                String goodurl = jsonObject.getString("goodurl");
                                JSONArray jsonArray1 = new JSONArray(goodurl);
                                urls = new ArrayList<String>();
                                if (jsonArray1.length() != 0) {
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                        String url = jsonObject2.getString("URL");
                                        urls.add(url);
                                    }
                                }

                                if ("".equals(weiShopEntity.getGOODSURL())) {
                                    //data.add(new TestEntity(0, "", urls.get(0), ""));
                                } else {
                                    data.add(new TestEntity(0, "", weiShopEntity.getGOODSURL(), ""));
                                }
                                data.add(new TestEntity(1, "药品名称", "", weiShopEntity.getNAME()));
                                data.add(new TestEntity(2, "用户价:", "", "$"+weiShopEntity.getMARKETPRICE()));
                                data.add(new TestEntity(2, "会员价:", "", "$"+weiShopEntity.getUSERPRICE()));
                                data.add(new TestEntity(1, "药品描述", "", weiShopEntity.getCD()+"制药有限公司生产" +
                                        ""+weiShopEntity.getPZWH()));
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
                            }else{
                                data.add(new TestEntity(9,"","",""));
                            }
                            medicinalDetailAdapter = new MedicinalDetailAdapter(data, MedicinalDetailActivity.this);
                            medicinalDetailAdapter.setUrls(urls);
                            recyclerView.setAdapter(medicinalDetailAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(MedicinalDetailActivity.this,"药品不存在",Toast.LENGTH_SHORT).show();
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
        args.put("GOODGUID", GUID);
        args.put("BUYCOUNT", "1");
        args.put("PRICE", PRICE);

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
                    if("ERROE".equals(response)){
                        Toast.makeText(MedicinalDetailActivity.this,"加入购物车异常",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MedicinalDetailActivity.this,"成功加入购物车",Toast.LENGTH_SHORT).show();
                       requestShopCarCountChanged();
                    }
                } else {
                    Log.e("InsertIntoCar", errorMsg.toString()+"===="+errorMsg.msg);
                }
            }
        });
    }

    // 获取PopWindow实例 保持一个实例
    private void getPopWindowInstance() {
        if (null != mPopupWindow) {
            mPopupWindow.dismiss();
            return;
        } else {
            initPopWindow();
        }
    }

    private PopupWindow mPopupWindow;
    private int mScreenwidth;
    private int mScreenHeight;

    // 创建PopupWindow
    @SuppressWarnings("deprecation")
    private void initPopWindow() {
        mScreenwidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        // 创建一个PopupWindow 并设置宽高
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
        FrameLayout container=new FrameLayout(this);
        ListView lv=new ListView(this);
        lv.setBackgroundResource(R.color.window_grey_background);

        container.addView(lv, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        mPopupWindow = new PopupWindow(container,mScreenwidth/3,
                LayoutHelper.WRAP_CONTENT);


        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);
        List<String> datas=new ArrayList<String>();
        datas.add("咨询1");
        datas.add("咨询2");
        datas.add("咨询3");
        PopAdapter popAdapter = new PopAdapter(MedicinalDetailActivity.this,datas);
        lv.setAdapter(popAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Toast.makeText(MedicinalDetailActivity.this, "功能暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
                mPopupWindow.dismiss();
            }
        });

    }
    //popWindow的Adapter
    private class PopAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mDdatas;

        public PopAdapter(Context context, List<String> datas) {
            this.mContext = context;
            this.mDdatas = datas;
        }

        @Override
        public int getCount() {
            if (mDdatas != null) {
                return mDdatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            PopWindowCell popWindowCell=new PopWindowCell(mContext);

            popWindowCell.setValue(mDdatas.get(position),"http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
            return popWindowCell;
        }




    }

}

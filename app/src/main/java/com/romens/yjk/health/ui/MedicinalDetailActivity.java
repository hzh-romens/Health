package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.location.core.AMapLocException;
import com.google.gson.Gson;
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
import com.romens.extend.scanner.Intents;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.core.LocationHelper;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.DaoMaster;
import com.romens.yjk.health.db.dao.HistoryDao;
import com.romens.yjk.health.db.entity.HistoryEntity;
import com.romens.yjk.health.model.ADPagerEntity;
import com.romens.yjk.health.model.GoodSpicsEntity;
import com.romens.yjk.health.model.NearByOnSaleEntity;
import com.romens.yjk.health.model.TestEntity;
import com.romens.yjk.health.model.WeiShopEntity;

import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.adapter.MedicinalDetailAdapter;
import com.romens.yjk.health.ui.cells.PopWindowCell;
import com.romens.yjk.health.ui.components.ABaseLinearLayoutManager;
import com.romens.yjk.health.ui.controls.ADBaseControl;
import com.romens.yjk.health.ui.controls.ADErrorControl;
import com.romens.yjk.health.ui.controls.ADGroupNameControls;
import com.romens.yjk.health.ui.controls.ADHorizontalScrollControl;
import com.romens.yjk.health.ui.controls.ADIllustrationControl;
import com.romens.yjk.health.ui.controls.ADMedicinalDetailControl;
import com.romens.yjk.health.ui.controls.ADMoreControl;
import com.romens.yjk.health.ui.controls.ADPagerControl;
import com.romens.yjk.health.ui.controls.ADStoreControls;

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
    private List<TestEntity> data;
    private WeiShopEntity weiShopEntity;
    private List<String> urls;
    private static String PRICE = "";
    private String GUID;
    private LocationManagerProxy mAMapLocationManager;
    private Location myLocation;
    private double LONGITUDE;
    private double LATITUDE;
    private List<NearByOnSaleEntity> nearResult;
    private boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicinal_detail, R.id.action_bar);
        initIntentValue();
        initView();
        requestNearbyData();

    }
    //获取intent传递过来的值
    private void initIntentValue() {
        //测试数据
        String s = getIntent().getStringExtra("guid");
        if (s != null) {
            GUID = s;
        } else {
            GUID = "";
            GUID = "851823b0-75fc-4795-8c2f-4554ec5402cf";
        }
        String mark = getIntent().getStringExtra("flag");
        if("".equals(mark)||mark ==null){
             flag=false;
        }else if("true".equals(mark)){
            flag=true;
        }else{
            flag=false;
        }
    }

    //获取附近有售数据
    private void requestNearbyData() {
        initMyLocation();
    }

    ActionBar actionBar;

    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        if (Build.VERSION.SDK_INT >= 9) {
            recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        }
        recyclerView.setClipToPadding(false);
        initLayoutManager();

        tv_favorite = (TextView) findViewById(R.id.favorite);
        tv_favorite.setEnabled(false);
        tv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserConfig.isClientLogined()) {
                    addtoFavorite();
                } else {
                    Toast.makeText(MedicinalDetailActivity.this, "请您先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_buy = (TextView) findViewById(R.id.tv_buy);
        tv_buy.setEnabled(false);
        tv_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    requestBuy();
            }
        });

        ask = (TextView) findViewById(R.id.ask);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出一个PopWindow
                getPopWindowInstance();
                int[] location = new int[2];
                v.getLocationInWindow(location);
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                mPopupWindow.showAtLocation(v, Gravity.LEFT | Gravity.BOTTOM, location[0], v.getHeight() + getWindow().getDecorView().getHeight() - wm.getDefaultDisplay().getHeight());
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
    //添加收藏夹
    private void addtoFavorite() {
        if(UserConfig.isClientLogined()) {
            Map<String, String> args = new FacadeArgs.MapBuilder().build();
            args.put("MERCHANDISEID", GUID);
            args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());

            FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "AddMyFavour", args);
            protocol.withToken(FacadeToken.getInstance().getAuthToken());
            Message message = new Message.MessageBuilder()
                    .withProtocol(protocol)
                    .build();
            FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
                @Override
                public void onTokenTimeout(Message msg) {
                    Log.e("addtofarvite", "ERROR");
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        String response = responseProtocol.getResponse();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String returnMsg = jsonObject.getString("success");
                            if ("yes".equals(returnMsg)) {
                                Toast.makeText(MedicinalDetailActivity.this, "加入收藏夹成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MedicinalDetailActivity.this, "加入收藏夹失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MedicinalDetailActivity.this, "加入收藏夹失败", Toast.LENGTH_SHORT).show();
                        Log.e("addtofarvite", errorMsg.toString() + "====" + errorMsg.msg);
                    }
                }
            });
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }

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


    private List<WeiShopEntity> result;

    //药品详情信息数据请求
    private void requestStoreData() {

        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("GUID", GUID).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsInfo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder().withProtocol(protocol).build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message message) {
                Log.e("GetStoreData1", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                SparseArray<ADBaseControl> controls = new SparseArray<ADBaseControl>();
                if (errorMsg == null) {
                    tv_buy.setEnabled(true);
                    tv_favorite.setEnabled(true);
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Log.i("药品详情数据",response);
                    data = new ArrayList<TestEntity>();
                    if (response != null) {
                        try {
                            JSONArray jsonArray2 = new JSONArray(response);
                            if (jsonArray2.length() != 0) {
                                Gson gson = new Gson();
                                result = gson.fromJson(response, new TypeToken<List<WeiShopEntity>>() {
                                }.getType());
                                weiShopEntity = result.get(0);
                                List<GoodSpicsEntity> goodspics = weiShopEntity.getGOODSPICS();
                                List<ADPagerEntity> adPagerEntities = new ArrayList<ADPagerEntity>();
                                for (int i = 0; i < goodspics.size(); i++) {
                                    adPagerEntities.add(new ADPagerEntity("", "", goodspics.get(i).getURL()));
                                }
                                int count = 0;
                                controls.append(count, new ADPagerControl().bindModel(adPagerEntities));
                                count++;
                                controls.append(count, new ADMedicinalDetailControl().bindModle(weiShopEntity.getSTORECOUNT(), weiShopEntity.getSHORTDESCRIPTION(), weiShopEntity.getNAME(), weiShopEntity.getUSERPRICE(), weiShopEntity.getSHOPADDRESS(), weiShopEntity.getSHOPNAME()));
                                count++;
                                controls.append(count, new ADIllustrationControl().bindModel("正品保证", "免运费", "货到付款"));
                                if(!flag) {
                                    count++;
                                    controls.append(count, new ADGroupNameControls().bindModel("附近药店", false));
                                    count++;
                                    //If it is from the vicinity of the details of the pharmacy to enter, do not show a nearby pharmacy module
                                    if (nearResult != null && !("".equals(nearResult))) {
                                        for (int i = 0; i < nearResult.size(); i++) {
                                            controls.append(count, new ADStoreControls().bindModel(nearResult.get(i).getTOTLESALEDCOUNT(), nearResult.get(i).getADDRESS(), nearResult.get(i).getPRICE(), nearResult.get(i).getSHOPNAME(), nearResult.get(i).getDISTANCE(), nearResult.get(i).getMERCHANDISEID()));
                                        }
                                    }
                                    count++;
                                    controls.append(count, new ADMoreControl());
                                }
                                AddToHistory(weiShopEntity);
                            } else {
                                //show emptyPage
                                Log.i("-----------","药品详情数据为空");
                              controls.append(0,new ADErrorControl().bindModel("该药店未能查询到该药品"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MedicinalDetailActivity.this, "药品不存在", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //error page
                    Log.e("GetStoreData2", errorMsg.msg);
                    controls.append(0, new ADErrorControl().bindModel("该药品不存在"));

                }
                MedicinalDetailAdapter medicinalDetailAdapter2 = new MedicinalDetailAdapter(MedicinalDetailActivity.this);
                medicinalDetailAdapter2.bindData(controls);
                recyclerView.setAdapter(medicinalDetailAdapter2);
            }
        });
    }

    //addTo shopCar
    private void requestBuy() {
        if (UserConfig.isClientLogined()) {

            int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
            Map<String, String> args = new FacadeArgs.MapBuilder().build();
            args.put("GOODSGUID", weiShopEntity.getGUID());
            args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
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
                    Log.e("InsertIntoCar",msg.msg);
                }

                @Override
                public void onResult(Message msg, Message errorMsg) {
                    if (errorMsg == null) {
                        ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                        String response = responseProtocol.getResponse();
                        if ("ERROE".equals(response)) {
                            Toast.makeText(MedicinalDetailActivity.this, "加入购物车异常", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MedicinalDetailActivity.this, "成功加入购物车", Toast.LENGTH_SHORT).show();
                            AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 1);
                        }
                    } else {
                        Log.e("InsertIntoCar", errorMsg.toString() + "====" + errorMsg.msg);
                    }
                }
            });
        }else{
            Toast.makeText(this,"请您先登录",Toast.LENGTH_SHORT).show();
        }
    }
    public void AddToHistory(WeiShopEntity weiShopEntity) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setShopName(weiShopEntity.getSHOPNAME());
        historyEntity.setImgUrl(weiShopEntity.getURL());
        historyEntity.setIsSelect(true);
        historyEntity.setMedicinalName(weiShopEntity.getNAME());
        historyEntity.setCurrentPrice(weiShopEntity.getUSERPRICE());
        historyEntity.setDiscountPrice(weiShopEntity.getMARKETPRICE());
        historyEntity.setSaleCount(weiShopEntity.getSTORECOUNT());
        historyEntity.setCommentCount(weiShopEntity.getSTORECOUNT());
        historyEntity.setGuid(weiShopEntity.getGUID());
        historyEntity.setShopIp(weiShopEntity.getSHOPID());
        HistoryDao historyDao = DBInterface.instance().openReadableDb().getHistoryDao();
        historyDao.insert(historyEntity);
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
        FrameLayout container = new FrameLayout(this);
        ListView lv = new ListView(this);
        lv.setBackgroundResource(R.color.window_grey_background);
        container.addView(lv, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        mPopupWindow = new PopupWindow(container, mScreenwidth / 3,
                LayoutHelper.WRAP_CONTENT);

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);
        List<String> datas = new ArrayList<String>();
        datas.add("咨询1");
        datas.add("咨询2");
        datas.add("咨询3");
        PopAdapter popAdapter = new PopAdapter(MedicinalDetailActivity.this, datas);
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
        public View
        getView(final int position, View convertView, ViewGroup parent) {
            PopWindowCell popWindowCell = new PopWindowCell(mContext);
            popWindowCell.setValue(mDdatas.get(position), "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg");
            return popWindowCell;
        }
    }

    //获取当前位置的经纬度
    private void initMyLocation() {
        mAMapLocationManager = LocationManagerProxy.getInstance(this);
        mAMapLocationManager.setGpsEnable(true);
            /*
             * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
        // Location API定位采用GPS和网络混合定位方式，时间最短是2000毫秒
        mAMapLocationManager.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 50, new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        myLocation = null;
                        if (aMapLocation != null) {
                            AMapLocException exception = aMapLocation.getAMapException();
                            if (exception == null || exception.getErrorCode() == 0) {
                                Map<String, String> args = new FacadeArgs.MapBuilder()
                                        .put("MERCHANDISEID", GUID)
                                        .put("LONGITUDE", aMapLocation.getLongitude() + "")
                                        .put("LATITUDE", aMapLocation.getLatitude() + "")
                                        .put("PAGE", "1")
                                        .put("COUNT", "5")
                                        .build();
                                FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "SaleInShop", args);
                                protocol.withToken(FacadeToken.getInstance().getAuthToken());
                                Message message = new Message.MessageBuilder()
                                        .withProtocol(protocol)
                                        .build();
                                FacadeClient.request(MedicinalDetailActivity.this, message, new FacadeClient.FacadeCallback() {
                                    @Override
                                    public void onTokenTimeout(Message msg) {
                                        Log.e("附近有售数据", "ERROR");
                                    }

                                    @Override
                                    public void onResult(Message msg, Message errorMsg) {
                                        if (errorMsg == null) {
                                            ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                                            String response = responseProtocol.getResponse();
                                            Log.i("附近有售数据----",response);
                                            Gson gson = new Gson();
                                            nearResult = gson.fromJson(response, new TypeToken<List<NearByOnSaleEntity>>() {
                                            }.getType());
                                            requestStoreData();
                                        } else {
                                            Log.e("附近有售数据", errorMsg.msg);
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }
}

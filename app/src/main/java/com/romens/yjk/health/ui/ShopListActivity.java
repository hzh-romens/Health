package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.model.GoodListEntity;
import com.romens.yjk.health.ui.adapter.ShopListAdapter;
import com.romens.yjk.health.ui.adapter.ShopListNoPictureAdapter;
import com.romens.yjk.health.ui.components.ReviseRadioButton;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by AUSU on 2015/9/23.
 */
public class ShopListActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back, other;
    private EditText et_search;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ShopListAdapter shopListAdapter;
    private RelativeLayout btn_switch;
    private boolean Expand = false;
    private String choice;
    private int PAGE = 1;
    private final int COUNT = 10;
    private String GUID, name;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private RadioGroup radioGroup;
    private RadioButton rb_all;
    private ReviseRadioButton rb_price, rb_sale;
    private int PRICE_FLAG = 0;
    private int SALE_FLAG = 5;
    private String EDITEXT = "";
    private String KEY = "";
    //Used to distinguish between Adapter signs
    private int ADAPTERFLAG = 1;
    private ShopListNoPictureAdapter shopListNoPictureAdapter;
    private boolean SEARCHDEFAULT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        initIntentValue();
        initView();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                Paint paint = new Paint();
                paint.setColor(getResources().getColor(R.color.md_grey_500));
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = parent.getChildAt(i);
                    float x = childAt.getWidth() + childAt.getX();
                    float y = childAt.getHeight() + childAt.getY();
                    c.drawLine(childAt.getX(), y, x, y, paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = AndroidUtilities.dp(1);
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        UIHelper.updateSwipeRefreshProgressBarTop(this, refreshLayout);
        shopListNoPictureAdapter = new ShopListNoPictureAdapter(this);
        shopListAdapter = new ShopListAdapter(this);
        recyclerView.setAdapter(shopListAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                PAGE = 1;
                if ((EDITEXT == null || "".equals(EDITEXT)) && (KEY == null || "".equals(KEY))) {
                    requestData();
                } else {
                    SEARCHDEFAULT = true;
                    requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                }

            }
        });
        refreshLayout.setRefreshing(true);
        requestData();
        setListener();
    }

    private void initIntentValue() {
        if (getIntent().getStringExtra("guid") != null) {
            GUID = getIntent().getStringExtra("guid");
        }
        if (getIntent().getStringExtra("key_id") != null) {
            GUID = getIntent().getStringExtra("key_id");
        }
        if (getIntent().getStringExtra("key_name") != null) {
            name = getIntent().getStringExtra("key_name");
            EDITEXT = name;
        }

    }

    private void setListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == shopListAdapter.getItemCount()) {
                    PAGE++;
                    if ((EDITEXT == null || "".equals(EDITEXT)) && (KEY == null || "".equals(KEY))) {
                        requestData();
                    } else {
                        SEARCHDEFAULT = true;
                        requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    //BindData
    private void bindData(List<GoodListEntity> result) {
        if (ADAPTERFLAG == 1) {
            shopListAdapter.BindData(result);
        } else if (ADAPTERFLAG == 2) {
            shopListNoPictureAdapter.BindData(result);
        }
        refreshLayout.setRefreshing(false);
    }


    private void initView() {
        btn_switch = (RelativeLayout) findViewById(R.id.btn_switch);
        back = (ImageView) findViewById(R.id.back);
        other = (ImageView) findViewById(R.id.other);
        et_search = (EditText) findViewById(R.id.et_search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        rb_all = (RadioButton) findViewById(R.id.rb_all);
        rb_price = (ReviseRadioButton) findViewById(R.id.rb_price);
        rb_sale = (ReviseRadioButton) findViewById(R.id.rb_sale);
        rb_price.setOnClickListener(this);
        rb_sale.setOnClickListener(this);
        rb_all.setOnClickListener(this);
        back.setOnClickListener(this);
        other.setOnClickListener(this);
        btn_switch.setOnClickListener(this);
        et_search.setOnKeyListener(onKeyListener);
    }

    @Override
    public void onClick(View v) {
        PAGE = 1;
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.other:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.rb_all:
                KEY = "default";
                requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                break;
            case R.id.rb_price:
                rb_sale.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow), null);
                SALE_FLAG = 5;
                if (PRICE_FLAG == 0) {
                    KEY = "priceDown";
                    requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                    rb_price.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    PRICE_FLAG = 1;
                    return;
                }
                if (PRICE_FLAG == 1) {
                    KEY = "priceUp";
                    requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                    rb_price.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_up), null);
                    PRICE_FLAG = 2;
                    return;
                }
                if (PRICE_FLAG == 2) {
                    KEY = "priceDown";
                    rb_price.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                    PRICE_FLAG = 1;
                    return;
                }
                break;
            case R.id.rb_sale:
                rb_price.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow), null);
                PRICE_FLAG = 0;
                if (SALE_FLAG == 5) {
                    KEY = "saleDown";
                    requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                    rb_sale.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    SALE_FLAG = 6;
                    return;
                }
                if (SALE_FLAG == 6) {
                    KEY = "saleUp";
                    rb_sale.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_up), null);
                    SALE_FLAG = 7;
                    requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                    return;
                }
                if (SALE_FLAG == 7) {
                    KEY = "saleDown";
                    SALE_FLAG = 6;
                    rb_sale.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                    return;
                }
                break;
            //Switch Adapter;
            case R.id.btn_switch:
                if (ADAPTERFLAG == 1) {
                    ADAPTERFLAG = 2;
                    recyclerView.setAdapter(shopListNoPictureAdapter);
                } else if (ADAPTERFLAG == 2) {
                    ADAPTERFLAG = 1;
                    recyclerView.setAdapter(shopListAdapter);
                }
                requestData();
                break;

        }
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                /*隐藏软键盘*/
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                SEARCHDEFAULT = false;
                EDITEXT = et_search.getText().toString();
                if (name.equals(EDITEXT) || "".equals(EDITEXT) || EDITEXT == null) {
                    Toast.makeText(ShopListActivity.this, "请输入你需要搜索的商品", Toast.LENGTH_SHORT).show();
                }
                requestSearchData(EDITEXT, KEY, SEARCHDEFAULT);
                return true;
            }
            return false;
        }
    };

    //获取用户商品列表
    private void requestData() {
        Map<String, Object> args = new HashMap<>();
        args.put("GUID", GUID);
        args.put("PAGE", PAGE);
        args.put("COUNT", COUNT);
        args.put("SORTFIELD", "default");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ShopListActivity.this, "数据获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {

                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Gson gson = new Gson();
                    List<GoodListEntity> result = new ArrayList<GoodListEntity>();
                    result = gson.fromJson(response, new TypeToken<List<GoodListEntity>>() {
                    }.getType());
                    if (PAGE == 1) {
                        bindData(result);
                    } else {
                        if (ADAPTERFLAG == 1) {
                            shopListAdapter.addData(result);
                        } else if (ADAPTERFLAG == 2) {
                            shopListNoPictureAdapter.addData(result);
                        }
                    }

                } else {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(ShopListActivity.this, "数据为空", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //获取用户商品列表
    private void requestSearchData(String key, String sortfiled, boolean searchType) {
        refreshLayout.setRefreshing(true);
        Map<String, Object> args = new HashMap<>();
        args.put("GUID", GUID);
        args.put("PAGE", PAGE);
        args.put("COUNT", COUNT);
        if (key != null && !("".equals(key))) {
            args.put("KEY", key);
        }
        if (sortfiled != null && !("".equals(sortfiled))) {
            args.put("SORTFIELD", sortfiled);
        } else {
            args.put("SORTFIELD", "default");
        }
        FacadeProtocol protocol;
        if (key != null && !("".equals(key)) && SEARCHDEFAULT) {
            protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        } else if (key != null && !("".equals(key)) && !SEARCHDEFAULT) {
            protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "SearchSort", args);
        } else {
            protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        }

        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ShopListActivity.this, "数据获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {

                if (errorMsg == null) {
                    refreshLayout.setRefreshing(false);
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Log.i("Edittext不为空的数据----", response);
                    Gson gson = new Gson();
                    List<GoodListEntity> result = gson.fromJson(response, new TypeToken<List<GoodListEntity>>() {
                    }.getType());
                    if (PAGE == 1) {
                        bindData(result);
                    } else {
                        if (ADAPTERFLAG == 1) {
                            shopListAdapter.addData(result);
                        } else if (ADAPTERFLAG == 2) {
                            shopListNoPictureAdapter.addData(result);
                        }
                    }

                } else {
                    refreshLayout.setRefreshing(false);
                    Log.i("errorMsg---", errorMsg.msg);
                    shopListAdapter.BindData(null);
                    Toast.makeText(ShopListActivity.this, "数据为空", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}

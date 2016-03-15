package com.romens.yjk.health.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
import com.romens.yjk.health.R;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.GoodListEntity;
import com.romens.yjk.health.ui.adapter.ShopListAdapter;
import com.romens.yjk.health.ui.adapter.ShopListNoPictureAdapter;
import com.romens.yjk.health.ui.components.ReviseRadioButton;
import com.romens.yjk.health.ui.fragment.ShoppingServiceFragment;
import com.romens.yjk.health.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by AUSU on 2015/9/23.
 */
public class ShopListActivity extends BaseActivity implements View.OnClickListener {
    private ImageView other;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ShopListAdapter shopListAdapter;
    private RelativeLayout switchButton;
    private boolean Expand = false;
    private String choice;
    private String guid, name;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private RadioGroup radioGroup;
    private RadioButton allButton;
    private ReviseRadioButton priceButton, saleButton;
    private String editTextValue = "";
    private String keyValue = "";
    private ShopListNoPictureAdapter shopListNoPictureAdapter;
    private int page = 0;
    private final int COUNT = 10;
    private static int ADAPTERFLAG = 1;
    private static int PRICE_FLAG = 0;
    private static int SALE_FLAG = 5;
    private static boolean SEARCHDEFAULT = true;
    private ActionBar actionBar;

    private int goodsFlag = GoodsFlag.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoppingServiceFragment.instance(getSupportFragmentManager());
        setContentView(R.layout.activity_shop_list, R.id.action_bar);
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
        //2016-03-15 周立思 新增适配器监听事件
        shopListAdapter = new ShopListAdapter(this, new ShopListAdapter.Delegate() {
            @Override
            public void onItemClick(String goodsID) {
                UIOpenHelper.openMedicineActivity(ShopListActivity.this, goodsID, goodsFlag);
            }

            @Override
            public void onItemAddShoppingCart(String goodsID, double price) {
                ShoppingServiceFragment.instance(getSupportFragmentManager())
                        .tryAddToShoppingCart(goodsID, price, goodsFlag);
            }
        });
        recyclerView.setAdapter(shopListAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                page = 0;
                if ((editTextValue == null || "".equals(editTextValue)) && (keyValue == null || "".equals(keyValue))) {
                    requestData();
                } else {
                    SEARCHDEFAULT = true;
                    requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
                }

            }
        });
        refreshLayout.setRefreshing(true);
        requestData();
        setListener();
    }

    private void initIntentValue() {
        Intent intent = getIntent();
        if (intent.hasExtra(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG)) {
            goodsFlag = intent.getIntExtra(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, GoodsFlag.NORMAL);
        }

        if (intent.getStringExtra("guid") != null) {
            guid = intent.getStringExtra("guid");
        }
        if (intent.getStringExtra("key_id") != null) {
            guid = intent.getStringExtra("key_id");
        }
        if (intent.getStringExtra("key_name") != null) {
            name = intent.getStringExtra("key_name");
            editTextValue = name;
        }

    }

    private void setListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == shopListAdapter.getItemCount()) {
                    page++;
                    if ((editTextValue == null || "".equals(editTextValue)) && (keyValue == null || "".equals(keyValue))) {
                        requestData();
                    } else {
                        SEARCHDEFAULT = true;
                        requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
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
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        switchButton = (RelativeLayout) findViewById(R.id.btn_switch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        allButton = (RadioButton) findViewById(R.id.rb_all);
        priceButton = (ReviseRadioButton) findViewById(R.id.rb_price);
        saleButton = (ReviseRadioButton) findViewById(R.id.rb_sale);
        priceButton.setOnClickListener(this);
        saleButton.setOnClickListener(this);
        allButton.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        actionBar.setBackButtonImage(R.drawable.ic_arrow_back_white_24dp);
        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem searchItem = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true, true);
        searchItem.getSearchField().setHint("请输入药品名称...");
        searchItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

            @Override
            public boolean canCollapseSearch() {
                return false;
            }

            @Override
            public void onSearchExpand() {

            }

            @Override
            public void onSearchCollapse() {

            }

            @Override
            public void onSearchPressed(EditText var1) {
                SEARCHDEFAULT = false;
                editTextValue = var1.getText().toString();
                if (name.equals(editTextValue) || "".equals(editTextValue) || editTextValue == null) {
                    Toast.makeText(ShopListActivity.this, "请输入你需要搜索的商品", Toast.LENGTH_SHORT).show();
                }
                requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
            }
        });
        if (goodsFlag == GoodsFlag.MEDICARE) {
            actionBar.setTitle(name + "(医保)");
        } else {
            actionBar.setTitle(name);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        page = 0;
        switch (v.getId()) {
//            case R.id.other:
//                startActivity(new Intent(this, HistoryActivity.class));
//                break;
            case R.id.rb_all:
                keyValue = "default";
                requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
                break;
            case R.id.rb_price:
                saleButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow), null);
                SALE_FLAG = 5;
                if (PRICE_FLAG == 0) {
                    keyValue = "priceDown";
                    requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
                    priceButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    PRICE_FLAG = 1;
                    return;
                }
                if (PRICE_FLAG == 1) {
                    keyValue = "priceUp";
                    requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
                    priceButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_up), null);
                    PRICE_FLAG = 2;
                    return;
                }
                if (PRICE_FLAG == 2) {
                    keyValue = "priceDown";
                    priceButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
                    PRICE_FLAG = 1;
                    return;
                }
                break;
            case R.id.rb_sale:
                priceButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow), null);
                PRICE_FLAG = 0;
                if (SALE_FLAG == 5) {
                    keyValue = "saleDown";
                    requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
                    saleButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    SALE_FLAG = 6;
                    return;
                }
                if (SALE_FLAG == 6) {
                    keyValue = "saleUp";
                    saleButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_up), null);
                    SALE_FLAG = 7;
                    requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
                    return;
                }
                if (SALE_FLAG == 7) {
                    keyValue = "saleDown";
                    SALE_FLAG = 6;
                    saleButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_arrow_down), null);
                    requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
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

//    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
//
//        @Override
//        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//                /*隐藏软键盘*/
//                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (inputMethodManager.isActive()) {
//                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
//                }
//                SEARCHDEFAULT = false;
//                // editTextValue = searchEditor.getText().toString();
//                if (name.equals(editTextValue) || "".equals(editTextValue) || editTextValue == null) {
//                    Toast.makeText(ShopListActivity.this, "请输入你需要搜索的商品", Toast.LENGTH_SHORT).show();
//                }
//                requestSearchData(editTextValue, keyValue, SEARCHDEFAULT);
//                return true;
//            }
//            return false;
//        }
//    };

    //获取用户商品列表
    private void requestData() {
        Map<String, Object> args = new HashMap<>();
        args.put("GUID", guid);
        args.put("PAGE", page);
        args.put("COUNT", COUNT);
        args.put("SORTFIELD", "default");
        args.put("FLAG", GoodsFlag.checkFlagForArg(goodsFlag));
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
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
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    List<LinkedTreeMap<String, String>> response = responseProtocol.getResponse();
                    List<GoodListEntity> result = new ArrayList<GoodListEntity>();
                    for (LinkedTreeMap<String, String> item : response) {
                        GoodListEntity entity = GoodListEntity.toEntity(item);
                        result.add(entity);
                    }
                    if (page == 0) {
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
        args.put("GUID", guid);
        args.put("PAGE", page);
        args.put("COUNT", COUNT);
        if (key != null && !("".equals(key))) {
            args.put("KEY", key);
        }
        if (sortfiled != null && !("".equals(sortfiled))) {
            args.put("SORTFIELD", sortfiled);
        } else {
            args.put("SORTFIELD", "default");
        }
        args.put("FLAG", GoodsFlag.checkFlagForArg(goodsFlag));
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
                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
                }))
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
                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    List<LinkedTreeMap<String, String>> response = responseProtocol.getResponse();
                    List<GoodListEntity> result = new ArrayList<GoodListEntity>();
                    for (LinkedTreeMap<String, String> item : response) {
                        GoodListEntity entity = GoodListEntity.toEntity(item);
                        result.add(entity);
                    }
                    if (page == 0) {
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
                    shopListAdapter.BindData(null);
                    Toast.makeText(ShopListActivity.this, "数据为空", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}

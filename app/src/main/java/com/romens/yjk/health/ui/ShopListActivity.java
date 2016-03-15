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

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.ActionBar.ActionBarMenuItem;
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
    public static final String ARGUMENTS_KEY_FLAG = "key_flag";
    private ShopListNoPictureAdapter shopListNoPictureAdapter;
    private ReviseRadioButton priceButton, saleButton;
    private LinearLayoutManager linearLayoutManager;
    private static boolean SEARCHDEFAULT = true;
    private SwipeRefreshLayout refreshLayout;
    private ShopListAdapter shopListAdapter;
    private RelativeLayout switchButton;
    private static int ADAPTERFLAG = 1;
    private RecyclerView recyclerView;
    private String editTextValue = "";
    private static int PRICE_FLAG = 0;
    private static int SALE_FLAG = 5;
    private boolean Expand = false;
    private RadioButton allButton;
    private RadioGroup radioGroup;
    private String keyValue = "";
    private final int COUNT = 10;
    private ActionBar actionBar;
    private int lastVisibleItem;
    private String guid, name;
    private ImageView other;
    private String choice;
    private int page = 0;
    private int flag = 0;


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
        flag = intent.getIntExtra(ARGUMENTS_KEY_FLAG, 0);

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
        actionBar.setTitle(name);
        actionBar.setBackgroundResource(R.color.theme_primary);
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

    //获取用户商品列表
    private void requestData() {
        Map<String, Object> args = new HashMap<>();
        args.put("GUID", guid);
        args.put("PAGE", page);
        args.put("COUNT", COUNT);
        args.put("SORTFIELD", "default");
        args.put("FLAG", "" + flag);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(ShopListActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            List<GoodListEntity> result = new ArrayList<GoodListEntity>();
                            for (int i = 0; i < response.size(); i++) {
                                JsonNode jsonNode = response.get(i);
                                GoodListEntity entity = GoodListEntity.toEntity(jsonNode);
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
                }).build();
        ConnectManager.getInstance().request(this, connect);
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
        args.put("FLAG", "" + flag);
        FacadeProtocol protocol;
        if (key != null && !("".equals(key)) && SEARCHDEFAULT) {
            protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        } else if (key != null && !("".equals(key)) && !SEARCHDEFAULT) {
            protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "SearchSort", args);
        } else {
            protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        }

        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(ShopListActivity.class).withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        if (errorMessage == null) {
                            refreshLayout.setRefreshing(false);

                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            List<GoodListEntity> result = new ArrayList<GoodListEntity>();
                            for (int i = 0; i < response.size(); i++) {
                                JsonNode jsonNode = response.get(i);
                                GoodListEntity entity = GoodListEntity.toEntity(jsonNode);
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
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }
}

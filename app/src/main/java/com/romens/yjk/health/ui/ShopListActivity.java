package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.github.materialdrawer.model.DividerDrawerItem;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.viewholder.PaddingDividerItemDecoration;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.model.ChoiceEntity;
import com.romens.yjk.health.model.GoodListEntity;
import com.romens.yjk.health.model.GoodsEntity;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.adapter.ShopListAdapter;
import com.romens.yjk.health.ui.cells.PopWindowCell;
import com.romens.yjk.health.ui.utils.UIHelper;
import com.romens.yjk.health.ui.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


/**
 * Created by AUSU on 2015/9/23.
 */
public class ShopListActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back, other;
    private EditText et_search;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ShopListAdapter shopListAdapter;
    private boolean Expand = false;
    //  private TextView classify,sort,show,filter;
    private String choice;
    private int PAGE = 1;
    private final int COUNT = 10;
    private String GUID, name;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private RadioGroup radioGroup;
    private RadioButton rb_all, rb_price, rb_sale;
    private int PRICE_FLAG = 0;
    private int SALE_FLAG = 5;
    private String EDITEXT = "";
    private String KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
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

        initView();
        // et_search.setText(name);

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
                    requestSearchData(EDITEXT, KEY);
                }

            }
        });
        refreshLayout.setRefreshing(true);
        requestData();
        setListener();
    }

    private void setListener() {
        //sort.setOnClickListener(this);
        //show.setOnClickListener(this);
        //filter.setOnClickListener(this);
        //classify.setOnClickListener(this);
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
                        requestSearchData(EDITEXT, KEY);
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

    //绑定数据
    private void bindData(List<GoodListEntity> result) {
        shopListAdapter.BindData(result);
        refreshLayout.setRefreshing(false);
    }


    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        other = (ImageView) findViewById(R.id.other);
        et_search = (EditText) findViewById(R.id.et_search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        rb_all = (RadioButton) findViewById(R.id.rb_all);
        rb_price = (RadioButton) findViewById(R.id.rb_price);
        rb_sale = (RadioButton) findViewById(R.id.rb_sale);
        rb_price.setOnClickListener(this);
        rb_sale.setOnClickListener(this);
        rb_all.setOnClickListener(this);
        //  classify= (TextView) findViewById(R.id.classify);
        //  sort= (TextView) findViewById(R.id.sort);
        // show= (TextView) findViewById(R.id.show);
        // filter= (TextView) findViewById(R.id.filter);
        back.setOnClickListener(this);
        other.setOnClickListener(this);
        et_search.setOnKeyListener(onKeyListener);
    }

    @Override
    public void onClick(View v) {
        PAGE = 1;
        switch (v.getId()) {

//            case R.id.classify:
//                getPopWindowInstance();
//                //mPopupWindow.showAsDropDown(classify);
//            break;
//            case R.id.sort:
//                break;
//            case R.id.show:
//                break;
//            case R.id.filter:
//                break;
            case R.id.back:
                finish();
                break;
            case R.id.other:
                startActivity(new Intent(this, HistoryActivity.class));
                break;
            case R.id.rb_all:
                KEY = "default";
                requestSearchData(EDITEXT, KEY);
                break;
            case R.id.rb_price:
                if (PRICE_FLAG == 0) {
                    rb_price.setText("按价格从高到低");
                    KEY = "priceDown";
                    requestSearchData(EDITEXT, KEY);
                    PRICE_FLAG = 1;
                    return;
                }
                if (PRICE_FLAG == 1) {
                    rb_price.setText("按价格从低到高");
                    KEY = "priceUp";
                    requestSearchData(EDITEXT, KEY);
                    PRICE_FLAG = 2;
                    return;
                }
                if (PRICE_FLAG == 2) {
                    rb_price.setText("按价格从高到低");
                    KEY = "priceDown";
                    requestSearchData(EDITEXT, KEY);
                    PRICE_FLAG = 1;
                    return;
                }
                break;
            case R.id.rb_sale:
                if (SALE_FLAG == 5) {
                    KEY = "saleDown";
                    rb_sale.setText("按销量从高到低");
                    requestSearchData(EDITEXT, KEY);
                    SALE_FLAG = 6;
                    return;
                }
                if (SALE_FLAG == 6) {
                    rb_sale.setText("按销量从低到高");
                    KEY = "saleUp";
                    SALE_FLAG = 7;
                    requestSearchData(EDITEXT, KEY);
                    return;
                }
                if (SALE_FLAG == 7) {
                    rb_sale.setText("按销量从高到低");
                    KEY = "saleDown";
                    SALE_FLAG = 6;
                    requestSearchData(EDITEXT, KEY);
                    return;
                }
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

                EDITEXT = et_search.getText().toString();
                if ((EDITEXT == null || "".equals(EDITEXT)) && (KEY == null || "".equals(KEY))) {
                    Toast.makeText(ShopListActivity.this, "请输入查询信息", Toast.LENGTH_SHORT);
                } else {
                    requestSearchData(EDITEXT, KEY);
                }
                return true;
            }
            return false;
        }
    };

    //获取用户商品列表
    private void requestData() {
        Map<String, Object> args = new HashMap<>();
        int lastTime = DBInterface.instance().getDrugGroupDataLastTime();
        args.put("GUID", GUID);
        args.put("PAGE", PAGE);
        args.put("COUNT", COUNT);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetGoodsList", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
//        .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//        }))
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ShopListActivity.this, "数据获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {

                if (errorMsg == null) {
                    //  ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    //bindData(responseProtocol.getResponse());
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Log.i("商品烈表示数据---",response);
                    Gson gson = new Gson();
                    List<GoodListEntity> result = new ArrayList<GoodListEntity>();
                    result = gson.fromJson(response, new TypeToken<List<GoodListEntity>>() {
                    }.getType());
                    if (PAGE == 1) {
                        bindData(result);
                    } else {
                        shopListAdapter.addData(result);
                    }

                } else {
                    refreshLayout.setRefreshing(false);
                    Log.i("errorMsg---", errorMsg.msg);
                    Toast.makeText(ShopListActivity.this, "数据为空", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //获取用户商品列表
    private void requestSearchData(String key, String sortfiled) {
//        UserEntity clientUserEntity = UserConfig.getClientUserEntity();
//        Log.i("用户guid----", clientUserEntity.getGuid());
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
        }

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "SearchSort", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
//        .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//        }))
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ShopListActivity.this, "数据获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {

                if (errorMsg == null) {
                    //ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
                    //bindData(responseProtocol.getResponse());
                    refreshLayout.setRefreshing(false);
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    Gson gson = new Gson();
                    List<GoodListEntity> result = gson.fromJson(response, new TypeToken<List<GoodListEntity>>() {
                    }.getType());
                    if (PAGE == 1) {
                        bindData(result);
                    } else {
                        shopListAdapter.addData(result);
                    }

                } else {
                    refreshLayout.setRefreshing(false);
                    Log.i("errorMsg---", errorMsg.msg);
                    Toast.makeText(ShopListActivity.this, "数据为空", Toast.LENGTH_SHORT).show();
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
    private List<ChoiceEntity> choiceDatas;

    private void initPopWindow() {
        mScreenwidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        // 创建一个PopupWindow 并设置宽高
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
        FrameLayout container = new FrameLayout(this);
        container.setBackgroundColor(getResources().getColor(R.color.transparent));
        ListView lv = new ListView(this);
        lv.setChoiceMode(lv.CHOICE_MODE_SINGLE);
        lv.setBackgroundResource(R.color.title_background_grey);
        container.addView(lv, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        mPopupWindow = new PopupWindow(container, LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT);
        // #e0000000  半透明颜色
        mPopupWindow.setAnimationStyle(R.style.Float_Dialog);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);
        getChoiceData();
        final PopAdapter popAdapter = new PopAdapter(ShopListActivity.this);
        lv.setAdapter(popAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                for (int i = 0; i < choiceDatas.size(); i++) {
                    if (i != position) {
                        choiceDatas.get(i).setFlag(false);
                    }
                }
                choiceDatas.get(position).setFlag(!choiceDatas.get(position).isFlag());
                popAdapter.notifyDataSetChanged();
                Toast.makeText(ShopListActivity.this, choiceDatas.get(position).getChoice(), Toast.LENGTH_SHORT).show();
                choice = choiceDatas.get(position).getChoice();
                mPopupWindow.dismiss();
            }
        });

    }

    private void getChoiceData() {
        choiceDatas = new ArrayList<ChoiceEntity>();
        ChoiceEntity ce1 = new ChoiceEntity();
        ce1.setChoice("价格");
        ce1.setFlag(false);
        choiceDatas.add(ce1);
        ChoiceEntity ce2 = new ChoiceEntity();
        ce2.setChoice("销量");
        ce2.setFlag(false);
        choiceDatas.add(ce2);
        ChoiceEntity ce3 = new ChoiceEntity();
        ce3.setChoice("综合");
        ce3.setFlag(false);
        choiceDatas.add(ce3);
        ChoiceEntity ce4 = new ChoiceEntity();
        ce4.setChoice("距离");
        ce4.setFlag(false);
        choiceDatas.add(ce4);
        ChoiceEntity ce5 = new ChoiceEntity();
        ce5.setChoice("评论");
        ce5.setFlag(false);
        choiceDatas.add(ce5);
    }

    //popWindow的Adapter
    private class PopAdapter extends BaseAdapter {
        private Context mContext;
        private List<ChoiceEntity> mDdatas;

        public PopAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public int getCount() {
            if (choiceDatas != null) {
                return choiceDatas.size();
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
            convertView = View.inflate(mContext, R.layout.list_item_checktext, null);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
            tv_name.setText(choiceDatas.get(position).getChoice());
            if (choiceDatas.get(position).isFlag()) {
                iv.setBackground(mContext.getResources().getDrawable(R.drawable.choice_icon));
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(36));
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return convertView;
        }
    }

}

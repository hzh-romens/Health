package com.romens.yjk.health.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.cells.TextSettingsCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.model.DrugDetailEntity;
import com.romens.yjk.health.model.LocationEntity;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by anlc on 2015/10/17.
 * 药店详情页面
 */
public class DrugStoryDetailActivity extends DarkActionBarActivity {

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private DrugDetailAdapter adapter;
    private LocationEntity location;

    private DrugDetailEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail, R.id.action_bar);
        Intent intent = getIntent();
        location = (LocationEntity) intent.getSerializableExtra("locationEntity");
        setRow();
        actionBarEven();
        listView = (ListView) findViewById(R.id.drug_detail_listview);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.drug_detail_refershlayout);
        UIHelper.setupSwipeRefreshLayoutProgress(refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (location != null) {
                    needShowProgress("正在请求数据...");
                    requestShopDetail(location.getId());
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        adapter = new DrugDetailAdapter(this);
        listView.setAdapter(adapter);
        if (location != null) {
            needShowProgress("正在请求数据...");
            requestShopDetail(location.getId());
        }
//        initData();
    }

    private void initData() {
        entity = new DrugDetailEntity();
        entity.setName("平价大药房");
        entity.setAddress("安定门外大街");
        entity.setPhone("133333333333");
        entity.setImgUrl("http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg");
        entity.setDescription("绿色科技发送旅客地方三地警方蓝桑坤加拉塞克警方i两款手机isjl空间司法局绿色科技日了康师傅i绿色科技islfkjiew");
        entity.setIconUrl("http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg");
        entity.setStatusUrl("http://img3.imgtn.bdimg.com/it/u=3336547744,2633972301&fm=21&gp=0.jpg");
        adapter.setEntity(entity);
    }

    private void actionBarEven() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("药店详情");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });
    }

    //访问删除收藏
    private void requestShopDetail(final String shopId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("SHOPID", shopId);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "ShopDetail", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(DrugStoryDetailActivity.this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(DrugStoryDetailActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    Log.e("tag", "--drugdetail-->" + responseProtocol.getResponse());
                    try {
                        JSONObject object = new JSONObject(responseProtocol.getResponse());
                        entity = new DrugDetailEntity();
                        entity.setGuid(object.getString("GUID"));
                        entity.setName(object.getString("NAME"));
                        entity.setAddress(object.getString("ADDRESS"));
                        entity.setPhone(object.getString("PHONE"));
                        entity.setIconUrl(object.getString("PHARMCYLOGO"));
                        entity.setImgUrl(object.getString("PHARMCYPIC1"));
                        entity.setImgUrl2(object.getString("PHARMCYPIC2"));
                        entity.setImgUrl3(object.getString("PHARMCYPIC3"));

                        entity.setStatusUrl(object.getString("PHARMCYCER1"));
                        entity.setStatusUrl2(object.getString("PHARMCYCER2"));
                        entity.setStatusUrl3(object.getString("PHARMCYCER3"));
                        entity.setDescription(object.getString("DESCRIPTION"));
                        adapter.setEntity(entity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (errorMsg == null) {
                } else {
                    Log.e("reqGetAllUsers", "ERROR");
                    Log.e("tag", "--collect--ERROR-->" + errorMsg.msg);
                }
                needHideProgress();
            }
        });
    }

    private int rowCount;
    private int titleImgRow;
    private int addressRow;
    private int imgTitleRow;
    private int imgRow;
    private int statusTitleRow;
    private int statusImgRow;
    private int introduceTitleRow;
    private int introduceRow;
    private int entityNullRow;

    public void setRow() {
        rowCount = 0;
        if (entity != null) {
            titleImgRow = rowCount++;
            addressRow = rowCount++;
            imgTitleRow = rowCount++;
            imgRow = rowCount++;
            statusTitleRow = rowCount++;
            statusImgRow = rowCount++;
            introduceTitleRow = rowCount++;
            introduceRow = rowCount++;
        } else {
            entityNullRow = rowCount++;
//            titleImgRow = -1;
//            addressRow = rowCount++;
//            imgTitleRow = rowCount++;
//            imgRow = rowCount++;
//            statusTitleRow = rowCount++;
//            statusImgRow = rowCount++;
//            introduceTitleRow = rowCount++;
//            introduceRow = rowCount++;
        }
    }

    @Override
    protected String getActivityName() {
        return "药店详情";
    }

    class DrugDetailAdapter extends BaseAdapter {

        private Context context;
        private DrugDetailEntity entity;

        public void setEntity(DrugDetailEntity entity) {
            setRow();
            this.entity = entity;
            notifyDataSetChanged();
        }

        public DrugDetailAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == imgTitleRow || position == statusTitleRow || position == introduceTitleRow) {
                return 0;
            } else if (position == imgRow || position == statusImgRow) {
                return 1;
            } else if (position == titleImgRow) {
                return 2;
            } else if (position == addressRow) {
                return 3;
            } else if (position == introduceRow) {
                return 4;
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (entity == null) {
                if (convertView == null) {
                    convertView = new LinearLayout(context);
                }
                LinearLayout linearLayout = (LinearLayout) convertView;
                TextView contentText = new TextView(context);
                contentText.setBackgroundColor(Color.TRANSPARENT);
                contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                contentText.setSingleLine(true);
                contentText.setTextColor(0xff666666);
                contentText.setGravity(Gravity.CENTER_HORIZONTAL);
                contentText.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(80), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
                LinearLayout.LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
                contentText.setLayoutParams(infoViewParams);
                linearLayout.addView(contentText);
                contentText.setText("获取信息失败,请重试");
            } else {
                int type = getItemViewType(position);
                if (type == 1) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_drug_detail, null);
                    BackupImageView firstImg = (BackupImageView) convertView.findViewById(R.id.first_image);
                    BackupImageView secondImg = (BackupImageView) convertView.findViewById(R.id.second_image);
                    BackupImageView threeImg = (BackupImageView) convertView.findViewById(R.id.three_image);
                    if (position == imgRow) {
                        firstImg.setImageUrl(entity.getImgUrl(), "64_64", null);
                        String str = entity.getImgUrl2();
                        if (str != null && !str.equals("null")) {
                            secondImg.setImageUrl(str, "64_64", null);
                        }
                        str = entity.getImgUrl3();
                        if (str != null && !str.equals("null")) {
                            threeImg.setImageUrl(str, "64_64", null);
                        }
                    } else if (position == statusImgRow) {
                        firstImg.setImageUrl(entity.getStatusUrl(), "64_64", null);
                        String str = entity.getStatusUrl2();
                        if (str != null && !str.equals("null")) {
                            secondImg.setImageUrl(entity.getStatusUrl2(), "64_64", null);
                        }
                        str = entity.getStatusUrl3();
                        if (str != null && !str.equals("null")) {
                            threeImg.setImageUrl(entity.getStatusUrl3(), "64_64", null);
                        }
                    }
                } else if (type == 0) {
                    convertView = new TextSettingsCell(context);
                    TextSettingsCell cell = (TextSettingsCell) convertView;
                    cell.setBackgroundColor(0x99e8e8e8);
                    cell.setTextColor(Color.BLACK);
                    if (position == imgTitleRow) {
                        cell.setText("药店实景", true);
                    } else if (position == statusTitleRow) {
                        cell.setText("药店资质", true);
                    } else if (position == introduceTitleRow) {
                        cell.setText("药店介绍", true);
                    }
                } else if (type == 2) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_drug_detail_titleimg, null);
                    BackupImageView titleImg = (BackupImageView) convertView.findViewById(R.id.title_image);
                    titleImg.setImageUrl(entity.getIconUrl(), "64_64", null);
                    TextView titleName = (TextView) convertView.findViewById(R.id.title_name);
                    titleName.setText(entity.getName());
                } else if (type == 3) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item_drug_detail_address, null);
                    TextView addressTitle = (TextView) convertView.findViewById(R.id.drug_detail_address_title);
                    addressTitle.setText(entity.getAddress());
                    TextView phoneText = (TextView) convertView.findViewById(R.id.drug_detail_address_phone);
                    String str = entity.getPhone();
                    if (str != null && !str.equals("null")) {
                        phoneText.setText(entity.getPhone());
                    }
                } else if (type == 4) {
                    convertView = new LinearLayout(context);
                    LinearLayout linearLayout = (LinearLayout) convertView;
                    TextView contentText = new TextView(context);
                    contentText.setBackgroundColor(Color.TRANSPARENT);
                    contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    contentText.setTextColor(0xff666666);
                    contentText.setGravity(Gravity.CENTER_VERTICAL);
                    contentText.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(50), AndroidUtilities.dp(4));
                    LinearLayout.LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
                    contentText.setLayoutParams(infoViewParams);
                    linearLayout.addView(contentText);
                    contentText.setText(entity.getDescription());
                }
            }
            return convertView;
        }
    }
}

package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
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
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.CitysDao;
import com.romens.yjk.health.db.entity.AllOrderEntity;
import com.romens.yjk.health.db.entity.CitysEntity;
import com.romens.yjk.health.model.EvaluateDatailsEntity;
import com.romens.yjk.health.ui.cells.FlexibleRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/9/23.
 * 订单评价详情页面
 */
public class OrderEvaluateDetailActivity extends BaseActivity {

    //    private BackupImageView leftImageView;
    private TextView titleTextView;
    //    private TextView specTextView;
    private TextView moneyTextView;
    private TextView dateTextView;
    private TextView countTextView;

    private FlexibleRatingBar qualityRatingBar;
    private FlexibleRatingBar speedRatingBar;
    private TextView opinionTextView;

    private String userGuid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        setContentView(R.layout.activity_order_evaluate_detail, R.id.action_bar);
        actionBarEvent();
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        AllOrderEntity entity = (AllOrderEntity) intent.getSerializableExtra("evaluateDetailEntity");
        if (null != entity) {
            Log.e("tag", "---entity-->" + entity.getGoodsName() + ":" + entity.getOrderPrice());
            requestAssessDetail(userGuid, entity.getOrderId());
            titleTextView.setText(entity.getGoodsName());
            moneyTextView.setText("￥" + entity.getOrderPrice());
            dateTextView.setText(entity.getCreateDate());
        }
    }

    private void viewSetData(EvaluateDatailsEntity evaluateDatailsEntity) {
        String qualiStr = evaluateDatailsEntity.getQualityStar();
        int qualiLevel = 0;
        if (qualiStr != null && !qualiStr.equals("")) {
            qualiLevel = Integer.getInteger(qualiStr);
        }
        String dileveryStr = evaluateDatailsEntity.getDileveryStar();
        int dileveryLevel = 0;
        if (dileveryStr != null && !dileveryStr.equals("")) {
            dileveryLevel = Integer.getInteger(dileveryStr);
        }
        qualityRatingBar.setRating(qualiLevel);
        speedRatingBar.setRating(dileveryLevel);
        opinionTextView.setText(evaluateDatailsEntity.getAdvice());
        Log.e("tag", "--evaluateDeatil-->" + qualiLevel + "::" + dileveryLevel + "::" + evaluateDatailsEntity.getAdvice());
    }

    //访问商品评价信息
    private void requestAssessDetail(String userGuid, String merchandiseId) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("MERCHANDISEID", merchandiseId);
        args.put("USERGUID", userGuid);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserAssessment", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
                .build();
        FacadeClient.request(OrderEvaluateDetailActivity.this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(OrderEvaluateDetailActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    setData(responseProtocol.getResponse());
                    Log.e("tag", "response---evaluateDetail->" + responseProtocol.getResponse());
                }
                if (errorMsg == null) {
                } else {
                    Log.e("tag", "evaluateDetail_---error->" + errorMsg.msg);
                    Log.e("reqGetAllUsers", "ERROR");
                }
            }
        });
    }

    private void setData(String response) {
        Log.e("tag", "--setData->" + response + "-->");
        if (response == null || response.equals("")) {
            return;
        }
        try {
            Log.e("tag", "--setData->" + response + "-->");
            JSONObject item = new JSONObject(response);
            EvaluateDatailsEntity entity = new EvaluateDatailsEntity();
            entity.setAdvice(item.getString("ADVICE"));
            entity.setIsAppend(item.getString("ISAPPEND"));
            entity.setAssessData(item.getString("ASSESSDATE"));
            entity.setDileveryStar(item.getString("DILEVERYSTAR"));
            entity.setQualityStar(item.getString("QUALITYSTAR"));
            viewSetData(entity);
        } catch (JSONException e) {
            Log.e("tag", "--setData-json_error->");
            e.printStackTrace();
        }
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("评价详情");
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

    private void initView() {
        titleTextView = (TextView) findViewById(R.id.order_title);
        moneyTextView = (TextView) findViewById(R.id.order_money);
        dateTextView = (TextView) findViewById(R.id.order_date);
        countTextView = (TextView) findViewById(R.id.order_count);

        qualityRatingBar = (FlexibleRatingBar) findViewById(R.id.order_evaluate_quality_ratingbar);
        speedRatingBar = (FlexibleRatingBar) findViewById(R.id.order_evaluate_speed_ratingbar);
        opinionTextView = (TextView) findViewById(R.id.order_evaluate_opinion_text);
    }
}

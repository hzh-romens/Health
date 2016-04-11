package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.dao.OrderDao;
import com.romens.yjk.health.db.entity.OrderEntity;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.FlexibleRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by anlc on 2015/9/23.
 * 编辑订单评价页面
 */
public class OrderEvaluateActivity extends DarkActionBarActivity {
    public static final String ARGUMENT_KEY_ORDER_ENTITY = "key_order_entity";

    private BackupImageView leftImageView;
    private TextView titleTextView;
    //    private TextView specTextView;
    private TextView moneyTextView;
    private TextView dateTextView;
    private TextView countTextView;

    private FlexibleRatingBar qualityRatingBar;
    private FlexibleRatingBar speedRatingBar;
    private EditText opinionEditText;
    private Button submitBut;
    private OrderEntity entity;

    private int qualityEvaluateLevel;
    private int speedEvaluateLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_evaluate, R.id.action_bar);

        Intent intent = getIntent();
        String orderId=intent.getStringExtra(ARGUMENT_KEY_ORDER_ENTITY);
        OrderDao orderDao = DBInterface.instance().openReadableDb().getOrderDataDao();
        entity=orderDao.queryBuilder().where(OrderDao.Properties.Id.eq(orderId)).unique();

        actionBarEvent();
        initView();
    }

    private void initView() {
        titleTextView = (TextView) findViewById(R.id.order_title);
        moneyTextView = (TextView) findViewById(R.id.order_money);
        dateTextView = (TextView) findViewById(R.id.order_date);
        countTextView = (TextView) findViewById(R.id.order_count);
        leftImageView = (BackupImageView) findViewById(R.id.order_img);

        qualityRatingBar = (FlexibleRatingBar) findViewById(R.id.order_evaluate_quality_ratingbar);
        qualityRatingBar.setStepSize(0.5f);
        qualityRatingBar.setColorFillOff(0xffffffff);
        qualityRatingBar.setColorFillOn(0xfff9a825);
        qualityRatingBar.setColorFillPressedOff(0xffffffff);
        qualityRatingBar.setColorFillPressedOn(0xfff9a825);
        qualityRatingBar.setColorOutlineOff(0xfff57f17);
        qualityRatingBar.setColorOutlineOn(0xfff57f17);
        qualityRatingBar.setColorOutlinePressed(0xfff57f17);
        qualityRatingBar.setStrokeWidth(AndroidUtilities.dp(2));

        speedRatingBar = (FlexibleRatingBar) findViewById(R.id.order_evaluate_speed_ratingbar);
        speedRatingBar.setStepSize(0.5f);
        speedRatingBar.setColorFillOff(0xffffffff);
        speedRatingBar.setColorFillOn(0xfff9a825);
        speedRatingBar.setColorFillPressedOff(0xffffffff);
        speedRatingBar.setColorFillPressedOn(0xfff9a825);
        speedRatingBar.setColorOutlineOff(0xfff57f17);
        speedRatingBar.setColorOutlineOn(0xfff57f17);
        speedRatingBar.setColorOutlinePressed(0xfff57f17);
        speedRatingBar.setStrokeWidth(AndroidUtilities.dp(2));

        opinionEditText = (EditText) findViewById(R.id.order_evaluate_edit_opinion);
        submitBut = (Button) findViewById(R.id.order_evaluate_submit_btn);
        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAssessMerch(entity.orderId, qualityEvaluateLevel + "", speedEvaluateLevel + "", opinionEditText.getText().toString());
            }
        });
        qualityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                qualityEvaluateLevel = (int) rating;
            }
        });
        speedRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                speedEvaluateLevel = (int) rating;
            }
        });
    }

    private void actionBarEvent() {
        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("评价订单");
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

    //访问订单评价
    private void requestAssessMerch( String merchandiseId, String dileveryStar, String speedStar, String bodyText) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("MERCHANDISEID", merchandiseId);
        args.put("DILEVERYSTAR", speedStar);
        args.put("QUALITYSTAR", dileveryStar);
        args.put("TEXT", bodyText);
        args.put("ISAPPEND", "0");
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "AssessMerch", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
                .build();
        FacadeClient.request(OrderEvaluateActivity.this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(OrderEvaluateActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        Toast.makeText(OrderEvaluateActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OrderEvaluateActivity.this, MyOrderActivity.class);
//                        startActivity(intent);
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onOrderStateChange);
                        finish();
                    } else {
                        Toast.makeText(OrderEvaluateActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg != null) {
                    Log.e("tag", "--requestCode--->" + errorMsg.msg);
                }
            }
        });
    }
}

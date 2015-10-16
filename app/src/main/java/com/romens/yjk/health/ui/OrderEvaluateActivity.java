package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;

/**
 * Created by anlc on 2015/9/23.
 * 编辑订单评价页面
 */
public class OrderEvaluateActivity extends BaseActivity {

//    private BackupImageView leftImageView;
    private TextView titleTextView;
//    private TextView specTextView;
    private TextView moneyTextView;
    private TextView dateTextView;
    private TextView countTextView;

    private RatingBar qualityRatingBar;
    private RatingBar speedRatingBar;
    private EditText opinionEditText;
    private Button submitBut;
//    private AllOrderEntity entity;

    private int qualityEvaluateLevel;
    private int speedEvaluateLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_evaluate, R.id.action_bar);
        actionBarEvent();
        initView();
        viewSetData();
    }

    private void viewSetData() {
        Intent intent = getIntent();
        AllOrderEntity entity = (AllOrderEntity) intent.getSerializableExtra("orderEntity");
        if (null != entity) {
            titleTextView.setText(entity.getGoodsName());
            moneyTextView.setText("x" + entity.getMerCount());
            dateTextView.setText(entity.getOrderPrice());
//            countTextView.setText("2015-12-15 08:09");
            countTextView.setText(entity.getCreateDate());
        }
    }

    private void initView() {
        titleTextView = (TextView) findViewById(R.id.order_title);
        moneyTextView = (TextView) findViewById(R.id.order_money);
        dateTextView = (TextView) findViewById(R.id.order_date);
        countTextView = (TextView) findViewById(R.id.order_count);

        qualityRatingBar = (RatingBar) findViewById(R.id.order_evaluate_quality_ratingbar);
        speedRatingBar = (RatingBar) findViewById(R.id.order_evaluate_speed_ratingbar);
        opinionEditText = (EditText) findViewById(R.id.order_evaluate_edit_opinion);
        submitBut = (Button) findViewById(R.id.order_evaluate_submit_btn);
        submitBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderEvaluateActivity.this, "click-->submit", Toast.LENGTH_SHORT).show();
            }
        });
        qualityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                qualityEvaluateLevel = (int) rating;
                Toast.makeText(OrderEvaluateActivity.this, "click-quality->" + rating, Toast.LENGTH_SHORT).show();
            }
        });
        speedRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                speedEvaluateLevel = (int) rating;
                Toast.makeText(OrderEvaluateActivity.this, "click-speed->" + rating, Toast.LENGTH_SHORT).show();
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
}

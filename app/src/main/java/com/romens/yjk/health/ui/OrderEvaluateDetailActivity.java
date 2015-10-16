package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;

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

    private RatingBar qualityRatingBar;
    private RatingBar speedRatingBar;
    private TextView opinionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_evaluate_detail, R.id.action_bar);
        actionBarEvent();
        initView();
        viewSetData();
    }

    private void viewSetData() {
        Intent intent = getIntent();
        AllOrderEntity entity = (AllOrderEntity) intent.getSerializableExtra("evaluateDetailEntity");
        if (null != entity) {
            titleTextView.setText(entity.getGoodsName());
            moneyTextView.setText("x"+entity.getMerCount());
            dateTextView.setText(entity.getOrderPrice());
//            countTextView.setText("2015-12-15 08:09");
            countTextView.setText(entity.getCreateDate());
            qualityRatingBar.setRating(3);
            speedRatingBar.setRating(4);
            opinionTextView.setText("jo爱的色放adoif就是防守反击三季度分厘卡剑荡四方阿卡迪夫拉克赛代练客服那算了开发拉开大夫拉克赛的饭卡夫拉克赛的家fid是flak的家纺哦is的");
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

        qualityRatingBar = (RatingBar) findViewById(R.id.order_evaluate_quality_ratingbar);
        speedRatingBar = (RatingBar) findViewById(R.id.order_evaluate_speed_ratingbar);
        opinionTextView = (TextView) findViewById(R.id.order_evaluate_opinion_text);
    }
}

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
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AllOrderEntity;

/**
 * Created by anlc on 2015/9/23.
 * 订单评价详情页面
 */
public class OrderEvaluateDetailActivity extends BaseActivity {

    private BackupImageView leftImageView;
    private TextView titleTextView;
    private TextView specTextView;
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
        AllOrderEntity  entity = (AllOrderEntity) intent.getSerializableExtra("evaluateDetailEntity");
        if (null != entity) {
            String url = "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg";
            leftImageView.setImageUrl(url, "64_64", null);
            titleTextView.setText(entity.getGoodsName());
            specTextView.setText("12g*10袋");
            moneyTextView.setText("x2");
            dateTextView.setText(entity.getOrderPrice());
            countTextView.setText("2015-12-15 08:09");
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
        leftImageView = (BackupImageView) findViewById(R.id.order_img);
        titleTextView = (TextView) findViewById(R.id.order_title);
        specTextView = (TextView) findViewById(R.id.order_spec);
        moneyTextView = (TextView) findViewById(R.id.order_money);
        dateTextView = (TextView) findViewById(R.id.order_date);
        countTextView = (TextView) findViewById(R.id.order_count);

        qualityRatingBar = (RatingBar) findViewById(R.id.order_evaluate_quality_ratingbar);
        speedRatingBar = (RatingBar) findViewById(R.id.order_evaluate_speed_ratingbar);
        opinionTextView = (TextView) findViewById(R.id.order_evaluate_opinion_text);
    }
}

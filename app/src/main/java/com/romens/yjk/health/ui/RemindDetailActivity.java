package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.RemindEntity;
import com.romens.yjk.health.ui.utils.TransformDateUitls;

/**
 * Created by anlc on 2015/8/24.
 */
public class RemindDetailActivity extends BaseActivity {

    private TextView user;
    private TextView drug;
    private TextView timesHint;
    private TextView startDate;
    private RemindEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_details, R.id.remind_detail_actionbar);

        user = (TextView) findViewById(R.id.remind_detail__user);
        drug = (TextView) findViewById(R.id.remind_detail__drug);
        timesHint = (TextView) findViewById(R.id.remind_detail_timeshint);
        startDate = (TextView) findViewById(R.id.remind_detail__date);

        initData();

        ActionBar actionBar = getMyActionBar();
        actionBar.setTitle("我的提醒");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.photo_edit);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                } else if (i == 0) {
                    Intent startEditIntent = new Intent(RemindDetailActivity.this, AddRemindActivity.class);
                    if (entity != null) {
                        startEditIntent.putExtra("editEntity", entity);
                    }
                    startActivity(startEditIntent);
                }
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("detailBundle");
        entity = (RemindEntity) bundle.getSerializable("detailEntity");
        user.setText(entity.getUser());
        drug.setText(entity.getDrug());
        timesHint.setText(entity.getCount());
        startDate.setText(TransformDateUitls.getYearDate(Long.parseLong(entity.getStartDate())));
    }
}

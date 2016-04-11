package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.adapter.CuoponFragmentPagerAdapter;
import com.romens.yjk.health.ui.fragment.CouponFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2016/1/15.
 * 优惠券界面
 */

public class CuoponActivity extends DarkActionBarActivity {
    public static final String ARGUMENT_KEY_SELECT_COUPON_ID = "select_coupon_id";
    public static final String ARGUMENT_KEY_ORDER_AMOUNT = "order_amount";

    private ActionBar actionBar;
    private ViewPager viewPager;
    private CuoponFragmentPagerAdapter fragmentPagerAdapter;
    private TabLayout tabLayout;

    private List<Fragment> fragmentsList;
    private List<String> titles;

    private BigDecimal sumMoney = BigDecimal.ZERO;
    private boolean canClick;
    private String selectCouponId;
    public static final int NOW = 1;
    public static final int HISTORY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuopon, R.id.action_bar);
        Intent intent = getIntent();
        selectCouponId = intent.getStringExtra(ARGUMENT_KEY_SELECT_COUPON_ID);
        sumMoney = new BigDecimal(intent.getDoubleExtra(ARGUMENT_KEY_ORDER_AMOUNT, 0));
        canClick = intent.getBooleanExtra("canClick", true);

        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("我的优惠券");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    finish();
                }
            }
        });

        initView();

    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tableLayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        initFragments();
        initTitles();
        fragmentPagerAdapter = new CuoponFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList, titles);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void initFragments() {
        fragmentsList = new ArrayList<Fragment>();
        CouponFragment fragment = new CouponFragment();
        Bundle argument = new Bundle();
        argument.putString(CouponFragment.ARGUMENT_KEY_SELECT_COUPON_ID, selectCouponId);
        argument.putDouble(CouponFragment.ARGUMENT_KEY_ORDER_AMOUNT, sumMoney.doubleValue());
        argument.putInt("page", NOW);
        argument.putBoolean("click", canClick);
        fragment.setArguments(argument);
        //fragment.setPage(NOW);
        //fragment.setCanClick(canClick);
        fragmentsList.add(fragment);

        CouponFragment historyFragment = new CouponFragment();
        Bundle historyArgument = new Bundle();
        historyArgument.putInt("page", HISTORY);
        historyArgument.putBoolean("click", canClick);
        historyFragment.setArguments(historyArgument);
        //historyFragment.setPage(HISTORY);
        fragmentsList.add(historyFragment);

    }

    private void initTitles() {
        titles = new ArrayList<String>();
        titles.add("可用优惠券");
        titles.add("历史优惠券");
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(titles.get(1)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

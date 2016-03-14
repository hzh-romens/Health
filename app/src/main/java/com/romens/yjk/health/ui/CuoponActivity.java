package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.adapter.CuoponFragmentPagerAdapter;
import com.romens.yjk.health.ui.fragment.CuoponFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2016/1/15.
 * 优惠券界面
 */

public class CuoponActivity extends BaseActivity {
    private ActionBar actionBar;
    private RadioGroup radioGroup;
    private ViewPager viewPager;
    private CuoponFragmentPagerAdapter fragmentPagerAdapter;
    private TabLayout tabLayout;

    private List<Fragment> fragmentsList;
    private List<String> titles;

    private String sumMoney;
    private boolean canClick;
    private int choicePosition;
    public static final int NOW = 1;
    public static final int HISTORY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuopon, R.id.action_bar);
        choicePosition = getIntent().getIntExtra("position", 0);
        sumMoney = getIntent().getStringExtra("sumMoney");
        canClick = getIntent().getBooleanExtra("canClick", true);

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
        CuoponFragment fragment = new CuoponFragment();
        fragment.setPage(NOW);
        fragment.setChoice(choicePosition, Double.parseDouble(sumMoney));
        fragmentsList.add(fragment);
        CuoponFragment historyFragment = new CuoponFragment();
        historyFragment.setPage(HISTORY);
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

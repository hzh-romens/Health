package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by HZH on 2016/1/27.
 */
public class CuoponFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<String> mTitles;
    private List<Fragment> mFragmentList;

    public CuoponFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.mTitles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}

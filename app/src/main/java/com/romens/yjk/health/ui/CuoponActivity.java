package com.romens.yjk.health.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.fragment.CuoponFragment;

/**
 * Created by HZH on 2016/1/15.
 * 优惠券界面
 */

public class CuoponActivity extends BaseActivity {
    private ActionBar actionBar;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuopon, R.id.action_bar);
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("我的优惠券");
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);


        final CuoponFragment fragment = new CuoponFragment();
        Bundle bundle = new Bundle();
        bundle.putString("flag", "1");
        fragment.setArguments(bundle);
        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment, "Cuopon");
        fragmentTransaction.commit();

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.now:
                        CuoponFragment nowFragment = new CuoponFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("flag", "1");
                        nowFragment.setArguments(bundle);
                        fragmentManager.beginTransaction().replace(R.id.container, nowFragment, "Cuopon").commit();
                        break;
                    case R.id.last:
                        CuoponFragment lastFragment = new CuoponFragment();
                        Bundle bundles = new Bundle();
                        bundles.putString("flag", "2");
                        lastFragment.setArguments(bundles);
                        fragmentManager.beginTransaction().replace(R.id.container,lastFragment, "Cuopon").commit();
                        break;
                }

            }
        });
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

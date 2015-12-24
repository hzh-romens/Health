package com.romens.yjk.health.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.Image.NetImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.HomeActivity;

/**
 * Created by anlc on 2015/10/28.
 */
public class IntroActivityNew extends Activity {

    private ViewPager viewPager;

    private boolean openGuidePager;
    private int[] pagerImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //是否首次打开App，首次打开App启动引导页
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        openGuidePager = sharedPreferences.getBoolean("open_guide_pager", false);
        if (openGuidePager) {
            sharedPreferences.edit().putBoolean("open_guide_pager", false).commit();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_intro);
        viewPager = (ViewPager) findViewById(R.id.intro_view_pager);
        if (openGuidePager) {
            pagerImages = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
            viewPager.setAdapter(new MyPagerAdapter(pagerImages, this));
        } else {
            viewPager.setAdapter(new ADPagerAdapter(this));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openHomeActivity();
                }
            }, 2000);
        }
    }

    private void openHomeActivity() {
        Intent intent2 = new Intent(this, HomeActivity.class);
        intent2.putExtra("fromIntro", true);
        startActivity(intent2);
        finish();
    }

    class ADPagerAdapter extends PagerAdapter {
        private Context context;

        public ADPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_intro_ad, null);
            NetImageView imageView = (NetImageView) view.findViewById(R.id.ad_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImage("", R.drawable.intro_ad_pager, 0);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {

            return view == o;
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        private int[] imgs;
        private Context context;

        public MyPagerAdapter(int[] imgs, Context context) {
            this.imgs = imgs;
            this.context = context;
        }

        @Override
        public int getCount() {
            return imgs.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_intro, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.item_intro_image);
            TextView textView = (TextView) view.findViewById(R.id.item_intro_btn);
            imageView.setImageResource(imgs[position]);
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 2) {
                        openHomeActivity();
                    }
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {

            return view == o;
        }
    }
}

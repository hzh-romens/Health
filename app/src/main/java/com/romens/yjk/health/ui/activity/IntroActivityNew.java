package com.romens.yjk.health.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.HomeActivity;

/**
 * Created by anlc on 2015/10/28.
 */
public class IntroActivityNew extends Activity {

    private ViewPager viewPager;
    private int[] imgs;
    private String isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("flag", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        isFirst = sharedPreferences.getString("isFirst", "yes");
        if (isFirst.equals("yes")) {
            editor.putString("isFirst", "no");
            editor.commit();
        } else {
            Intent intent2 = new Intent(this, HomeActivity.class);
            intent2.putExtra("fromIntro", true);
            startActivity(intent2);
            finish();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //  setContentView(R.layout.activity_intro);
        viewPager = (ViewPager) findViewById(R.id.intro_view_pager);
        imgs = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        viewPager.setAdapter(new MyPagerAdapter(imgs, this));
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
                        Intent intent2 = new Intent(context, HomeActivity.class);
                        intent2.putExtra("fromIntro", true);
                        startActivity(intent2);
                        finish();
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

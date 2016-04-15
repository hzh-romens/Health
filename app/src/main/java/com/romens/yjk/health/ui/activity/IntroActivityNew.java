package com.romens.yjk.health.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.romens.android.AndroidUtilities;
import com.romens.android.log.FileLog;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.HomeActivity;
import com.romens.yjk.health.ui.base.BaseActivity;
import com.romens.yjk.health.wx.mta.MTAManager;
import com.romens.yjk.health.wx.push.PushManager;
import com.tencent.stat.StatService;

/**
 * Created by anlc on 2015/10/28.
 */
public class IntroActivityNew extends BaseActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        MTAManager.init(this);
        PushManager.init(this);
        PushManager.register(this);
        StatService.trackCustomEvent(this, "onCreate", "");

        //是否首次打开App，首次打开App启动引导页
        setContentView(R.layout.activity_intro);
        viewPager = (ViewPager) findViewById(R.id.intro_view_pager);
        viewPager.setAdapter(new ADPagerAdapter(this));
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                openHomeActivity();
            }
        }, 2000);

        FileLog.d("IntroActivity start");
    }

    private void openHomeActivity() {
        Intent intent2 = new Intent(this, HomeActivity.class);
        intent2.putExtra("fromIntro", true);
        startActivity(intent2);
        finish();
    }

    @Override
    protected String getActivityName() {
        return "起始页";
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
            Context context = container.getContext();
            FrameLayout content = new FrameLayout(context);
            CloudImageView cloudImageView = CloudImageView.create(context);
            cloudImageView.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
            cloudImageView.setPlaceholderImage(context.getResources().getDrawable(R.drawable.intro_ad_pager));
            content.addView(cloudImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            container.addView(content, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return content;
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

//    class MyPagerAdapter extends PagerAdapter {
//
//        private int[] imgs;
//        private Context context;
//
//        public MyPagerAdapter(int[] imgs, Context context) {
//            this.imgs = imgs;
//            this.context = context;
//        }
//
//        @Override
//        public int getCount() {
//            return imgs.length;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, final int position) {
//            View view = LayoutInflater.from(context).inflate(R.layout.list_item_intro, null);
//            ImageView imageView = (ImageView) view.findViewById(R.id.item_intro_image);
//            TextView textView = (TextView) view.findViewById(R.id.item_intro_btn);
//            imageView.setImageResource(imgs[position]);
//            textView.setVisibility(View.VISIBLE);
//            textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (position == 2) {
//                        openHomeActivity();
//                    }
//                }
//            });
//            container.addView(view);
//            return view;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object o) {
//
//            return view == o;
//        }
//    }
}

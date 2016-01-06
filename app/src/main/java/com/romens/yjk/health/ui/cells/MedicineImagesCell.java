package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.android.ui.view.GuideViewPager;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.CommonConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/12/14.
 */
public class MedicineImagesCell extends FrameLayout {
    public static final int defaultHeight = (int) (AndroidUtilities.displayMetrics.widthPixels * CommonConfig.medicinePagerScale);
    private GuideViewPager viewPager;
    private TextView pagerCountView;

    private GoodsImagesAdapter adapter;

    //    private Timer timeTimer;
//    private final Object timerSync = new Object();
    private int currPagerIndex = 0;
    private int pagerCount = 0;

    public MedicineImagesCell(Context context) {
        super(context);
        viewPager = new GuideViewPager(context);
        viewPager.setBackgroundColor(0xfff0f0f0);
        addView(viewPager, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, defaultHeight));

        pagerCountView = new TextView(context);
        pagerCountView.setBackgroundResource(R.drawable.attach_pager_count);
        pagerCountView.setTextColor(0xffffffff);
        pagerCountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        pagerCountView.setLines(1);
        pagerCountView.setMaxLines(1);
        pagerCountView.setSingleLine(true);
        pagerCountView.setGravity(Gravity.CENTER);
        addView(pagerCountView, LayoutHelper.createFrame(48, 48, Gravity.BOTTOM | Gravity.RIGHT, 8, 8, 8, 8));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currPagerIndex = position;
                updatePageCountTag();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter = new GoodsImagesAdapter(context);
        viewPager.setAdapter(adapter);
    }

    private void updatePageCountTag() {
        pagerCountView.setText(String.format("%d/%d", (currPagerIndex + 1), pagerCount));
    }

    //    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        start();
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        destroyTimer();
//        super.onDetachedFromWindow();
//    }
//
//    public void start() {
//        createTimer();
//    }
//
//    private void createTimer() {
//        if (viewPager == null || viewPager.getAdapter() == null || viewPager.getAdapter().getCount() <= 0) {
//            return;
//        }
//        if (timeTimer != null) {
//            return;
//        }
//        timeTimer = new Timer();
//        timeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                int count = viewPager.getAdapter().getCount();
//                if (currPagerIndex == (count - 1)) {
//                    currPagerIndex = 0;
//                } else {
//                    currPagerIndex++;
//                }
//                AndroidUtilities.runOnUIThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        viewPager.setCurrentItem(currPagerIndex, true);
//                    }
//                });
//
//            }
//        }, 3000, 3000);
//    }
//
//    private void destroyTimer() {
//        try {
//            synchronized (timerSync) {
//                if (timeTimer != null) {
//                    timeTimer.cancel();
//                    timeTimer = null;
//                }
//            }
//        } catch (Exception e) {
//            FileLog.e("romens", e);
//        }
//    }
//
    public void bindData(List<String> images) {
        adapter.bindData(images);
        viewPager.setCurrentItem(0);
        currPagerIndex = 0;
        pagerCount = (images == null) ? 0 : images.size();
        updatePageCountTag();
        //createTimer();
    }

    static class GoodsImagesAdapter extends PagerAdapter {

        private Context adapterContext;
        private List<String> imagesList = new ArrayList<>();
        private final List<CloudImageView> viewList = new ArrayList<>();

        public GoodsImagesAdapter(Context context) {
            adapterContext = context;
        }

        public void bindData(List<String> images) {
            imagesList.clear();
            if (images != null && images.size() > 0) {
                imagesList.addAll(images);
            }
            final int size = imagesList.size();
            viewList.clear();
            for (int i = 0; i < size; i++) {
                viewList.add(CloudImageView.create(adapterContext));
            }
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(viewList.get(position));
        }


        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            final String imageUrl = imagesList.get(position);
            CloudImageView pager = viewList.get(position);
            container.addView(pager, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,defaultHeight));
            pager.setImagePath(imageUrl);
            return pager;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


}

package com.romens.yjk.health.ui.components;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.core.NotificationCenter;
import com.romens.android.log.FileLog;
import com.romens.android.ui.AnimationCompat.ViewProxy;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Components.PagerSlidingTabStrip;
import com.romens.android.ui.Components.ScrollSlidingTabStrip;
import com.romens.yjk.health.R;
import com.romens.yjk.health.im.Emoji;

import java.util.ArrayList;
import java.util.HashMap;

public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

    public interface Listener {
        boolean onBackspace();
        void onEmojiSelected(String emoji);
    }

    private ArrayList<EmojiGridAdapter> adapters = new ArrayList<>();
    private HashMap<Long, Integer> stickersUseHistory = new HashMap<>();

    private int[] icons = {
            R.drawable.ic_emoji_recent,
            R.drawable.ic_emoji_smile,
            R.drawable.ic_emoji_flower,
            R.drawable.ic_emoji_bell,
            R.drawable.ic_emoji_car,
            R.drawable.ic_emoji_symbol,
            R.drawable.ic_emoji_sticker};

    private Listener listener;
    private ViewPager pager;
    private FrameLayout recentsWrap;
    private ArrayList<GridView> views = new ArrayList<>();
    private ImageView backspaceButton;
    private LinearLayout pagerSlidingTabStripContainer;
    private ScrollSlidingTabStrip scrollSlidingTabStrip;

    private int oldWidth;
    private int lastNotifyWidth;

    private boolean backspacePressed;
    private boolean backspaceOnce;

    public EmojiView( Context context) {
        super(context);

        for (int i = 0; i < Emoji.data.length; i++) {
            GridView gridView = new GridView(context);
            if (AndroidUtilities.isTablet()) {
                gridView.setColumnWidth(AndroidUtilities.dp(60));
            } else {
                gridView.setColumnWidth(AndroidUtilities.dp(45));
            }
            gridView.setNumColumns(-1);
            views.add(gridView);

            EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter(Emoji.data[i]);
            gridView.setAdapter(emojiGridAdapter);
            AndroidUtilities.setListViewEdgeEffectColor(gridView, 0xfff5f6f7);
            adapters.add(emojiGridAdapter);
        }
        setBackgroundColor(0xfff5f6f7);

        pager = new ViewPager(context) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        pager.setAdapter(new EmojiPagesAdapter());

        pagerSlidingTabStripContainer = new LinearLayout(context) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        pagerSlidingTabStripContainer.setOrientation(LinearLayout.HORIZONTAL);
        pagerSlidingTabStripContainer.setBackgroundColor(0xfff5f6f7);
        addView(pagerSlidingTabStripContainer, LayoutHelper.createFrame(LayoutParams.MATCH_PARENT, 48));

        PagerSlidingTabStrip pagerSlidingTabStrip = new PagerSlidingTabStrip(context);
        pagerSlidingTabStrip.setViewPager(pager);
        pagerSlidingTabStrip.setShouldExpand(true);
        pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2));
        pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1));
        pagerSlidingTabStrip.setIndicatorColor(0xff2b96e2);
        pagerSlidingTabStrip.setUnderlineColor(0xffe2e5e7);
        pagerSlidingTabStripContainer.addView(pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, 1.0f));
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                EmojiView.this.onPageScrolled(position, getMeasuredWidth(), positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        FrameLayout frameLayout = new FrameLayout(context);
        pagerSlidingTabStripContainer.addView(frameLayout, LayoutHelper.createLinear(52, 48));

        backspaceButton = new ImageView(context) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    backspacePressed = true;
                    backspaceOnce = false;
                    postBackspaceRunnable(350);
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
                    backspacePressed = false;
                    if (!backspaceOnce) {
                        if (EmojiView.this.listener != null && EmojiView.this.listener.onBackspace()) {
                            backspaceButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        }
                    }
                }
                super.onTouchEvent(event);
                return true;
            }
        };
        backspaceButton.setImageResource(R.drawable.ic_smiles_backspace);
        backspaceButton.setBackgroundResource(R.drawable.ic_emoji_backspace);
        backspaceButton.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout.addView(backspaceButton, LayoutHelper.createFrame(52, 48));

        View view = new View(context);
        view.setBackgroundColor(0xffe2e5e7);
        frameLayout.addView(view, LayoutHelper.createFrame(52, 1, Gravity.LEFT | Gravity.BOTTOM));

        recentsWrap = new FrameLayout(context);
        recentsWrap.addView(views.get(0));

        TextView textView = new TextView(context);
        textView.setText("无最近的");
        textView.setTextSize(18);
        textView.setTextColor(0xff888888);
        textView.setGravity(Gravity.CENTER);
        recentsWrap.addView(textView);
        views.get(0).setEmptyView(textView);

        addView(pager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 0, 48, 0, 0));

        loadRecents();

        if (Emoji.data[0] == null || Emoji.data[0].length == 0) {
            pager.setCurrentItem(1);
        }
    }

    private void onPageScrolled(int position, int width, int positionOffsetPixels) {
        if (scrollSlidingTabStrip == null) {
            return;
        }

        if (width == 0) {
            width = AndroidUtilities.displaySize.x;
        }

        int margin = 0;
        if (position == 5) {
            margin = -positionOffsetPixels;
        } else if (position == 6) {
            margin = -width;
        }

        if (ViewProxy.getTranslationX(pagerSlidingTabStripContainer) != margin) {
            ViewProxy.setTranslationX(pagerSlidingTabStripContainer, margin);
            ViewProxy.setTranslationX(scrollSlidingTabStrip, width + margin);
            if (Build.VERSION.SDK_INT < 11) {
                if (margin <= -width) {
                    pagerSlidingTabStripContainer.clearAnimation();
                    pagerSlidingTabStripContainer.setVisibility(GONE);
                } else {
                    pagerSlidingTabStripContainer.setVisibility(VISIBLE);
                }
            }
        } else if (Build.VERSION.SDK_INT < 11 && pagerSlidingTabStripContainer.getVisibility() == GONE) {
            pagerSlidingTabStripContainer.clearAnimation();
            pagerSlidingTabStripContainer.setVisibility(GONE);
        }
    }

    private void postBackspaceRunnable(final int time) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!backspacePressed) {
                    return;
                }
                if (EmojiView.this.listener != null && EmojiView.this.listener.onBackspace()) {
                    backspaceButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                }
                backspaceOnce = true;
                postBackspaceRunnable(Math.max(50, time - 100));
            }
        }, time);
    }

    private void addToRecent(long code) {
        if (pager.getCurrentItem() == 0) {
            return;
        }
        ArrayList<Long> recent = new ArrayList<>();
        long[] currentRecent = Emoji.data[0];
        boolean was = false;
        for (long aCurrentRecent : currentRecent) {
            if (code == aCurrentRecent) {
                recent.add(0, code);
                was = true;
            } else {
                recent.add(aCurrentRecent);
            }
        }
        if (!was) {
            recent.add(0, code);
        }
        Emoji.data[0] = new long[Math.min(recent.size(), 50)];
        for (int q = 0; q < Emoji.data[0].length; q++) {
            Emoji.data[0][q] = recent.get(q);
        }
        adapters.get(0).data = Emoji.data[0];
        adapters.get(0).notifyDataSetChanged();
        saveRecents();
    }

    private String convert(long paramLong) {
        String str = "";
        for (int i = 0; ; i++) {
            if (i >= 4) {
                return str;
            }
            int j = (int) (0xFFFF & paramLong >> 16 * (3 - i));
            if (j != 0) {
                str = str + (char) j;
            }
        }
    }

    private void saveRecents() {
        ArrayList<Long> arrayList = new ArrayList<>(Emoji.data[0].length);
        for (int j = 0; j < Emoji.data[0].length; j++) {
            arrayList.add(Emoji.data[0][j]);
        }
        getContext().getSharedPreferences("emoji", 0).edit().putString("recents", TextUtils.join(",", arrayList)).commit();
    }

    private void saveRecentStickers() {
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", Activity.MODE_PRIVATE);
        StringBuilder stringBuilder = new StringBuilder();
        for (HashMap.Entry<Long, Integer> entry : stickersUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        getContext().getSharedPreferences("emoji", 0).edit().putString("stickers", stringBuilder.toString()).commit();
    }

    public void loadRecents() {
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", Activity.MODE_PRIVATE);
        String str = preferences.getString("recents", "");
        try {
            if (str != null && str.length() > 0) {
                String[] args = str.split(",");
                Emoji.data[0] = new long[args.length];
                for (int i = 0; i < args.length; i++) {
                    Emoji.data[0][i] = Long.parseLong(args[i]);
                }
            } else {
                Emoji.data[0] = new long[]{0x00000000D83DDE02L, 0x00000000D83DDE18L, 0x0000000000002764L, 0x00000000D83DDE0DL, 0x00000000D83DDE0AL, 0x00000000D83DDE01L,
                        0x00000000D83DDC4DL, 0x000000000000263AL, 0x00000000D83DDE14L, 0x00000000D83DDE04L, 0x00000000D83DDE2DL, 0x00000000D83DDC8BL,
                        0x00000000D83DDE12L, 0x00000000D83DDE33L, 0x00000000D83DDE1CL, 0x00000000D83DDE48L, 0x00000000D83DDE09L, 0x00000000D83DDE03L,
                        0x00000000D83DDE22L, 0x00000000D83DDE1DL, 0x00000000D83DDE31L, 0x00000000D83DDE21L, 0x00000000D83DDE0FL, 0x00000000D83DDE1EL,
                        0x00000000D83DDE05L, 0x00000000D83DDE1AL, 0x00000000D83DDE4AL, 0x00000000D83DDE0CL, 0x00000000D83DDE00L, 0x00000000D83DDE0BL,
                        0x00000000D83DDE06L, 0x00000000D83DDC4CL, 0x00000000D83DDE10L, 0x00000000D83DDE15L};
            }
            adapters.get(0).data = Emoji.data[0];
            adapters.get(0).notifyDataSetChanged();
        } catch (Exception e) {
            FileLog.e("romens", e);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams layoutParams = (LayoutParams) pagerSlidingTabStripContainer.getLayoutParams();
        LayoutParams layoutParams1 = null;
        layoutParams.width = MeasureSpec.getSize(widthMeasureSpec);
        if (scrollSlidingTabStrip != null) {
            layoutParams1 = (LayoutParams) scrollSlidingTabStrip.getLayoutParams();
            layoutParams1.width = layoutParams.width;
        }
        if (layoutParams.width != oldWidth) {
            if (scrollSlidingTabStrip != null) {
                onPageScrolled(pager.getCurrentItem(), layoutParams.width, 0);
                scrollSlidingTabStrip.setLayoutParams(layoutParams1);
            }
            pagerSlidingTabStripContainer.setLayoutParams(layoutParams);
            oldWidth = layoutParams.width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (lastNotifyWidth != right - left) {
            lastNotifyWidth = right - left;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setListener(Listener value) {
        listener = value;
    }

    public void invalidateViews() {
        for (GridView gridView : views) {
            if (gridView != null) {
                gridView.invalidateViews();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
    }

    private class EmojiGridAdapter extends BaseAdapter {
        long[] data;

        public EmojiGridAdapter(long[] arg2) {
            this.data = arg2;
        }

        public int getCount() {
            return data.length;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return data[i];
        }

        public View getView(int i, View view, ViewGroup paramViewGroup) {
            ImageView imageView = (ImageView)view;
            if (imageView == null) {
                imageView = new ImageView(EmojiView.this.getContext()) {
                    public void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2) {
                        setMeasuredDimension(MeasureSpec.getSize(paramAnonymousInt1), MeasureSpec.getSize(paramAnonymousInt1));
                    }
                };
                imageView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (EmojiView.this.listener != null) {
                            EmojiView.this.listener.onEmojiSelected(EmojiView.this.convert((Long)view.getTag()));
                        }
                        EmojiView.this.addToRecent((Long)view.getTag());
                    }
                });
                imageView.setBackgroundResource(R.drawable.list_selector);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            }
            imageView.setImageDrawable(Emoji.getEmojiBigDrawable(data[i]));
            imageView.setTag(data[i]);
            return imageView;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        public void destroyItem(ViewGroup viewGroup, int position, Object object) {
            View view;
            if (position == 0) {
                view = recentsWrap;
            }else {
                view = views.get(position);
            }
            viewGroup.removeView(view);
        }

        public int getCount() {
            return views.size();
        }

        public int getPageIconResId(int paramInt) {
            return icons[paramInt];
        }

        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view;
            if (position == 0) {
                view = recentsWrap;
            }else {
                view = views.get(position);
            }
            viewGroup.addView(view);
            return view;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }
}
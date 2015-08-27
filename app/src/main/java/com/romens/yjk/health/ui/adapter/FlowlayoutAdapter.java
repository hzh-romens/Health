package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.romens.yjk.health.ui.IllnessActivity;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.utils.UIUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by romens007 on 2015/8/18.
 */
public class FlowlayoutAdapter {
    private FlowLayout mFlowLayout;
    private Context mContext;
    private List<String> mData;

    public FlowlayoutAdapter(FlowLayout flowLayout,Context context,List<String> data){
        this.mFlowLayout=flowLayout;
        this.mContext=context;
        this.mData=data;
    }
//    public int getItemCount(){
//        if(mData!=null){
//            return mData.size();
//        }
//        return 0;
//    }
    public interface FlowLayoutItemClick{
        void onItemClick(int position);
    }
    private FlowLayoutItemClick flowLayoutItemClick;

    public void ItemClickListener(FlowLayoutItemClick flowLayoutItemClick) {
        this.flowLayoutItemClick = flowLayoutItemClick;
    }
    public void andTextView() {
        // mFlowLayout = new FlowLayout(MainActivity.this);
        int layoutPadding = UIUtils.dip2px(13);
        mFlowLayout.setPadding(layoutPadding, layoutPadding, layoutPadding,
                layoutPadding);
        mFlowLayout.setHorizontalSpacing(layoutPadding);
        mFlowLayout.setVerticalSpacing(layoutPadding);

        int textPaddingV = UIUtils.dip2px(4);
        int textPaddingH = UIUtils.dip2px(10);
        int backColor = 0xffcecece;
        int radius = UIUtils.dip2px(5);
        // 代码动态创建一个图片
        GradientDrawable pressDrawable = createDrawable(
                backColor, backColor, radius);
        Random mRdm = new Random();
        for (int i = 0; i < mData.size(); i++) {
            TextView tv = new TextView(UIUtils.getContext());
            // 随机颜色的范围0x202020~0xefefef
            int red = 32 + mRdm.nextInt(208);
            int green = 32 + mRdm.nextInt(208);
            int blue = 32 + mRdm.nextInt(208);
            //   int color = 0xff000000 | (red << 16) | (green << 8) | blue;
            int color=mContext.getResources().getColor(com.romens.android.R.color.md_blue_900);
            // 创建背景图片选择器
            GradientDrawable normalDrawable = createDrawable(
                    color, backColor, radius);
            StateListDrawable selector = createSelector(
                    normalDrawable, pressDrawable);
            tv.setBackgroundDrawable(selector);

            final String text = mData.get(i);
            tv.setText(text);
            tv.setTextColor(Color.rgb(255, 255, 255));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(textPaddingH, textPaddingV, textPaddingH,
                    textPaddingV);
            final int position=i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flowLayoutItemClick.onItemClick(position);


                }
            });
            mFlowLayout.addView(tv);
        }
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 创建一个图片
     * @param contentColor 内部填充颜色
     * @param strokeColor  描边颜色
     * @param radius       圆角
     */
    public static GradientDrawable createDrawable(int contentColor, int strokeColor, int radius) {
        GradientDrawable drawable = new GradientDrawable(); // 生成Shape
        drawable.setGradientType(GradientDrawable.RECTANGLE); // 设置矩形
        drawable.setColor(contentColor);// 内容区域的颜色
        drawable.setStroke(1, strokeColor); // 四周描边,描边后四角真正为圆角，不会出现黑色阴影。如果父窗体是可以滑动的，需要把父View设置setScrollCache(false)
        drawable.setCornerRadius(radius); // 设置四角都为圆角
        return drawable;
    }
    /**
     * 创建一个图片选择器
     * @param normalState  普通状态的图片
     * @param pressedState 按压状态的图片
     */
    public static StateListDrawable createSelector(Drawable normalState, Drawable pressedState) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressedState);
        bg.addState(new int[]{android.R.attr.state_enabled}, normalState);
        bg.addState(new int[]{}, normalState);
        return bg;
    }


}

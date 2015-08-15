package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by siery on 15/8/14.
 */
public class ProductCell extends CardView {
    private BackupImageView iconView;
    private TextView nameView;
    public ProductCell(Context context) {
        super(context);
        setRadius(4);
        setCardBackgroundColor(Color.WHITE);
        setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4), AndroidUtilities.dp(4));
        iconView=new BackupImageView(context);
        iconView.setRoundRadius(4);
        addView(iconView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.TOP | Gravity.CENTER_HORIZONTAL, 4, 4, 4, 4));

        LinearLayout content=new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        addView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        nameView=new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        nameView.setSingleLine(true);
        content.addView(nameView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        TextView priceView=new TextView(context);
        priceView.setTextColor(0xff0f9d58);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        priceView.setSingleLine(true);
        priceView.setText("$21.0");
        priceView.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
        content.addView(priceView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void setValue(String name,String iconUrl){
        nameView.setText(name);
        iconView.setImage(iconUrl, null, null);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = measureWidth+AndroidUtilities.dp(48);
        int count = getChildCount();

        for (int index = 0; index < count; index++) {
            final View child = getChildAt(index);
            if(child instanceof BackupImageView) {
                if (child.getVisibility() != GONE) {
                    measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth - AndroidUtilities.dp(8), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(measureWidth - AndroidUtilities.dp(8), MeasureSpec.EXACTLY));
                }
            }
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
        int totalWidth = getMeasuredWidth();
        int totalHeight=getMeasuredHeight();
        int count = getChildCount();

        int y=0;
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            if(view instanceof BackupImageView){
                y=view.getMeasuredHeight()+AndroidUtilities.dp(8);
            }else if(view instanceof LinearLayout){
                view.layout(0, y,totalWidth,totalHeight-AndroidUtilities.dp(8));
            }
        }
    }
}

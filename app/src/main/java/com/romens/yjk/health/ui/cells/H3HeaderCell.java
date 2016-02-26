package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;

/**
 * @author Zhou Lisi
 * @create 16/2/25
 * @description
 */
public class H3HeaderCell extends FrameLayout {

    private TextView textView;

    private void init() {
        textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        AndroidUtilities.setMaterialTypeface(textView);
        textView.setTextColor(0xff212121);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        addView(textView);
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        layoutParams.leftMargin = AndroidUtilities.dp(17);
        layoutParams.rightMargin = AndroidUtilities.dp(17);
        layoutParams.topMargin = AndroidUtilities.dp(15);
        layoutParams.gravity = Gravity.LEFT;
        textView.setLayoutParams(layoutParams);
    }

    public H3HeaderCell(Context context) {
        super(context);
        init();
    }

    public H3HeaderCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public H3HeaderCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40), MeasureSpec.EXACTLY));
    }

    public void setTextSize(int size){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }
}

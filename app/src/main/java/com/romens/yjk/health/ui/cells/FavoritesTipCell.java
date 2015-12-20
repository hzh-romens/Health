package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/18.
 */
public class FavoritesTipCell extends LinearLayout {
    //private ImageView favoritesView;
    private TextView textView;

    private static Paint paint;
    private boolean needDivider;

    public FavoritesTipCell(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
//        favoritesView = new ImageView(context);
//        favoritesView.setScaleType(ImageView.ScaleType.CENTER);
//        favoritesView.setColorFilter(ResourcesConfig.favoritesColor);
//        addView(favoritesView, LayoutHelper.createLinear(56, 48));

        textView = new TextView(context);
        textView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        textView.setBackgroundResource(R.drawable.layout_grey_background);
        textView.setTextColor(ResourcesConfig.bodyText2);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 17, 16, 17, 16));
    }

    public void setValue(String text, String tag, int tagResId, boolean divider) {
        SpannableString textSpan = new SpannableString(text);
        int startIndex = text.indexOf(tag);
        int endIndex = startIndex + tag.length();
        Drawable drawable = getResources().getDrawable(tagResId);
        drawable.setColorFilter(ResourcesConfig.favoritesColor, PorterDuff.Mode.SRC_ATOP);
        Paint.FontMetrics paint = textView.getPaint().getFontMetrics();
        int size = (int) (Math.abs(paint.descent) + Math.abs(paint.ascent));
        if (size <= 0) {
            size = (int) textView.getTextSize();
        }
        drawable.setBounds(0, 0, size, size);
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        textSpan.setSpan(span, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(textSpan);
        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}

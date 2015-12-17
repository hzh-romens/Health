package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineMainCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider;

    private TextView nameView;
    private ImageView favoritesView;

    public MedicineMainCell(Context context) {
        super(context);
        init(context);
    }

    public MedicineMainCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MedicineMainCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        nameView = new TextView(context);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameView.setLines(2);
        nameView.setMaxLines(2);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity(Gravity.LEFT);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 17, 10, 64, 10));

        favoritesView = new ImageView(context);
        favoritesView.setClickable(true);
        favoritesView.setScaleType(ImageView.ScaleType.CENTER);
        favoritesView.setBackgroundResource(R.drawable.list_selector);
        favoritesView.setColorFilter(0xffe51c23);
        addView(favoritesView, LayoutHelper.createFrame(64, 56, Gravity.RIGHT | Gravity.CENTER_VERTICAL));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    public void setValue(CharSequence name, String desc, boolean isFavorites, boolean divider) {
        String text = name + "-" + desc;
        SpannableString textSpan = new SpannableString(text);
        textSpan.setSpan(new ForegroundColorSpan(0xff8a8a8a), name.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textSpan.setSpan(new AbsoluteSizeSpan(16, true), name.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        nameView.setText(textSpan);
        favoritesView.setImageResource(isFavorites ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setFavoritesDelegate(View.OnClickListener delegate) {
        favoritesView.setOnClickListener(delegate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(16), getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }
}

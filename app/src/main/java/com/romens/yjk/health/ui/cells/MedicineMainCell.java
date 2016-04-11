package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
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
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.helper.AnimHelper;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineMainCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider;

    private TextView nameView;
    private TextView favoritesView;

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
        nameView.setGravity(Gravity.LEFT|Gravity.TOP);
        AndroidUtilities.setMaterialTypeface(nameView);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 17, 10, 80, 10));

        favoritesView = new TextView(context);
        favoritesView.setBackgroundResource(R.drawable.button_favorites);
        favoritesView.setTextColor(getResources().getColor(R.color.medicine_favorites));
        favoritesView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        favoritesView.setSingleLine(true);
        favoritesView.setEllipsize(TextUtils.TruncateAt.END);
        favoritesView.setGravity(Gravity.CENTER);
        favoritesView.setText("收藏");
        favoritesView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        addView(favoritesView, LayoutHelper.createFrame(64, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.TOP, 0, 10, 16, 10));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    public void setValue(CharSequence name, String desc, boolean isFavorites, boolean divider) {
        SpannableStringBuilder textSpanBuilder = new SpannableStringBuilder();
        textSpanBuilder.append(name);
        if (!TextUtils.isEmpty(desc)) {
            desc = "-" + desc;
            SpannableString descSpan = new SpannableString(desc);
            descSpan.setSpan(new ForegroundColorSpan(0xff8a8a8a), 0, desc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            descSpan.setSpan(new AbsoluteSizeSpan(16, true), 0, desc.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textSpanBuilder.append(descSpan);
        }
        nameView.setText(textSpanBuilder);
        favoritesView.setText(isFavorites ? "已收藏" : "收藏");
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

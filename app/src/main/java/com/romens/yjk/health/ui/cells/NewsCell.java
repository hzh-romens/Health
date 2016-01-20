package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.images.ui.CloudImageView;

/**
 * Created by siery on 15/8/14.
 */
public class NewsCell extends FrameLayout {

    private TextView nameView;
    private CloudImageView iconView;

    private static Paint paint;
    private boolean needDivider = false;

    public NewsCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        nameView = new TextView(context);
        nameView.setTextColor(0x8a000000);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setLines(3);
        nameView.setMaxLines(3);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity((Gravity.LEFT) | Gravity.TOP);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (Gravity.LEFT) | Gravity.TOP, 8, 0, 80, 0));


        iconView = CloudImageView.create(context);
        iconView.setRound(AndroidUtilities.dp(4));
        addView(iconView, LayoutHelper.createFrame(64, 64, (Gravity.RIGHT) | Gravity.TOP, 8, 0, 8, 0));
    }

    public void setValue(String title, String content, String iconUrl, boolean divider) {
        CharSequence text=createText(title,content);
        nameView.setText(text);
        iconView.setImagePath(iconUrl);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public static CharSequence createText(String title, String content) {
        String text;
        if (!TextUtils.isEmpty(content)) {
            text = title.concat("-").concat(content);
        } else {
            text = title;
        }
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new ForegroundColorSpan(0xff212121), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80) + (needDivider ? 1 : 0), View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(16), getHeight() - 1, getWidth() - AndroidUtilities.dp(16), getHeight() - 1, paint);
        }
    }
}

package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class ToastCell  extends LinearLayout {
    private TextView textView;

    public ToastCell(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.toast_cell_states);

        textView = new TextView(context);
        textView.setTextColor(0xff8a8a8a);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36), View.MeasureSpec.EXACTLY));
    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public static void toast(Context context, CharSequence text) {
        toast(context, text, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, CharSequence text, int duration) {
        if (!TextUtils.isEmpty(text)) {
            ToastCell cell = new ToastCell(context);
            cell.setText(text);
            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(duration);
            toast.setView(cell);
            toast.show();
        }
    }


}

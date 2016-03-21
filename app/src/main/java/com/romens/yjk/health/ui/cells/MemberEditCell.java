package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/3/21.
 */
public class MemberEditCell extends FrameLayout {
    private boolean needDivider;
    private EditText editText;
    private TextView textView;
    private Paint paint;

    public MemberEditCell(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setWillNotDraw(false);
        editText = new EditText(context);
        editText.setTextSize(16);
        editText.setMaxLines(1);
        editText.setBackground(null);
        editText.setEllipsize(TextUtils.TruncateAt.END);
        addView(editText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.LEFT, 16, 8, 16, 8));
        textView = new TextView(context);
        textView.setTextColor(getResources().getColor(R.color.theme_primary));
        textView.setTextSize(14);
        textView.setText("获取验证码");
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT, 0, 8, 8, 16));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //  if (needDivider) {
        canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, paint);
        //}
    }

    public void setDrawableLeft(Drawable resId) {
        editText.setCompoundDrawablesWithIntrinsicBounds(resId, null, null, null);
    }

    public void setNeedDivider(boolean needDivider) {
        this.needDivider = needDivider;
    }

    public void setHintText(String hintText) {
        editText.setHint(hintText);
        editText.setHintTextColor(getResources().getColor(R.color.btn_primary_light));
    }

    public void setVisible(boolean visible) {
        if (visible) {
            textView.setVisibility(GONE);
        } else {
            textView.setVisibility(VISIBLE);
        }
    }
}

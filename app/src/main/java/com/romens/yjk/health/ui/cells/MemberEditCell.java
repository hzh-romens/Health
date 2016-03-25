package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
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

    private void init(final Context context) {
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
        editText.setBackgroundColor(getResources().getColor(R.color.white));
        editText.setEllipsize(TextUtils.TruncateAt.END);
        addView(editText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.LEFT, 16, 8, 16, 8));
        textView = new TextView(context);
        textView.setTextColor(getResources().getColor(R.color.md_white_1000));
        textView.setTextSize(14);
        textView.setPadding(8, 8, 8, 8);
        textView.setText("获取验证码");
        GradientDrawable textDrawable = new GradientDrawable();
        textDrawable.setCornerRadius(8);
        textDrawable.setColor(getResources().getColor(R.color.progress_bgc));
        textView.setBackground(textDrawable);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 8, 0));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEditListener.EditTextChange(s.toString().trim());
            }
        });
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SendRecommond();
            }
        });
    }

    private SendRecommondListener mListener;
    private EditTextChangeListener mEditListener;

    public void setSendRecommondListener(SendRecommondListener listener) {
        this.mListener = listener;
    }

    public interface SendRecommondListener {
        void SendRecommond();
    }

    public void SetEditTextChangeListener(EditTextChangeListener listener) {
        this.mEditListener = listener;
    }

    public interface EditTextChangeListener {
        void EditTextChange(String value);
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

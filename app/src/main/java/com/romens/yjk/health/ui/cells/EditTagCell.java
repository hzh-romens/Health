package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/10/14.
 */
public class EditTagCell extends LinearLayout {

    private EditText leftEditView;
    private TextView rightTextView;

    private boolean needDivider = false;
    private int dividerLeftPadding = 0;
    private int dividerRightPadding = 0;
    private static Paint paint;

    private onBtnClickListener onBtnClickListener;

    public void setOnBtnClickListener(EditTagCell.onBtnClickListener onBtnClickListener) {
        this.onBtnClickListener = onBtnClickListener;
    }

    public interface onBtnClickListener {
        void onClick();

        void editTextChange();
    }

    public EditTagCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTagCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(0), AndroidUtilities.dp(8), AndroidUtilities.dp(2));
        leftEditView = new EditText(context);
        leftEditView.setBackgroundResource(R.drawable.bg_light_gray);
        leftEditView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        leftEditView.setSingleLine(true);
        leftEditView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        leftEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (onBtnClickListener != null) {
                    onBtnClickListener.editTextChange();
                }
            }
        });
        LayoutParams leftViewParmas = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        leftViewParmas.weight = 1;
        addView(leftEditView, leftViewParmas);

        rightTextView = new TextView(context);
        rightTextView.setBackgroundResource(R.drawable.bg_gray);
        rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        rightTextView.setSingleLine(true);
        rightTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(0), AndroidUtilities.dp(8), AndroidUtilities.dp(0));
        LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        addView(rightTextView, infoViewParams);

        rightTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBtnClickListener != null) {
                    onBtnClickListener.onClick();
                }
            }
        });
    }

    public EditTagCell(Context context) {
        this(context, null, 0);
    }

    public void setRightViewText(String viewText, boolean needDivider) {
        this.needDivider = needDivider;
        rightTextView.setText(viewText);
        setWillNotDraw(!needDivider);
    }

    public String getEditText() {
        return leftEditView.getText().toString().trim();
    }

    public void setEditViewText(String editViewText) {
        leftEditView.setText(editViewText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(dividerLeftPadding, getHeight() - 1, getWidth() - dividerRightPadding, getHeight() - 1, paint);
        }
    }

    public void setDivider(boolean divider, int leftPadding, int rightPadding) {
        needDivider = divider;
        dividerLeftPadding = leftPadding;
        dividerRightPadding = rightPadding;
        setWillNotDraw(!divider);
    }
}

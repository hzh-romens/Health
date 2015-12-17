package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;


/**
 * Created by siery on 15/12/17.
 */
public class AllSelectCell extends FrameLayout {
    private CheckBox checkBox;
    private TextView checkBoxTextView;

    private TextView selectCountView;
    private ImageView actionView;

    private boolean checked = false;

    private static Paint paint;
    private boolean needDivider;

    public AllSelectCell(Context context) {
        super(context);
        init(context);
    }

    public AllSelectCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AllSelectCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        checkBox = new CheckBox(context);
        checkBox.setBackgroundColor(ResourcesConfig.primaryColor);
        checkBox.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        addView(checkBox, LayoutHelper.createFrame(36, 36, Gravity.LEFT | Gravity.CENTER_VERTICAL, 8, 0, 8, 0));

        checkBoxTextView = new TextView(context);
        checkBoxTextView.setTextColor(ResourcesConfig.bodyText1);
        checkBoxTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        checkBoxTextView.setLines(1);
        checkBoxTextView.setMaxLines(1);
        checkBoxTextView.setSingleLine(true);
        checkBoxTextView.setGravity(Gravity.LEFT);
        addView(checkBoxTextView, LayoutHelper.createFrame(48, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 56, 0, 16, 0));

        selectCountView = new TextView(context);
        selectCountView.setTextColor(ResourcesConfig.bodyText3);
        selectCountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        selectCountView.setLines(1);
        selectCountView.setMaxLines(1);
        selectCountView.setSingleLine(true);
        selectCountView.setGravity(Gravity.RIGHT);
        addView(selectCountView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 16, 0, 56, 0));

        actionView = new ImageView(context);
        actionView.setScaleType(ImageView.ScaleType.CENTER);
        actionView.setClickable(true);
        actionView.setBackgroundResource(R.drawable.list_selector);
        addView(actionView, LayoutHelper.createFrame(56, 48, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 8, 0, 0, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), View.MeasureSpec.EXACTLY));
    }

    public void setValue(boolean allChecked, int checkedCount, int actionResId, boolean divider) {
        setValue(allChecked, checkedCount, actionResId, 0,divider);
    }

    public void setValue(boolean allChecked, int checkedCount, int actionResId, int actionColor, boolean divider) {
        checked = allChecked;
        checkBox.setChecked(allChecked);
        checkBoxTextView.setText("全选");
        if (checkedCount <= 0) {
            selectCountView.setText("");
        } else {
            selectCountView.setText(String.format("已选(%d)", checkedCount));
        }
        actionView.setImageResource(actionResId);
        if (actionColor == 0) {
            actionView.clearColorFilter();
        } else {
            actionView.setColorFilter(actionColor);
        }
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setAllCheckBoxDelegate(CheckBox.OnCheckListener delegate) {
        checkBox.setOncheckListener(delegate);
    }

    public void setActionDelegate(View.OnClickListener delegate) {
        actionView.setOnClickListener(delegate);
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), 0, getWidth() - getPaddingRight(), 0, paint);
        }
    }
}

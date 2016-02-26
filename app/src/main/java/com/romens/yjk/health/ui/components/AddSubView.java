package com.romens.yjk.health.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * @author Zhou Lisi
 * @create 16/2/25
 * @description
 */
public class AddSubView extends LinearLayout {
    private TextView numTextView;

    private int number = 1;

    private boolean shouldZero = false;
    private AddSubDelegate addSubDelegate;

    public interface AddSubDelegate {
        void onNumSelect();

        boolean onNumberChanged(int number);
    }

    public AddSubView(Context context) {
        super(context);
        init(context);
    }

    public AddSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setShouldZero(boolean should) {
        shouldZero = should;
    }

    public void setAddSubDelegate(AddSubDelegate delegate) {
        addSubDelegate = delegate;
    }

    private void init(Context context) {
        final int borderColor = 0xffbdbdbd;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        ShapeDrawable shapeDrawable = createShape(borderColor);
        setPadding(AndroidUtilities.dp(1), AndroidUtilities.dp(1), AndroidUtilities.dp(1), AndroidUtilities.dp(1));
        setBackgroundDrawable(shapeDrawable);

        ImageView subView = new ImageView(context);
        subView.setClickable(true);
        subView.setBackgroundResource(R.drawable.list_selector);
        subView.setScaleType(ImageView.ScaleType.CENTER);
        subView.setColorFilter(borderColor);
        subView.setImageResource(R.drawable.ic_remove_grey600_24dp);
        addView(subView, LayoutHelper.createLinear(32, 32));

        numTextView = new TextView(context);
        numTextView.setClickable(true);
        numTextView.setBackgroundColor(0xfff0f0f0);
        numTextView.setTextColor(0xff757575);
        numTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        numTextView.setSingleLine(true);
        numTextView.setEllipsize(TextUtils.TruncateAt.END);
        numTextView.setGravity(Gravity.CENTER);
        numTextView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(2), AndroidUtilities.dp(8), AndroidUtilities.dp(2));
        addView(numTextView, LayoutHelper.createLinear(48, 32));

        ImageView addView = new ImageView(context);
        addView.setScaleType(ImageView.ScaleType.CENTER);
        addView.setClickable(true);
        addView.setBackgroundResource(R.drawable.list_selector);
        addView.setColorFilter(borderColor);
        addView.setImageResource(R.drawable.ic_add_grey600_24dp);
        addView(addView, LayoutHelper.createLinear(32, 32));

        subView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeValue(number - 1);
            }
        });

        addView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeValue(number + 1);
            }
        });

        numTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addSubDelegate != null) {
                    addSubDelegate.onNumSelect();
                }
            }
        });
        setValue(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32), View.MeasureSpec.EXACTLY));
    }

    public void setValue(int value) {
        if (shouldZero) {
            number = value < 0 ? 0 : value;
        } else {
            number = value <= 0 ? 1 : value;
        }
        numTextView.setText(String.valueOf(number));
    }

    private void changeValue(int value) {
        if (addSubDelegate != null) {
            boolean isValid = addSubDelegate.onNumberChanged(number);
            if (isValid) {
                setValue(value);
            }
        } else {
            setValue(value);
        }
    }

    public int getNumber() {
        return number;
    }

    private ShapeDrawable createShape(int borderColor) {
        int r = AndroidUtilities.dp(4);
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(borderColor);
        drawable.getPaint().setStyle(Paint.Style.STROKE);
        drawable.getPaint().setStrokeWidth(AndroidUtilities.dp(1));
        return drawable;
    }

//    public class NumTextView extends TextView {
//
//        private  Paint borderPaint;
//
//        public NumTextView(Context context, int borderColor) {
//            super(context);
//            if (borderPaint == null) {
//                borderPaint = new Paint();
//                borderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//                borderPaint.setColor(borderColor);
//                borderPaint.setStrokeWidth(AndroidUtilities.dp(1));
//            }
//        }
//
//        public void setValue(int value) {
//            setText("" + value);
//            setWillNotDraw(false);
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            final int x = getWidth();
//            final int y = getHeight();
//            canvas.drawLine(0, 0, 0, y, borderPaint);
//            canvas.drawLine(x, 0, x, y, borderPaint);
//        }
//    }
}

package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/12/20.
 */
public class LocationAddressInputCell extends LinearLayout {

    private static Paint paint;
    private boolean needDivider;

    private TextView textView;

    private Button provinceBtn;
    private Button cityBtn;
    private Button countyBtn;

    private boolean isMustInput;

    public LocationAddressInputCell(Context context) {
        super(context);
        init(context);
    }

    public LocationAddressInputCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LocationAddressInputCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(VERTICAL);
        textView = new TextView(context);
        textView.setTextColor(0x80000000);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.LEFT | Gravity.TOP);
        addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 17, 8, 17, 8));

        provinceBtn = new Button(context);
        provinceBtn.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        provinceBtn.setBackgroundResource(R.drawable.location_background);
        provinceBtn.setCompoundDrawablePadding(AndroidUtilities.dp(8));
        provinceBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_grey600_24dp, 0);
        provinceBtn.setTextColor(0xff212121);
        provinceBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        provinceBtn.setLines(1);
        provinceBtn.setMaxLines(1);
        provinceBtn.setSingleLine(true);
        provinceBtn.setEllipsize(TextUtils.TruncateAt.END);
        provinceBtn.setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= 21) {
            provinceBtn.setStateListAnimator(null);
        }
        addView(provinceBtn, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 17, 8, 17, 4));

        cityBtn = new Button(context);
        cityBtn.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        cityBtn.setBackgroundResource(R.drawable.location_background);
        cityBtn.setCompoundDrawablePadding(AndroidUtilities.dp(8));
        cityBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_grey600_24dp, 0);
        cityBtn.setTextColor(0xff212121);
        cityBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        cityBtn.setLines(1);
        cityBtn.setMaxLines(1);
        cityBtn.setSingleLine(true);
        cityBtn.setEllipsize(TextUtils.TruncateAt.END);
        cityBtn.setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= 21) {
            cityBtn.setStateListAnimator(null);
        }
        addView(cityBtn, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 17, 4, 17, 8));

        countyBtn = new Button(context);
        countyBtn.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        countyBtn.setBackgroundResource(R.drawable.location_background);
        countyBtn.setCompoundDrawablePadding(AndroidUtilities.dp(8));
        countyBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_grey600_24dp, 0);
        countyBtn.setTextColor(0xff212121);
        countyBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        countyBtn.setLines(1);
        countyBtn.setMaxLines(1);
        countyBtn.setSingleLine(true);
        countyBtn.setEllipsize(TextUtils.TruncateAt.END);
        countyBtn.setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= 21) {
            countyBtn.setStateListAnimator(null);
        }
        addView(countyBtn, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 17, 4, 17, 8));


        provinceBtn.setHint("请选择省");
        cityBtn.setHint("请选择城市");
        countyBtn.setHint("请选择区县");
    }

    public void setTextAndValue(String text, String province, String city, String county, boolean isMust, boolean divider) {
        isMustInput = isMust;
        if (isMust) {
            SpannableString caption = new SpannableString("* " + text);
            caption.setSpan(new ForegroundColorSpan(Color.RED), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(caption);
        } else {
            textView.setText(text);
        }
        provinceBtn.setText(province);
        cityBtn.setText(city);
        countyBtn.setText(county);
        if(TextUtils.isEmpty(province)){
            provinceBtn.setEnabled(true);
            cityBtn.setEnabled(false);
            countyBtn.setEnabled(false);
        }else if(TextUtils.isEmpty(city)){
            provinceBtn.setEnabled(true);
            cityBtn.setEnabled(true);
            countyBtn.setEnabled(false);
        }else{
            provinceBtn.setEnabled(true);
            cityBtn.setEnabled(true);
            countyBtn.setEnabled(true);
        }
        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public void setDelegate(View.OnClickListener provinceDelegate, View.OnClickListener cityDelegate, View.OnClickListener countyDelegate) {
        provinceBtn.setOnClickListener(provinceDelegate);
        cityBtn.setOnClickListener(provinceDelegate);
        countyBtn.setOnClickListener(provinceDelegate);
    }

    public void setProvinceEnable(boolean enable) {
        provinceBtn.setEnabled(enable);
    }

    public void setCityEnable(boolean enable) {
        cityBtn.setEnabled(enable);
    }

    public void setCountyEnable(boolean enable) {
        countyBtn.setEnabled(enable);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}

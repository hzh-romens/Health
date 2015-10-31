package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;

/**
 * Created by anlc on 2015/10/16.
 */
public class ImageAndTextCell extends LinearLayout {

    public TextView textView;
    public ImageView imageView;

    public ImageAndTextCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageAndTextCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        LayoutParams imgParams = LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        imgParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(imgParams);
        addView(imageView);

        textView = new TextView(context);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff757575);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LayoutParams textParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        textParams.setMargins(AndroidUtilities.dp(0), AndroidUtilities.dp(16), AndroidUtilities.dp(0), AndroidUtilities.dp(0));
        textView.setLayoutParams(textParams);
        addView(textView);
    }

    public ImageAndTextCell(Context context) {
        this(context, null);
    }

    public void setImageAndText(int imgResource, String text) {
        textView.setText(text);
        imageView.setBackgroundResource(imgResource);
    }

    public void setTextViewPadding(int left, int top, int right, int bottom) {
        textView.setPadding(left, top, right, bottom);
    }
}

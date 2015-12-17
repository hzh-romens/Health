package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/17.
 */
public class MedicineListCell extends FrameLayout {

    private CheckBox checkBox;

    private LinearLayout cellContainer;
    private BackupImageView iconView;

    private TextView nameView;
    private TextView descView;
    private TextView priceView;
    private TextView priceInfoView;

    private Button addShoppingCartBtn;

    private static Paint paint;
    private boolean needDivider;

    public MedicineListCell(Context context) {
        super(context);
        init(context);
    }

    public MedicineListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MedicineListCell(Context context, AttributeSet attrs, int defStyleAttr) {
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
        checkBox.setClickable(true);
        checkBox.setBackgroundColor(ResourcesConfig.primaryColor);
        checkBox.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        addView(checkBox, LayoutHelper.createFrame(36, 36, Gravity.LEFT | Gravity.CENTER_VERTICAL, 8, 8, 8, 8));

        cellContainer = new LinearLayout(context);
        cellContainer.setOrientation(LinearLayout.HORIZONTAL);
        cellContainer.setClickable(true);
        cellContainer.setBackgroundResource(R.drawable.list_selector);
        addView(cellContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 48, 0, 0, 0));

        iconView = new BackupImageView(context);
        cellContainer.addView(iconView, LayoutHelper.createLinear(64, 64, Gravity.TOP, 8, 8, 8, 8));

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        cellContainer.addView(content, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 8, 8, 16, 8));

        nameView = new TextView(context);
        nameView.setTextColor(ResourcesConfig.bodyText1);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameView.setLines(1);
        nameView.setMaxLines(2);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setGravity(Gravity.LEFT);
        content.addView(nameView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        descView = new TextView(context);
        descView.setTextColor(ResourcesConfig.bodyText2);
        descView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descView.setLines(1);
        descView.setMaxLines(1);
        descView.setSingleLine(true);
        descView.setEllipsize(TextUtils.TruncateAt.END);
        descView.setGravity(Gravity.LEFT);
        content.addView(descView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        priceView = new TextView(context);
        priceView.setTextColor(ResourcesConfig.bodyText2);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        priceView.setLines(1);
        priceView.setMaxLines(1);
        priceView.setSingleLine(true);
        priceView.setEllipsize(TextUtils.TruncateAt.END);
        priceView.setGravity(Gravity.LEFT);
        content.addView(priceView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,0,4,0,0));

        priceInfoView = new TextView(context);
        priceInfoView.setTextColor(ResourcesConfig.bodyText2);
        priceInfoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        priceInfoView.setLines(1);
        priceInfoView.setMaxLines(1);
        priceInfoView.setSingleLine(true);
        priceInfoView.setEllipsize(TextUtils.TruncateAt.END);
        priceInfoView.setGravity(Gravity.LEFT);
        content.addView(priceInfoView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        addShoppingCartBtn = new Button(context);
        addShoppingCartBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addShoppingCartBtn.setMaxLines(1);
        addShoppingCartBtn.setEllipsize(TextUtils.TruncateAt.END);
        addShoppingCartBtn.setSingleLine(true);
        addShoppingCartBtn.setTextColor(ResourcesConfig.bodyText1);
        addShoppingCartBtn.setBackgroundResource(R.drawable.add_shopping_cart_states);
        addShoppingCartBtn.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
        addShoppingCartBtn.setGravity(Gravity.CENTER);
        addShoppingCartBtn.setText("添加到购物车");
        content.addView(addShoppingCartBtn, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 32, Gravity.RIGHT, 8, 8, 8, 0));
    }

    public void setValue(boolean checkMode, boolean checked, String iconUrl, Drawable defaultIcon, CharSequence name, CharSequence desc, CharSequence price, CharSequence priceInfo, boolean showAdd, boolean divider) {
        checkBox.setVisibility(checkMode ? View.VISIBLE : View.GONE);
        checkBox.setChecked(checked);

        iconView.setImageUrl(iconUrl, "64_64", defaultIcon);
        nameView.setText(name);

        if (TextUtils.isEmpty(desc)) {
            descView.setVisibility(View.GONE);
            descView.setText("");
        } else {
            descView.setVisibility(View.VISIBLE);
            descView.setText(desc);
        }

        priceView.setText(price);

        if (TextUtils.isEmpty(priceInfo)) {
            priceInfoView.setVisibility(View.GONE);
            priceInfoView.setText("");
        } else {
            priceInfoView.setVisibility(View.VISIBLE);
            priceInfoView.setText(priceInfo);
        }


        addShoppingCartBtn.setVisibility(showAdd ? View.VISIBLE : View.GONE);

        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public void setCellDelegate(View.OnClickListener delegate) {
        cellContainer.setOnClickListener(delegate);
    }

    public void enableAddShoppingCartBtn(boolean enable, View.OnClickListener delegate) {
        addShoppingCartBtn.setEnabled(enable);
        addShoppingCartBtn.setOnClickListener(delegate);
    }

    public void setCheckBoxDelegate(CheckBox.OnCheckListener delegate) {
        checkBox.setOncheckListener(delegate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }


}

package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/17.
 */
public class MedicineListCell extends LinearLayout {
    private BackupImageView iconView;

    private TextView nameView;
    private TextView descView;
    private TextView priceView;
    private TextView priceInfoView;


    private ImageView favoritesView;
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
        setOrientation(VERTICAL);
        setClickable(true);
        setBackgroundResource(R.drawable.list_selector);

        LinearLayout cellContainer = new LinearLayout(context);
        cellContainer.setOrientation(LinearLayout.HORIZONTAL);

        addView(cellContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 48, 0, 0, 0));

        iconView = new BackupImageView(context);
        cellContainer.addView(iconView, LayoutHelper.createLinear(64, 64, Gravity.TOP, 8, 8, 8, 8));

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        cellContainer.addView(content, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 8, 8, 16, 8));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) content.getLayoutParams();
        layoutParams.weight = 1;
        content.setLayoutParams(layoutParams);


        favoritesView = new ImageView(context);
        favoritesView.setClickable(true);
        favoritesView.setScaleType(ImageView.ScaleType.CENTER);
        favoritesView.setBackgroundResource(R.drawable.list_selector);
        favoritesView.setColorFilter(ResourcesConfig.favoritesColor);
        cellContainer.addView(favoritesView, LayoutHelper.createFrame(48, 48, Gravity.RIGHT | Gravity.TOP));


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
        content.addView(priceView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 4, 0, 0));

        priceInfoView = new TextView(context);
        priceInfoView.setTextColor(ResourcesConfig.bodyText2);
        priceInfoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        priceInfoView.setLines(1);
        priceInfoView.setMaxLines(1);
        priceInfoView.setSingleLine(true);
        priceInfoView.setEllipsize(TextUtils.TruncateAt.END);
        priceInfoView.setGravity(Gravity.LEFT);
        content.addView(priceInfoView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        FrameLayout bottomContainer = new FrameLayout(context);
        addView(bottomContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));


        addShoppingCartBtn = new Button(context);
        addShoppingCartBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addShoppingCartBtn.setMaxLines(1);
        addShoppingCartBtn.setEllipsize(TextUtils.TruncateAt.END);
        addShoppingCartBtn.setSingleLine(true);
        addShoppingCartBtn.setTextColor(ResourcesConfig.textPrimary);
        addShoppingCartBtn.setBackgroundResource(R.drawable.btn_primary_border);
        addShoppingCartBtn.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
        addShoppingCartBtn.setGravity(Gravity.CENTER);
        addShoppingCartBtn.setText("添加到购物车");
        if (Build.VERSION.SDK_INT >= 21) {
            addShoppingCartBtn.setStateListAnimator(null);
        }
        bottomContainer.addView(addShoppingCartBtn, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 28, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 8, 0, 16, 0));
    }

    public void setValue(boolean enableFavorites, boolean isFavorites, String iconUrl, Drawable defaultIcon, CharSequence name, CharSequence desc, CharSequence price, CharSequence priceInfo, boolean showAdd, boolean divider) {
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

        favoritesView.setVisibility(enableFavorites ? View.VISIBLE : View.GONE);
        favoritesView.setImageResource(isFavorites ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);

        addShoppingCartBtn.setVisibility(showAdd ? View.VISIBLE : View.GONE);

        needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public void setCellDelegate(View.OnClickListener delegate) {
        setOnClickListener(delegate);
    }

    public void enableAddShoppingCartBtn(boolean enable, View.OnClickListener delegate) {
        addShoppingCartBtn.setEnabled(enable);
        addShoppingCartBtn.setOnClickListener(delegate);
    }

    public void setFavoritesDelegate(View.OnClickListener delegate) {
        favoritesView.setOnClickListener(delegate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }


}

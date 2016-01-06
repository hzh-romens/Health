package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineStoreCell extends FrameLayout {

    private TextView nameTextView;
    private TextView distanceTextView;
    private TextView addressTextView;
    private CloudImageView imageView;
    private boolean needDivider;
    private static Paint paint;
    private AvatarDrawable avatarDrawable;

    private TextView storeCountView;
    private Button addShoppingCartView;

    public MedicineStoreCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        imageView = CloudImageView.create(context);
        imageView.setRound(20);
        addView(imageView, LayoutHelper.createFrame(40, 40, Gravity.TOP | (Gravity.LEFT), 17, 10, 0, 0));

        LinearLayout topContainer = new LinearLayout(context);
        topContainer.setOrientation(LinearLayout.HORIZONTAL);
        topContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(topContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 72, 8, 16, 0));

        nameTextView = new TextView(context);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setMaxLines(1);
        nameTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        nameTextView.setSingleLine(true);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setGravity(Gravity.LEFT);
        topContainer.addView(nameTextView, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 0, 0, 8, 0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nameTextView.getLayoutParams();
        layoutParams.weight = 1;
        nameTextView.setLayoutParams(layoutParams);

        distanceTextView = new TextView(context);
        distanceTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        distanceTextView.setMaxLines(1);
        distanceTextView.setEllipsize(TextUtils.TruncateAt.END);
        distanceTextView.setSingleLine(true);
        distanceTextView.setTextColor(ResourcesConfig.bodyText2);
        distanceTextView.setGravity(Gravity.RIGHT);
        distanceTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        topContainer.addView(distanceTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        addressTextView = new TextView(context);
        addressTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addressTextView.setMaxLines(1);
        addressTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        addressTextView.setSingleLine(true);
        addressTextView.setTextColor(0xff999999);
        addressTextView.setGravity(Gravity.LEFT);
        addView(addressTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 72, 30, 16, 0));

        LinearLayout bottomContainer = new LinearLayout(context);
        bottomContainer.setOrientation(LinearLayout.VERTICAL);
        bottomContainer.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        addView(bottomContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 80, Gravity.BOTTOM, 72, 0, 16, 10));


        storeCountView = new TextView(context);
        storeCountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        storeCountView.setMaxLines(1);
        storeCountView.setEllipsize(TextUtils.TruncateAt.END);
        storeCountView.setSingleLine(true);
        storeCountView.setTextColor(ResourcesConfig.textPrimary);
        storeCountView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        bottomContainer.addView(storeCountView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 8, 8, 0));

        addShoppingCartView = new Button(context);
        addShoppingCartView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addShoppingCartView.setMaxLines(1);
        addShoppingCartView.setEllipsize(TextUtils.TruncateAt.END);
        addShoppingCartView.setSingleLine(true);
        addShoppingCartView.setTextColor(Color.WHITE);
        //addShoppingCartView.setTextColor(ResourcesConfig.textPrimary);
        addShoppingCartView.setBackgroundResource(R.drawable.btn_primary);
        addShoppingCartView.setText("加入购物车");
        addShoppingCartView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
        addShoppingCartView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            addShoppingCartView.setStateListAnimator(null);
        }
        bottomContainer.addView(addShoppingCartView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 32, 8, 8, 0, 0));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(136) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setValue(String storeIcon, String storeName, String storeAddress, int storeCount, BigDecimal price, boolean divider) {
        needDivider = divider;
        nameTextView.setText(storeName);
        addressTextView.setText(storeAddress);
        if (avatarDrawable == null) {
            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setSmallStyle(true);
        }
        avatarDrawable.setInfo(0, storeName);
        avatarDrawable.setColor(ResourcesConfig.primaryColor);
        imageView.setPlaceholderImage(avatarDrawable);
        imageView.setImagePath(storeIcon,AndroidUtilities.dp(40),AndroidUtilities.dp(40));

        distanceTextView.setText("");
        distanceTextView.setVisibility(View.GONE);

        String storeCountStr;
        int storeCountFontColor;
        int addShoppingCartResId;
        if (storeCount <= 0) {
            storeCountStr = "暂时无货";
            storeCountFontColor = ResourcesConfig.bodyText3;
            addShoppingCartResId = R.drawable.btn_border_grey;
        } else if (storeCount < 10) {
            storeCountStr = String.format("库存紧张 (%d)", storeCount);
            storeCountFontColor = ResourcesConfig.emergencyText;
            addShoppingCartResId = R.drawable.btn_border_emergency;
        } else if (storeCount < 999) {
            storeCountStr = String.format("现在有货 (%d)", storeCount);
            storeCountFontColor = Color.WHITE;
            addShoppingCartResId = R.drawable.btn_primary;
        } else {
            storeCountStr = "现在有货";
            storeCountFontColor = Color.WHITE;
            addShoppingCartResId = R.drawable.btn_primary;
        }

        SpannableString storeCountSpan = new SpannableString(storeCountStr);
        storeCountSpan.setSpan(new ForegroundColorSpan(storeCountFontColor), 0, storeCountStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        storeCountView.setText(storeCountSpan);

        addShoppingCartView.setBackgroundResource(addShoppingCartResId);
        addShoppingCartView.setTextColor(storeCountFontColor);
        DecimalFormat decimalFormat = new DecimalFormat("￥#,###.00");
        String priceStr = decimalFormat.format(price);
        String addShoppingCartText = String.format("加入购物车 %s", priceStr);
        addShoppingCartView.setText(addShoppingCartText);


        setWillNotDraw(!divider);
    }

    public void setDistance(double distance) {
        String distanceText;
        if (distance < 1) {
            distance = distance * 1000;
            distanceText = String.format("%d米", (int) distance);
        } else {
            distanceText = String.format("%d千米", (int) distance);
        }
        distanceTextView.setText(distanceText);
        distanceTextView.setVisibility(View.VISIBLE);
    }

    public void setAddShoppingCartDelegate(View.OnClickListener delegate) {
        addShoppingCartView.setOnClickListener(delegate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(72), getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }
}

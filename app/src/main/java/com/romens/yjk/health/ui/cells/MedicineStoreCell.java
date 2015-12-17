package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
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
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineStoreCell extends FrameLayout {

    private TextView nameTextView;
    private TextView distanceTextView;
    private TextView addressTextView;
    private BackupImageView imageView;
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
        imageView = new BackupImageView(context);
        imageView.setRoundRadius(15);
        imageView.setBackgroundResource(R.drawable.round_grey);
        imageView.setSize(AndroidUtilities.dp(30), AndroidUtilities.dp(30));
        imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(0xff999999, PorterDuff.Mode.MULTIPLY));
        addView(imageView, LayoutHelper.createFrame(40, 40, Gravity.TOP | (Gravity.LEFT), 17, 10, 0, 0));

        LinearLayout topContainer = new LinearLayout(context);
        topContainer.setOrientation(LinearLayout.HORIZONTAL);
        topContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(topContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 72, 8, 16, 0));

        nameTextView = new TextView(context);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setMaxLines(1);
        nameTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        nameTextView.setSingleLine(true);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setGravity(Gravity.LEFT);
        topContainer.addView(nameTextView, LayoutHelper.createFrame(0, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 0, 0, 8, 0));
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
        topContainer.addView(distanceTextView, new LinearLayout.LayoutParams(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        addressTextView = new TextView(context);
        addressTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addressTextView.setMaxLines(1);
        addressTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        addressTextView.setSingleLine(true);
        addressTextView.setTextColor(0xff999999);
        addressTextView.setGravity(Gravity.LEFT);
        addView(addressTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 72, 30, 16, 0));

        LinearLayout bottomContainer = new LinearLayout(context);
        bottomContainer.setOrientation(LinearLayout.HORIZONTAL);
        bottomContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(bottomContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM, 72, 0, 16, 10));


        storeCountView = new TextView(context);
        storeCountView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        storeCountView.setMaxLines(1);
        storeCountView.setEllipsize(TextUtils.TruncateAt.END);
        storeCountView.setSingleLine(true);
        storeCountView.setTextColor(ResourcesConfig.textPrimary);
        storeCountView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        bottomContainer.addView(storeCountView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 8, 8, 0));
        layoutParams = (LinearLayout.LayoutParams) storeCountView.getLayoutParams();
        layoutParams.weight = 1;
        storeCountView.setLayoutParams(layoutParams);

        addShoppingCartView = new Button(context);
        addShoppingCartView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addShoppingCartView.setMaxLines(1);
        addShoppingCartView.setEllipsize(TextUtils.TruncateAt.END);
        addShoppingCartView.setSingleLine(true);
        addShoppingCartView.setTextColor(ResourcesConfig.textPrimary);
        addShoppingCartView.setBackgroundResource(R.drawable.btn_primary_border);
        addShoppingCartView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
        addShoppingCartView.setGravity(Gravity.CENTER);
        addShoppingCartView.setText("加入购物车");
        bottomContainer.addView(addShoppingCartView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT,32, 8, 8, 0, 0));
        layoutParams = (LinearLayout.LayoutParams) addShoppingCartView.getLayoutParams();
        layoutParams.weight = 1;
        addShoppingCartView.setLayoutParams(layoutParams);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(104) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setValue(String storeIcon, String storeName, String storeAddress, int storeCount, boolean divider) {
        needDivider = divider;
        nameTextView.setText(storeName);
        addressTextView.setText(storeAddress);
        if (avatarDrawable == null) {
            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setSmallStyle(true);
        }
        avatarDrawable.setInfo(0, "店");
        avatarDrawable.setColor(ResourcesConfig.primaryColor);
        imageView.setImageUrl(storeIcon, null, avatarDrawable);

        distanceTextView.setText("");
        distanceTextView.setVisibility(View.GONE);

        storeCountView.setText(String.format("库存量(%s)", storeCount));
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

package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.LocationEntity;

public class LocationCell extends FrameLayout {

    private TextView nameTextView;
    private TextView addressTextView;
    private BackupImageView imageView;
    private boolean needDivider;
    private static Paint paint;
    private AvatarDrawable avatarDrawable;

    public LocationCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        imageView = new BackupImageView(context);
        imageView.setBackgroundResource(R.drawable.round_grey);
        imageView.setSize(AndroidUtilities.dp(30), AndroidUtilities.dp(30));
        imageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(0xff999999, PorterDuff.Mode.MULTIPLY));
        addView(imageView, LayoutHelper.createFrame(40, 40, Gravity.TOP | (Gravity.LEFT), 17, 8,0, 0));

        nameTextView = new TextView(context);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setMaxLines(1);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setSingleLine(true);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setGravity(Gravity.LEFT);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 72, 5, 16, 0));

        addressTextView = new TextView(context);
        addressTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addressTextView.setMaxLines(1);
        addressTextView.setEllipsize(TextUtils.TruncateAt.END);
        addressTextView.setSingleLine(true);
        addressTextView.setTextColor(0xff999999);
        addressTextView.setGravity(Gravity.LEFT);
        addView(addressTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 72, 30, 16, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setLocation(LocationEntity location, boolean divider) {
        needDivider = divider;
        nameTextView.setText(location.name);
        String distance = formatDistance(location.distance);
        addressTextView.setText(String.format("%s %s", distance, location.address));
        if (avatarDrawable == null) {
            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setSmallStyle(true);
        }
        avatarDrawable.setInfo(0, location.typeDesc);
        avatarDrawable.setColor(0xff999999);
        imageView.setImageUrl("", null, avatarDrawable);
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(72), getHeight() - 1, getWidth(), getHeight() - 1, paint);
        }
    }

    private String formatDistance(int distance) {
        if (distance <= 0) {
            return "";
        }
        if (distance <= 1000) {
            return String.format("(%d米)", distance);
        } else {
            return String.format("(%d千米)", distance / 1000);
        }
    }
}

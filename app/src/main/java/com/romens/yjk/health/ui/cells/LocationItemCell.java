package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.LocationEntity;

/**
 * Created by anlc on 2015/9/29.
 */
public class LocationItemCell extends FrameLayout {

    private TextView nameTextView;
    private TextView addressTextView;
    private BackupImageView imageView;
    private boolean needDivider;
    private static Paint paint;
    private AvatarDrawable avatarDrawable;

    private TextView ditanceTextView;

    public LocationItemCell(Context context) {
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
        addView(imageView, LayoutHelper.createFrame(40, 40, Gravity.TOP | (Gravity.LEFT), 17, 8, 0, 0));

        LinearLayout linearLayout = new LinearLayout(context);
        addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT, 72, 5, 16, 0));

        nameTextView = new TextView(context);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setMaxLines(1);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setSingleLine(true);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(nameTextView, new LinearLayout.LayoutParams(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        LinearLayout.LayoutParams nameParmas = (LinearLayout.LayoutParams) nameTextView.getLayoutParams();
        nameParmas.weight = 1;

        ditanceTextView = new TextView(context);
        ditanceTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        ditanceTextView.setMaxLines(1);
        ditanceTextView.setEllipsize(TextUtils.TruncateAt.END);
        ditanceTextView.setSingleLine(true);
        ditanceTextView.setTextColor(0x8a000000);
        ditanceTextView.setGravity(Gravity.RIGHT);
        Drawable drawable = getResources().getDrawable(R.drawable.address);
        ditanceTextView.setCompoundDrawables(drawable, null, null, null);
        ditanceTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(ditanceTextView, new LinearLayout.LayoutParams(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

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

    public void setLocation(String nameText, String address, double distance, boolean divider, String typeDesc) {
        needDivider = divider;
        nameTextView.setText(nameText);
        addressTextView.setText(address);
        String distanceText;
        if (distance < 1) {
            distance = distance * 1000;
            distanceText=String.format("%d米", (int) distance);
        } else {
            distanceText=String.format("%d千米", (int) distance);
        }
        ditanceTextView.setText(distanceText);
        //ditanceTextView.setText(distance + "km");
        if (avatarDrawable == null) {
            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setSmallStyle(true);
        }
        avatarDrawable.setInfo(0, typeDesc);
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

    private String formatDistance(double distance) {
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

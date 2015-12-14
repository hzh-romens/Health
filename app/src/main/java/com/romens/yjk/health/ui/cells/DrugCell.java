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
import com.romens.android.ui.Components.CheckBox;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;

/**
 * Created by siery on 15/10/29.
 */
public class DrugCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private TextView nameTextView;
    private TextView descTextView;
    private CheckBox checkBox;

    private AvatarDrawable avatarDrawable;

    private static Paint paint;
    private boolean needDivider;

    public DrugCell(Context context) {
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }

        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(15));

        avatarImageView.setBackgroundResource(R.drawable.rm_round_grey);
        avatarImageView.setSize(AndroidUtilities.dp(30), AndroidUtilities.dp(30));
        avatarImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(0xff999999, PorterDuff.Mode.MULTIPLY));

        addView(avatarImageView, LayoutHelper.createFrame(40, 40, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));
        avatarDrawable = new AvatarDrawable();

        nameTextView = new TextView(context);
        nameTextView.setTextColor(0xffa8a8a8);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        nameTextView.setLines(1);
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 68, 10, 16, 0));

        descTextView = new TextView(context);
        nameTextView.setTextColor(0xff212121);
        descTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        descTextView.setLines(1);
        descTextView.setMaxLines(1);
        descTextView.setSingleLine(true);
        descTextView.setEllipsize(TextUtils.TruncateAt.END);
        descTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        addView(descTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 68, 33, 16, 0));

        checkBox = new CheckBox(context, R.drawable.round_check2);
        checkBox.setVisibility(INVISIBLE);
        addView(checkBox, LayoutHelper.createFrame(22, 22, Gravity.LEFT, 37, 38, 0, 0));
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checkBox.getVisibility() != VISIBLE) {
            checkBox.setVisibility(VISIBLE);
        }
        checkBox.setChecked(checked, animated);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), MeasureSpec.EXACTLY));
    }

    public void setValue(String drugType, CharSequence drugName, CharSequence drugDesc, boolean divider) {

        avatarDrawable.setSmallStyle(true);
        avatarDrawable.setInfo(drugType);
        avatarDrawable.setColor(0xff0f9d58);
        avatarImageView.setImageDrawable(avatarDrawable);

        nameTextView.setText(drugName);

        descTextView.setText(drugDesc);


        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(AndroidUtilities.dp(68), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}

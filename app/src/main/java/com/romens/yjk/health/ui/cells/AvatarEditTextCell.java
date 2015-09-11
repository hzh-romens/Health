package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;

/**
 * Created by anlc on 2015/9/10.
 */
public class AvatarEditTextCell extends LinearLayout {
    private static Paint paint;
    private BackupImageView avatarImage;
    private EditText valueEditText;
    private ImageView actionImageView;


    private AvatarDrawable avatarDrawable;
    private boolean needDivider = false;
    private int dividerLeftPadding=0;
    private int dividerRightPadding=0;

    private AvatarEditTextCellDelegate cellDelegate;

    public AvatarEditTextCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setOrientation(HORIZONTAL);
        avatarImage = new BackupImageView(context);
        avatarImage.setRoundRadius(AndroidUtilities.dp(15));
        addView(avatarImage, LayoutHelper.createLinear(40, 40, Gravity.CENTER_VERTICAL, 8, 8, 8, 8));

        valueEditText = new EditText(context);
        valueEditText.setBackgroundColor(Color.TRANSPARENT);
        valueEditText.setTextColor(0xff212121);
        valueEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        valueEditText.setSingleLine(true);
        valueEditText.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
        addView(valueEditText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) valueEditText.getLayoutParams();
        layoutParams.weight = 1;
        valueEditText.setLayoutParams(layoutParams);
        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0) {
                    onInputTextChanged(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (cellDelegate != null) {
                    cellDelegate.onValueChanged(s.toString());
                }
            }
        });

        actionImageView = new ImageView(context);
        actionImageView.setClickable(true);
        actionImageView.setBackgroundResource(R.drawable.list_selector);
        actionImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cellDelegate != null) {
                    cellDelegate.onAction();
                }
            }
        });
        addView(actionImageView, LayoutHelper.createLinear(32, 32, Gravity.CENTER_VERTICAL, 8, 8, 8, 8));
        valueEditText.setText("");    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(dividerLeftPadding, getHeight() - 1, getWidth()-dividerRightPadding, getHeight() - 1, paint);
        }
    }

    public void setDivider(boolean divider,int leftPadding,int rightPadding) {
        needDivider = divider;
        dividerLeftPadding=leftPadding;
        dividerRightPadding=rightPadding;
        setWillNotDraw(!divider);
    }

    public void setValue(CharSequence text) {
        valueEditText.setText(text);
    }

    public String getValue(){
        return valueEditText.getText().toString().trim();
    }

    public void setValueHint(CharSequence hintText){
        valueEditText.setHint(hintText);
    }

    public void setActionImageResource(int resId){
        actionImageView.setImageResource(resId);
    }

    private void onInputTextChanged(String text) {
        if (avatarDrawable == null) {
            avatarDrawable = new AvatarDrawable(true);
        }
        avatarDrawable.setInfo(0, text);
        avatarDrawable.setColor(getResources().getColor(R.color.theme_primary));
        avatarImage.setImageDrawable(avatarDrawable);
    }

    public void setAvatarEditTextCellDelegate(AvatarEditTextCellDelegate delegate){
        cellDelegate=delegate;
    }

    public interface AvatarEditTextCellDelegate{
        void onValueChanged(String value);
        void onAction();
    }
}

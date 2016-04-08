package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.helper.FormatHelper;

/**
 * Created by HZH on 2015/11/9.
 */
public class NewUserProfileCell extends FrameLayout {
    private CloudImageView userBackgroundView;
    private CloudImageView userAvatarView;
    private TextView userNameView;

    private TextView userIntegralView;
    private TextView userCouponView;
    private TextView userRemainMoneyView;
    private LinearLayout subLayout;


    public NewUserProfileCell(Context context) {
        super(context);

        userBackgroundView = CloudImageView.create(context);
        userBackgroundView.setImagePath(R.drawable.ic_userprofile);
        addView(userBackgroundView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));

        userAvatarView = CloudImageView.create(context);
        addView(userAvatarView, LayoutHelper.createFrame(48, 48, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 17, 32, 17, 8));

        userNameView = new TextView(context);
        userNameView.setSingleLine(true);
        userNameView.setEllipsize(TextUtils.TruncateAt.END);
        userNameView.setTextColor(0xffffffff);
        userNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        userNameView.setGravity(Gravity.CENTER);
        addView(userNameView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 17, 86, 17, 8));

        subLayout = new LinearLayout(context) {
            Paint paint;

            @Override
            public void draw(Canvas canvas) {
                super.draw(canvas);
                if (paint == null) {
                    paint = new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setStyle(Paint.Style.STROKE);
                }
                canvas.drawLine(subLayout.getWidth() / 3, AndroidUtilities.dp(8),
                        subLayout.getWidth() / 3, subLayout.getMeasuredHeight() - AndroidUtilities.dp(8), paint);
                canvas.drawLine(subLayout.getWidth() / 3 * 2, AndroidUtilities.dp(8),
                        subLayout.getWidth() / 3 * 2, subLayout.getMeasuredHeight() - AndroidUtilities.dp(8), paint);

            }
        };
        subLayout.setBackgroundColor(0x99333333);
        subLayout.setOrientation(LinearLayout.HORIZONTAL);

        userIntegralView = new TextView(context);
        userIntegralView.setSingleLine();
        userIntegralView.setEllipsize(TextUtils.TruncateAt.END);
        userIntegralView.setTextColor(0xffffffff);
        userIntegralView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        userIntegralView.setText("积分0");
        userIntegralView.setGravity(Gravity.CENTER);
        userIntegralView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        LinearLayout.LayoutParams integralParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        integralParams.weight = 1;
        subLayout.addView(userIntegralView, integralParams);

        userRemainMoneyView = new TextView(context);
        userRemainMoneyView.setSingleLine();
        userRemainMoneyView.setEllipsize(TextUtils.TruncateAt.END);
        userRemainMoneyView.setTextColor(0xffffffff);
        userRemainMoneyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        userRemainMoneyView.setText("余额0");
        userRemainMoneyView.setGravity(Gravity.CENTER);
        userRemainMoneyView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        LinearLayout.LayoutParams remainParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        remainParams.weight = 1;
        subLayout.addView(userRemainMoneyView, remainParams);

        userCouponView = new TextView(context);
        userCouponView.setSingleLine();
        userCouponView.setEllipsize(TextUtils.TruncateAt.END);
        userCouponView.setTextColor(0xffffffff);
        userCouponView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        userCouponView.setText("优惠券0");
        userCouponView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        userCouponView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams couponParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        couponParams.weight = 1;
        subLayout.addView(userCouponView, couponParams);
        addView(subLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        userCouponView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null) {
                    onViewClickListener.onCouponClick();
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(152), View.MeasureSpec.EXACTLY));
    }

    public void setUser(UserEntity userEntity, int integral, double remainMoney, int coupon, String sex) {
        userAvatarView.setRound(40);
        if (TextUtils.isEmpty(userEntity.getAvatar())) {
            if (sex.equals("1")) {
                userAvatarView.setImagePath(R.drawable.user_default_icon_boy);
            } else if (sex.equals("0")) {
                userAvatarView.setImagePath(R.drawable.user_default_icon_girl);
            } else {
                userAvatarView.setImagePath(R.drawable.user_default_icon);
            }
        } else {
            userAvatarView.setImagePath(userEntity.getAvatar());
        }
        userNameView.setText(userEntity.getPhone());
        userIntegralView.setText(String.format("积分 %d", integral));
        userRemainMoneyView.setText(String.format("余额 %s", FormatHelper.format(remainMoney)));
        userCouponView.setText(String.format("优惠券 %d", coupon));
    }

    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener {
        void onCouponClick();
    }
}
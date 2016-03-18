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
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.images.ui.CloudImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.db.entity.UserEntity;

/**
 * Created by HZH on 2015/11/9.
 */
public class NewUserProfileCell extends FrameLayout {
    private CloudImageView userBackgroundView;
    private CloudImageView userAvatarView;
    private TextView userNameView;

    private TextView userIntegralView;
    private TextView userCouponView;
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
                canvas.drawLine(subLayout.getWidth() / 2, AndroidUtilities.dp(8),
                        subLayout.getWidth() / 2, subLayout.getMeasuredHeight() - AndroidUtilities.dp(8), paint);

            }
        };
        subLayout.setBackgroundColor(0x99333333);
        subLayout.setOrientation(LinearLayout.HORIZONTAL);

        userIntegralView = new TextView(context);
        userIntegralView.setSingleLine();
        userIntegralView.setEllipsize(TextUtils.TruncateAt.END);
        userIntegralView.setTextColor(0xffffffff);
        userIntegralView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        userIntegralView.setText("积分余额0");
        userIntegralView.setGravity(Gravity.CENTER);
        userIntegralView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        LinearLayout.LayoutParams integralParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        integralParams.weight = 1;
        subLayout.addView(userIntegralView, integralParams);

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(152), View.MeasureSpec.EXACTLY));
    }

    public void setUser(UserEntity userEntity) {
//        AvatarDrawable avatarDrawable = new AvatarDrawable(true);
//        avatarDrawable.setInfo(0, userEntity.getAvatar(), "a", false);
//        avatarDrawable.setColor(ResourcesConfig.primaryColor);
//        userAvatarView.setPlaceholderImage(avatarDrawable);
        userAvatarView.setRound(40);
        if (TextUtils.isEmpty(userEntity.getAvatar())) {
            userAvatarView.setImagePath(R.drawable.user_default_icon);
        } else {
            userAvatarView.setImagePath(userEntity.getAvatar());
        }
        userNameView.setText(userEntity.getPhone());
    }
}

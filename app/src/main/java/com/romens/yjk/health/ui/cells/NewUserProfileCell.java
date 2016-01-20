package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
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
public class NewUserProfileCell extends FrameLayout{
    private CloudImageView userBackgroundView;
    private CloudImageView userAvatarView;
    private TextView userNameView;
    public NewUserProfileCell(Context context) {
        super(context);

        userBackgroundView=CloudImageView.create(context);
        addView(userBackgroundView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.MATCH_PARENT,Gravity.CENTER));

        userAvatarView=CloudImageView.create(context);
        addView(userAvatarView, LayoutHelper.createFrame(48,48,Gravity.TOP|Gravity.CENTER_HORIZONTAL,17,48,17,8));

        userNameView=new TextView(context);
        userNameView.setSingleLine(true);
        userNameView.setEllipsize(TextUtils.TruncateAt.END);
        userNameView.setTextColor(0xffffffff);
        userNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        userNameView.setGravity(Gravity.CENTER);
        addView(userNameView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 17, 8, 17, 8));
        userBackgroundView.setImagePath(R.drawable.ic_userprofile);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(152), View.MeasureSpec.EXACTLY));
    }

    public void setUser(UserEntity userEntity) {
        AvatarDrawable avatarDrawable = new AvatarDrawable(true);
        avatarDrawable.setInfo(0, userEntity.getAvatar(), "", false);
        avatarDrawable.setColor(ResourcesConfig.primaryColor);
        userAvatarView.setPlaceholderImage(avatarDrawable);
        userAvatarView.setImagePath(userEntity.getAvatar());
        userNameView.setText(userEntity.getPhone());
    }
}

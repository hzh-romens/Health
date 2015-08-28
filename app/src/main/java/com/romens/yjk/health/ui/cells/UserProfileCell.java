package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.model.UserEntity;

/**
 * Created by siery on 15/8/17.
 */
public class UserProfileCell extends FrameLayoutFixed {
    private BackupImageView avatarImage;
    private TextView nameView;

    public UserProfileCell(Context context) {
        super(context);
        setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
        avatarImage = new BackupImageView(context);
        avatarImage.setRoundRadius(AndroidUtilities.dp(30));
        addView(avatarImage, LayoutHelper.createFrame(64, 64, Gravity.LEFT | Gravity.TOP));

        nameView = new TextView(context);
        nameView.setSingleLine(true);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setTextColor(0xff212121);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.LEFT | Gravity.TOP, 96, 8, 0, 8));
    }

    public void setUser(UserEntity userEntity) {
        AvatarDrawable avatarDrawable = new AvatarDrawable(true);
        avatarDrawable.setColor(0xff5c98cd);
        avatarDrawable.setInfo(0, userEntity.userName, "", false);
        avatarImage.setImageUrl(userEntity.avatarUrl, "64_64", avatarDrawable);

        nameView.setText(userEntity.userName);
    }
}
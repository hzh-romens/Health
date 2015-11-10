package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.db.entity.UserEntity;

/**
 * Created by HZH on 2015/11/9.
 */
public class NewUserProfileCell extends FrameLayout{
    private TextView tv_point,tv_activity,nameValue;
    private BackupImageView avatarImage;
    public NewUserProfileCell(Context context) {
        super(context);
        View view=View.inflate(context, R.layout.list_item_user,null);
        tv_point= (TextView) view.findViewById(R.id.tv_point);
        tv_activity= (TextView) view.findViewById(R.id.tv_activity);
        nameValue= (TextView) view.findViewById(R.id.nameValue);
        avatarImage= (BackupImageView) view.findViewById(R.id.avatarImage);
        avatarImage.setRoundRadius(AndroidUtilities.dp(30));
        addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

    }
    public void setUser(UserEntity userEntity) {
        AvatarDrawable avatarDrawable = new AvatarDrawable(true);
        avatarDrawable.setInfo(0, userEntity.getAvatar(), "", false);
        avatarDrawable.setColor(ResourcesConfig.primaryColor);
        avatarImage.setImageUrl(userEntity.getAvatar(), "64_64", avatarDrawable);
        nameValue.setText(userEntity.getPhone());
        tv_activity.setText("优惠券  0");
        tv_point.setText("积分  0");
    }
}

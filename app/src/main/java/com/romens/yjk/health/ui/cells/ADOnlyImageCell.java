package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.FrameLayoutFixed;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by siery on 15/8/13.
 */
public class ADOnlyImageCell extends FrameLayoutFixed {
    private BackupImageView adImageView;


    public ADOnlyImageCell(Context context) {
        super(context);
        adImageView = new BackupImageView(context);
        adImageView.setRoundRadius(AndroidUtilities.dp(4));
        addView(adImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.CENTER));
    }

    public void setImage(String httpUrl, String filter, Drawable thumb) {
        adImageView.setImage(httpUrl, filter, thumb);
    }
}

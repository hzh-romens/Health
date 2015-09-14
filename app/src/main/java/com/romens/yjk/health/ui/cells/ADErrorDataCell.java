package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/9/11.
 */
public class ADErrorDataCell extends FrameLayout{
    private ImageView imageView;
    public ADErrorDataCell(Context context) {
        super(context);
        setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        imageView=new ImageView(context);
        imageView.setBackground(getResources().getDrawable(R.drawable.error));
        addView(imageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));
    }
}

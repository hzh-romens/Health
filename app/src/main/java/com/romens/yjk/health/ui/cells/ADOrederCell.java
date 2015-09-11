package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by AUSU on 2015/9/11.
 * 我的订单Item
 */

public class ADOrederCell extends FrameLayout{
    private BackupImageView imageView;
    private TextView textView;
    private TextView tv_comment;

    private static Paint paint;
    private boolean needDivider = false;

    public ADOrederCell(Context context) {
        super(context);
        if(paint==null){
            paint=new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setPadding(AndroidUtilities.dp(8),AndroidUtilities.dp(8),AndroidUtilities.dp(8),AndroidUtilities.dp(8));
        imageView=new BackupImageView(context);
        imageView.setRoundRadius(2);
        addView(imageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 140, Gravity.CENTER | Gravity.LEFT, 8, 0, 8, 0));
        //FrameLayout frameLayout=new FrameLayout(context);
        textView=new TextView(context);
        textView.setTextColor(0xffffffff);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(2);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);



    }
}

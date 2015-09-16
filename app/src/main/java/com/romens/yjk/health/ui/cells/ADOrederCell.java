package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
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
        addView(imageView, LayoutHelper.createFrame(64,64, Gravity.TOP | Gravity.LEFT, 8, 0, 8, 0));
        //FrameLayout frameLayout=new FrameLayout(context);
        textView=new TextView(context);
        textView.setTextColor(0xff212121);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(2);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP|Gravity.RIGHT, 80, 0, 8, 0));
        tv_comment=new TextView(context);
        tv_comment.setTextColor(0xff212121);
        tv_comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addView(tv_comment,LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT,LayoutHelper.WRAP_CONTENT,Gravity.BOTTOM|Gravity.RIGHT,8,0,8,0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(needDivider){
            canvas.drawLine(AndroidUtilities.dp(16),getHeight()-1,getWidth()-AndroidUtilities.dp(16),getHeight()-1,paint);
        }
    }
    public void setValue(String info, String iconUrl,String comment, boolean divider) {
        textView.setText(info);
        imageView.setImage(iconUrl, "64_64", null);
        needDivider = divider;
        tv_comment.setText(comment);
        setWillNotDraw(!divider);
    }
}
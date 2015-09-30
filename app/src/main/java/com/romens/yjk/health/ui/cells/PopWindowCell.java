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
import com.romens.android.ui.Image.AvatarDrawable;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by AUSU on 2015/9/12.
 */
public class PopWindowCell extends FrameLayout{
    private BackupImageView imageView;
    private TextView textView;
    private Paint paint;
    public PopWindowCell(Context context) {
        super(context);
        if(paint==null){
            paint=new Paint();
            paint.setColor(0xffd9d9d9);
            paint.setStrokeWidth(1);
        }
        setPadding(AndroidUtilities.dp(8),AndroidUtilities.dp(8),AndroidUtilities.dp(8),AndroidUtilities.dp(8));
        imageView=new BackupImageView(context);
        imageView.setRoundRadius(AndroidUtilities.dp(16));
        addView(imageView, LayoutHelper.createFrame(32, 32, Gravity.CENTER_VERTICAL | Gravity.LEFT, 8, 0, 8, 0));
        //FrameLayout frameLayout=new FrameLayout(context);
        textView=new TextView(context);
        textView.setTextColor(0xffffffff);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(2);
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER_VERTICAL|Gravity.RIGHT, 48, 0, 8, 0));
    }
    @Override
    protected void onDraw(Canvas canvas) {
            canvas.drawLine(AndroidUtilities.dp(16),getHeight()-1,getWidth()-AndroidUtilities.dp(16),getHeight()-1,paint);
    }
    public void setValue(String info, String iconUrl) {
        textView.setText(info);
        imageView.setImage(iconUrl, "64_64", null);
    }


}

package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.Image.BackupImageView;

/**
 * Created by AUSU on 2015/9/16.
 */
public class DrugStoreCell extends FrameLayout {

    private BackupImageView imageView;
    private TextView name;
    private TextView comment;

    public DrugStoreCell(Context context) {
        super(context);
        setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        imageView = new BackupImageView(context);
        imageView.setRoundRadius(2);
        addView(imageView, LayoutHelper.createFrame(64, 64, Gravity.TOP | Gravity.LEFT, 8, 0, 8, 0));
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        name=new TextView(context);
        name.setTextColor(0xff212121);
        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        name.setLines(2);
        name.setMaxLines(2);
        name.setEllipsize(TextUtils.TruncateAt.END);
        ll.addView(name, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 0, 8, 0));
        comment=new TextView(context);
        comment.setTextColor(0xff212121);
        comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        comment.setLines(2);
        comment.setMaxLines(2);
        comment.setEllipsize(TextUtils.TruncateAt.END);
        ll.addView(comment,LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 8, 8, 8));
        addView(ll, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.NO_GRAVITY, 80, 0, 8, 0));
    }
    public void setValue(String names,String info,String url){
        name.setText(names);
        comment.setText(info);
        imageView.setImage(url, "64_64", null);
    }
}

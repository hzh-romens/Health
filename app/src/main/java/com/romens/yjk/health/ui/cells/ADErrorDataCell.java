package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/9/11.
 */
public class ADErrorDataCell extends FrameLayout{
    private TextView errorMsg;
    public ADErrorDataCell(Context context) {
        super(context);
        View view=View.inflate(context,R.layout.list_item_error,null);
        errorMsg= (TextView) view.findViewById(R.id.errorMsg);
        LinearLayout layout_msg= (LinearLayout) view.findViewById(R.id.layout_msg);
        addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }
    public void setValue(String msg){
        errorMsg.setText(msg);
    }
}

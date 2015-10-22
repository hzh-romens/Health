package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.LocationActivity;

/**
 * Created by AUSU on 2015/10/14.
 */
public class ADMoreCell extends FrameLayout{
    private TextView tv_more;
    public ADMoreCell(final Context context) {
        super(context);
       View view=View.inflate(context, R.layout.list_item_medicinal_more,null);
        tv_more= (TextView) view.findViewById(R.id.tv_more);
        tv_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, LocationActivity.class));
            }
        });
        addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT));
    }
}

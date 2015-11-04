package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;

/**
 * Created by AUSU on 2015/10/15.
 */
public class ADGroupNameCell extends FrameLayout{
    private TextView tv_groupName,moreButton;
    public ADGroupNameCell(Context context) {
        super(context);
        View view=View.inflate(context, R.layout.list_item_group_name,null);
        tv_groupName= (TextView) view.findViewById(R.id.group_name);
        tv_groupName.setTextSize(16);
        moreButton= (TextView) view.findViewById(R.id.moreButton);
        addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,LayoutHelper.WRAP_CONTENT));
    }

    /**
     *
     * @param str_group groupName
     * @param flag 判断是否显示更多的那个按钮
     */
    public void setValue(String str_group,boolean flag){
        tv_groupName.setText(str_group);
        if(flag){
            moreButton.setVisibility(VISIBLE);
        }else{
            moreButton.setVisibility(GONE);
        }
    }
}

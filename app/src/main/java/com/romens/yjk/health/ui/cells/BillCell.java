package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.gc.materialdesign.views.Switch;
import com.romens.yjk.health.R;

/**
 * Created by HZH on 2016/2/2.
 */
public class BillCell extends FrameLayout {
    private Switch switchBtn;
    private EditText editText;

    public BillCell(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.list_item_bill, null);
        switchBtn = (Switch) view.findViewById(R.id.btn_switch);
        editText = (EditText) view.findViewById(R.id.edit_bill);
        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void setValue(String value){

    }
}

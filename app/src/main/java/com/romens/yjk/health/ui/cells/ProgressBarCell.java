package com.romens.yjk.health.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.components.LineProgressBar;

/**
 * Created by AUSU on 2016/1/2.
 */
public class ProgressBarCell extends FrameLayout {
    private TextView tv_name, tv_value;
    private LineProgressBar progressBar;

    public ProgressBarCell(Context context) {
        super(context);
        View v= View.inflate(context, R.layout.list_item_progress,null);
        tv_value= (TextView) v.findViewById(R.id.tv_value);
        tv_name= (TextView) v.findViewById(R.id.tv_name);
        progressBar= (LineProgressBar) v.findViewById(R.id.pb);
        addView(v);
    }

    public void setValue(int max,int progress){
        progressBar.setMax(max);
        progressBar.setCurrentCount(progress);
        progressBar.setSecondCount(0);
    }
}

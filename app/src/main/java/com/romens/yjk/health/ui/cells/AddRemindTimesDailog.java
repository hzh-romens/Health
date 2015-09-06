package com.romens.yjk.health.ui.cells;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.AddRemindActivity;
import com.romens.yjk.health.ui.adapter.TimesAdapter;
import com.romens.yjk.health.ui.components.AddRemindCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/1.
 */
public class AddRemindTimesDailog {

    private Context context;
    private Dialog timesCountDialog;
    private TimesAdapter timesAdapter;
    private List<String> timesData;
    private int oldTimes;
    private AddRemindCallback callback;
    private AddRemindTimesDailog dailogInstace;

    public AddRemindTimesDailog(Context context, List<String> timesData, int oldTimes) {
        dailogInstace = this;
        this.context = context;
        this.timesData = timesData;
        this.oldTimes = oldTimes;
        callback = (AddRemindCallback) context;
        timesCountDialog = new AlertDialog.Builder(context).create();
    }

    public void setTimesData(List<String> timesData) {
        this.timesData = timesData;
    }

    public void show() {
        //设置1天的提醒次数的dialog
        View timesCountView = LayoutInflater.from(context).inflate(R.layout.dialog_times_count, null);
        ListView timesList = (ListView) timesCountView.findViewById(R.id.times_count_list);
        timesAdapter = new TimesAdapter(timesData, context, dailogInstace);
        timesList.setAdapter(timesAdapter);

        ImageView minusBtn = (ImageView) timesCountView.findViewById(R.id.times_count_minus);
        ImageView addBtn = (ImageView) timesCountView.findViewById(R.id.times_count_add);
        TextView cancel = (TextView) timesCountView.findViewById(R.id.times_count_cancel);
        TextView confirm = (TextView) timesCountView.findViewById(R.id.times_count_confirm);
        final TextView timesTv = (TextView) timesCountView.findViewById(R.id.times_count_count);
        oldTimes = timesData.size();
        timesTv.setText(oldTimes + "次");
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timesData.size() > 1) {
                    timesData.remove(timesData.size() - 1);
                }
                timesTv.setText(timesData.size() + "次");
                timesAdapter.notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timesData.size() < 5) {
                    timesData.add("08:30");
                }
                timesTv.setText(timesData.size() + "次");
                timesAdapter.notifyDataSetChanged();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.setCountsText(oldTimes);
                timesCountDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.setTimesDate(timesData);
                oldTimes = timesData.size();
                callback.setCountsText(oldTimes);
                callback.setTimesHintText(oldTimes);
                timesCountDialog.dismiss();
            }
        });

        timesCountDialog.show();
        timesCountDialog.getWindow().setContentView(timesCountView);
    }
}

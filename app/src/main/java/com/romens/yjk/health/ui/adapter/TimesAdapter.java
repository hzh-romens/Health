package com.romens.yjk.health.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.library.datetimepicker.time.RadialPickerLayout;
import com.romens.android.library.datetimepicker.time.TimePickerDialog;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.TimesAdapterCallBack;
import com.romens.yjk.health.ui.AddRemindActivityNew;
import com.romens.yjk.health.ui.BaseActivity;
import com.romens.yjk.health.ui.cells.AddRemindTimesDailog;

import java.util.Calendar;
import java.util.List;

/**
 * Created by anlc on 2015/8/27.
 * 用药提醒中显示次数的adapter
 */
public class TimesAdapter extends BaseAdapter implements TimePickerDialog.OnTimeSetListener {
    //设置1天的提醒次数的适配器
    private List<String> data;
    private Context context;
    public static final String TIMEPICKER_TAG = "timepicker";
    private int index;
    private TimesAdapterCallBack dailog;

    public TimesAdapter(List<String> data, Context context, TimesAdapterCallBack callBack) {
        this.dailog = callBack;
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TimesViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_text, null);
            holder = new TimesViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.list_item_text);
            convertView.setTag(holder);
        } else {
            holder = (TimesViewHolder) convertView.getTag();
        }
        holder.textView.setText(data.get(position));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(TimesAdapter.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
                timePickerDialog.setVibrate(true);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(((BaseActivity) context).getSupportFragmentManager(), TIMEPICKER_TAG);
                index = position;
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeDialogView(position);
                return true;
            }
        });
        convertView.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    public void removeDialogView(final int position) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        TextView textView = new TextView(context);

        textView.setBackgroundResource(R.drawable.bg_white);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        textView.setText("删除");
        textView.setTextColor(context.getResources().getColor(R.color.theme_primary));
        textView.setPadding(AndroidUtilities.dp(32), AndroidUtilities.dp(8), AndroidUtilities.dp(32), AndroidUtilities.dp(8));
        LinearLayout.LayoutParams infoViewParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT);
        infoViewParams.weight = 1;
        infoViewParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(infoViewParams);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.size() > 1) {
                    data.remove(position);
                } else {
                    Toast.makeText(context, "默认选项不能删除", Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setContentView(textView);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String minuteStr = minute + "";
        if (minute < 10) {
            minuteStr = "0" + minute;
        }
        data.set(index, hourOfDay + ":" + minuteStr);
        dailog.setTimesData(data);
        notifyDataSetChanged();
    }

    class TimesViewHolder {
        private TextView textView;
    }
}



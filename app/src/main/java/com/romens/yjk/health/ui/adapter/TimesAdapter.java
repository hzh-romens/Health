package com.romens.yjk.health.ui.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.romens.android.library.datetimepicker.time.RadialPickerLayout;
import com.romens.android.library.datetimepicker.time.TimePickerDialog;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.TimesAdapterCallBack;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_add_time, null);
            holder = new TimesViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.remind_add_time_text);
            convertView.setTag(holder);
        } else {
            holder = (TimesViewHolder) convertView.getTag();
        }

        holder.textView.setText(data.get(position));

        convertView.setBackgroundColor(Color.WHITE);
        return convertView;
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



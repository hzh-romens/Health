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
import com.romens.yjk.health.ui.BaseActivity;
import com.romens.yjk.health.ui.components.SwLin;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, SwLin> mapView;

    public TimesAdapter(List<String> data, Context context, TimesAdapterCallBack callBack) {
        this.dailog = callBack;
        this.data = data;
        this.context = context;
        mapView = new HashMap<Integer, SwLin>();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_swlin, null);
            holder = new TimesViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.swlin_time);
            holder.textDelete = (TextView) convertView.findViewById(R.id.swlin_delete);
            convertView.setTag(holder);
        } else {
            holder = (TimesViewHolder) convertView.getTag();
        }
        SwLin sw = (SwLin) convertView.findViewById(R.id.swlin_layout);
        sw.setTag(position);
        mapView.put(position, sw);
        sw.setScreenListener(new SwLin.ScreenListener() {

            @Override
            public boolean startTouch(String tag) {
                if (mTouch) {
                    if (showMenuTag.equals(tag)) {
                        mTouch = false;
                    } else {
                        int p = Integer.parseInt(showMenuTag);
                        showMainLayout();
                    }
                }
                return mTouch;
            }

            @Override
            public void changeScreen(int screen, String tag) {
                if (screen == 1) {
                    mTouch = true;
                    showMenuTag = tag;
                }
            }

            @Override
            public void canTouch(boolean flag) {
                mTouch = false;
            }

        });
        holder.textView.setText(data.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
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
        holder.textDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.size() > 1) {
                    data.remove(position);
                } else {
                    Toast.makeText(context, "默认选项不能删除", Toast.LENGTH_SHORT).show();
                }
                showMainLayout();
                notifyDataSetChanged();

            }
        });
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

    public void showMainLayout() {

        for (int key : mapView.keySet()) {
            mapView.get(key).showScreen(0);

        }
    }

    private boolean mTouch = false;
    private String showMenuTag;

    class TimesViewHolder {
        private TextView textView;
        private TextView textDelete ;
    }
}



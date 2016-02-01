package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.romens.android.AndroidUtilities;

import java.util.List;

/**
 * Created by HZH on 2016/1/18.
 */
public class CuoponAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mResult;

    public CuoponAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<String> result) {
        if (mResult != null) {
            mResult.clear();
        }
        this.mResult = result;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        // return mResult != null ? 0 : mResult.size();
        if (mResult != null) {
            return mResult.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(mContext);
        }
        TextView textView = (TextView) convertView;
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(48)));
        textView.setText(mResult.get(position));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        return convertView;
    }
}

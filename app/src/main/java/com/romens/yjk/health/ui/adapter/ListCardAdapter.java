package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by AUSU on 2016/1/25.
 */
public class ListCardAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mResult;

    public ListCardAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<String> result) {
        this.mResult = result;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mResult!=null){
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
        return null;
    }
}

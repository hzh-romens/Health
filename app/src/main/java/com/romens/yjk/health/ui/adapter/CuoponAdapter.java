package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.romens.yjk.health.ui.cells.CuoponCardCell;

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
            convertView = new CuoponCardCell(mContext);
        }
        CuoponCardCell cell = (CuoponCardCell) convertView;

        return convertView;
    }
}

package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.romens.yjk.health.model.CuoponEntity;
import com.romens.yjk.health.ui.cells.CuoponCardCell;

import java.util.List;

/**
 * Created by HZH on 2016/1/18.
 */
public class CuoponAdapter extends BaseAdapter {

    private Context mContext;
    private List<CuoponEntity> mResult;

    public CuoponAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<CuoponEntity> result) {
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
        CuoponEntity entity = mResult.get(position);
        cell.setValue(entity.getIsused(), entity.getName(), entity.getEnddate(), entity.getLimitamount(), entity.getShuoming(), entity.getAmount(), entity.getStartdate());
        cell.setStatus("1");
        return convertView;
    }
}

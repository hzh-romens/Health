package com.yunuo.pay.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yunuo.pay.cells.CntentCell;

import java.util.List;

/**
 * Created by HZH on 2016/1/13.
 */
public class PayAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mResult;
    private SparseArray mTypes;
    private SparseBooleanArray mStatus;

    public PayAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<String> result, SparseArray types) {
        this.mResult = result;
        this.mTypes = types;
        notifyDataSetChanged();
    }

    public void setStatus(SparseBooleanArray status) {
        this.mStatus = status;
        notifyDataSetChanged();
    }

    private StatuNotifyDataSetChanged mListener;

    public void setStatuNotifyDataSetChanged(StatuNotifyDataSetChanged listener) {
        this.mListener = listener;
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
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CntentCell(mContext);
        }
        CntentCell cell = (CntentCell) convertView;
        if (mTypes.get(position) == 1) {
            cell.setValue("订单号码", mResult.get(position));
        } else if (mTypes.get(position) == 2) {
            cell.setValue("商品价格", mResult.get(position));
        }
        return convertView;
    }

    public interface StatuNotifyDataSetChanged {
        void notify(int flag, boolean statu);
    }

}

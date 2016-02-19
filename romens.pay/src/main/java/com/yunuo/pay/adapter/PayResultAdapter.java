package com.yunuo.pay.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yunuo.pay.cells.ButtonCell;
import com.yunuo.pay.cells.CntentCell;
import com.yunuo.pay.cells.StatusCell;

import java.util.List;

/**
 * Created by HZH on 2016/2/19.
 */
public class PayResultAdapter extends BaseAdapter {
    private Context mContext;
    private SparseArray mTypes;
    private List<String> mResult;

    @Override
    public int getCount() {
        if (mResult != null) {
            return mResult.size()+1;
        }
        return 0;
    }

    public PayResultAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<String> result, SparseArray types) {
        if (mResult != null) {
            mResult.clear();
        }
        if (mTypes != null) {
            mTypes.clear();
        }
        this.mResult = result;
        this.mTypes = types;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return mTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mTypes.get(position) == 1) {
            return 1;
        } else if (mTypes.get(position) == 2 || mTypes.get(position) == 3 || mTypes.get(position) == 4 || mTypes.get(position) == 5 || mTypes.get(position) == 6) {
            return 2;
        } else if (mTypes.get(position) == 7) {

            return 3;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == 1) {
            if (convertView == null) {
                convertView = new StatusCell(mContext);
            }
            StatusCell cell = (StatusCell) convertView;
            cell.setValue(mResult.get(position), "");
        } else if (viewType == 2) {
            if (convertView == null) {
                convertView = new CntentCell(mContext);
            }
            CntentCell cell = (CntentCell) convertView;
            if (mTypes.get(position) == 2) {
                cell.setDetail("商品名称", mResult.get(position));
            } else if (mTypes.get(position) == 3) {
                cell.setDetail("支付方式", mResult.get(position));
            } else if (mTypes.get(position) == 4) {
                cell.setDetail("交易订单", mResult.get(position));
            } else if (mTypes.get(position) == 5) {
                cell.setDetail("交易时间", mResult.get(position));
            } else if (mTypes.get(position) == 6) {
                cell.setDetail("支付金额", mResult.get(position));
            }
        } else if (viewType == 3) {
            if (convertView == null) {
                convertView = new ButtonCell(mContext);
            }
            ButtonCell cell = (ButtonCell) convertView;
            cell.setValue(mResult.get(0));
        }
        return convertView;
    }
}

package com.yunuo.pay;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by HZH on 2016/1/13.
 */
public class PayAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mResult;
    private SparseArray mTypes;

    public PayAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<String> result, SparseArray types) {
        this.mResult = result;
        this.mTypes = types;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mTypes != null) {
            return mTypes.size();
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
        int itemViewType = getItemViewType(position);
        if (itemViewType == 1) {
            if (convertView == null) {
                convertView = new CntentCell(mContext);
            }
            CntentCell cntentCell = (CntentCell) convertView;
            cntentCell.setValue(mResult.get(0), mResult.get(1));
        } else if (itemViewType == 2) {
            if (convertView == null) {
                convertView = new TextViewCell(mContext);
            }
            TextViewCell textViewCell = (TextViewCell) convertView;
        } else if (itemViewType == 3) {
            if (convertView == null) {
                convertView = new CheckLayoutCell(mContext);
            }
            final CheckLayoutCell checkLayoutCell = (CheckLayoutCell) convertView;
            if (mTypes.get(position) == 3) {
                checkLayoutCell.setValue(R.drawable.logos, "微信支付", "XXX", true);
            } else if (mTypes.get(position) == 4) {
                checkLayoutCell.setValue(R.drawable.logos, "支付宝支付", "X", false);
            }
            checkLayoutCell.setListener(new CheckLayoutCell.checkChangeListener() {
                @Override
                public void stateChange(boolean status) {
                    checkLayoutCell.setCheckStatus(status);
                }
            });
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (1 == mTypes.get(position)) {
            return 1;
        } else if (2 == mTypes.get(position)) {
            return 2;
        } else if (3 == mTypes.get(position) || 4 == mTypes.get(position)) {
            return 3;
        }
        return 0;
    }
}

package com.yunuo.pay.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yunuo.pay.R;
import com.yunuo.pay.cells.CheckLayoutCell;
import com.yunuo.pay.cells.CntentCell;
import com.yunuo.pay.cells.TextViewCell;

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
            if (position == 0) {
                cntentCell.setDetail("订单号", mResult.get(position));
            } else if (position == 1) {
                cntentCell.setDetail("价格", "¥" + mResult.get(position));
            }

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
                checkLayoutCell.setValue(R.drawable.logos, "微信支付", "", true);
                checkLayoutCell.setCheckStatus(mStatus.get(3));
            } else if (mTypes.get(position) == 4) {
                checkLayoutCell.setValue(R.drawable.logos, "支付宝支付", "", false);
                checkLayoutCell.setCheckStatus(mStatus.get(4));
            }
            checkLayoutCell.setListener(new CheckLayoutCell.checkChangeListener() {

                @Override
                public void stateChange(int flag, boolean status) {
                    checkLayoutCell.setCheckStatus(status);
                    mListener.notify(flag, status);
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

    public interface StatuNotifyDataSetChanged {
        void notify(int flag, boolean statu);
    }

}

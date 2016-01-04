package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.romens.yjk.health.ui.cells.MemberTextViewCell;
import com.romens.yjk.health.ui.cells.ProgressBarCell;

import java.util.List;

/**
 * Created by AUSU on 2016/1/2.
 */
public class MemberAdapter extends BaseAdapter {
    private List<String> mResult;
    private Context mContext;

    public MemberAdapter(Context context) {
        this.mContext = context;
    }

    public void bindData(List<String> result) {
        this.mResult = result;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mResult.size() == 0 ? 0 : mResult.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return Integer.parseInt(mResult.get(position));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 1) {
            if (convertView == null) {
                convertView = new ProgressBarCell(mContext);
            }
            ProgressBarCell progressBarCell = (ProgressBarCell) convertView;
            progressBarCell.setValue(500, 100);
        } else if (getItemViewType(position) == 2 || getItemViewType(position) == 3 || getItemViewType(position) == 4) {
            if (convertView == null) {
                convertView = new MemberTextViewCell(mContext);
            }
            MemberTextViewCell textViewCell = (MemberTextViewCell) convertView;
            if (getItemViewType(position) == 2) {
                textViewCell.setValue("我的账户", "12345678");
            } else if (getItemViewType(position) == 3) {
                textViewCell.setValue("会员等级", "铜牌会员");
            } else if (getItemViewType(position) == 4) {
                textViewCell.setValue("会员积分", "50");
            }
        }
        return convertView;
    }
}

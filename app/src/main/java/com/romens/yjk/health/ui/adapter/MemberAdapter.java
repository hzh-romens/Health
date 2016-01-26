package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.android.ui.cells.TextIconCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.MemberDetailCell;
import com.romens.yjk.health.ui.components.MemberCardView;

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
                convertView = new MemberCardView(mContext);
            }
            MemberCardView memberCardView = (MemberCardView) convertView;
        } else if (getItemViewType(position) == 2) {
            if (convertView == null) {
                convertView = new MemberDetailCell(mContext);
            }
            MemberDetailCell cell= (MemberDetailCell) convertView;
            cell.setValue("LV5","100","20");
        } else if (getItemViewType(position) == 4 || getItemViewType(position) == 6 || getItemViewType(position) == 7
                ||getItemViewType(position)==9||getItemViewType(position)==10) {
            if(convertView==null){
                convertView=new TextIconCell(mContext);
            }
            TextIconCell textIconCell = (TextIconCell) convertView;
            if (getItemViewType(position) == 4) {
                textIconCell.setIconTextAndNav(R.drawable.ic_address2, "会员条形码", R.drawable.ic_chevron_right_grey600_24dp, true);
            } else if (getItemViewType(position) == 6) {
                textIconCell.setIconTextAndNav(R.drawable.ic_address2, "优惠券", R.drawable.ic_chevron_right_grey600_24dp, true);
            } else if (getItemViewType(position) == 7) {
                textIconCell.setIconTextAndNav(R.drawable.ic_address2, "积分商城", R.drawable.ic_chevron_right_grey600_24dp, true);
            }else if(getItemViewType(position)==9){
                textIconCell.setIconTextAndNav(R.drawable.ic_address2, "个人资料", R.drawable.ic_chevron_right_grey600_24dp, true);
            }else if (getItemViewType(position)==10){
                textIconCell.setIconTextAndNav(R.drawable.ic_address2, "消费明细", R.drawable.ic_chevron_right_grey600_24dp, true);
            }
        }else if(getItemViewType(position)==3||getItemViewType(position)==5||getItemViewType(position)==8){
            if(convertView==null){
                convertView=new ShadowSectionCell(mContext);
            }
            ShadowSectionCell cell= (ShadowSectionCell) convertView;
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 11;
    }
}

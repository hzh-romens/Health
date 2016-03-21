package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.EmptyCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.MemberType;
import com.romens.yjk.health.ui.cells.MemberButtonCell;
import com.romens.yjk.health.ui.cells.MemberEditCell;
import com.romens.yjk.health.ui.cells.TipCell;

/**
 * Created by HZH on 2016/3/18.
 */
public class NewMemberAdapter extends RecyclerView.Adapter {
    private SparseIntArray mTypes = new SparseIntArray();
    private Context mContext;

    public NewMemberAdapter(Context context) {
        this.mContext = context;
    }

    public void bindType(SparseIntArray types) {
        if (mTypes != null) {
            mTypes.clear();
        }
        this.mTypes = types;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MemberType.TIP) {
            TipCell cell = new TipCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        } else if (viewType == MemberType.EMPTY) {
            EmptyCell cell = new EmptyCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            cell.setHeight(16);
            return new Holder(cell);
        } else if (viewType == MemberType.PHONE) {
            MemberEditCell cell = new MemberEditCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        } else if (viewType == MemberType.PSW) {
            MemberEditCell cell = new MemberEditCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        } else if (viewType == MemberType.ADVICE) {
            MemberEditCell cell = new MemberEditCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        } else if (viewType == MemberType.BUTTON) {
            MemberButtonCell cell = new MemberButtonCell(parent.getContext());
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new Holder(cell);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == MemberType.TIP) {
            TipCell cell = (TipCell) holder.itemView;
            cell.setBackgroundColor(Color.WHITE);
            cell.setTextColor(Color.BLACK);
            cell.setTextSize(16);
            cell.setValue("验证手机号码立即开通会员");
        } else if (itemViewType == MemberType.EMPTY) {
            EmptyCell cell = (EmptyCell) holder.itemView;
        } else if (itemViewType == MemberType.PHONE) {
            MemberEditCell cell = (MemberEditCell) holder.itemView;
            cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_launcher));
            cell.setVisible(true);
            cell.setHintText("请输入手机号码");
            cell.setNeedDivider(true);
        } else if (itemViewType == MemberType.PSW) {
            MemberEditCell cell = (MemberEditCell) holder.itemView;
            cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_launcher));
            cell.setVisible(false);
            cell.setHintText("请输入验证码");
            cell.setNeedDivider(true);
        } else if (itemViewType == MemberType.ADVICE) {
            MemberEditCell cell = (MemberEditCell) holder.itemView;
            cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_launcher));
            cell.setVisible(true);
            cell.setHintText("请输入推荐码(选填)");
            cell.setNeedDivider(true);
        } else {
            MemberButtonCell cell = (MemberButtonCell) holder.itemView;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        // return mTypes != null ? 0 : mTypes.size();
        if (mTypes != null) {
            return mTypes.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mTypes.get(position);
    }

}

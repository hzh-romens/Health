package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
            cell.setHeight(0);
            return new Holder(cell);
        } else if (viewType == MemberType.PHONE || viewType == MemberType.PSW || viewType == MemberType.ADVICE) {
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
        final int itemViewType = getItemViewType(position);
        if (itemViewType == MemberType.TIP) {
            TipCell cell = (TipCell) holder.itemView;
            cell.setBackgroundColor(mContext.getResources().getColor(R.color.btn_primary_light));
            cell.setTextColor(Color.BLACK);
            cell.setTextSize(16);
            cell.setValue("验证手机号码立即开通会员");
        } else if (itemViewType == MemberType.EMPTY) {
            EmptyCell cell = (EmptyCell) holder.itemView;
        } else if (itemViewType == MemberType.PHONE || itemViewType == MemberType.PSW || itemViewType == MemberType.ADVICE) {
            MemberEditCell cell = (MemberEditCell) holder.itemView;
            cell.setBackgroundColor(mContext.getResources().getColor(R.color.md_white_1000));
            if (itemViewType == MemberType.PHONE) {
                cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_edit_phonebumber));
                cell.setVisible(true);
                cell.setHintText("请输入手机号码");
                cell.setNeedDivider(true);
            } else if (itemViewType == MemberType.PSW) {
                cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_edit_phonepassword));
                cell.setVisible(false);
                cell.setHintText("请输入验证码");
                cell.setNeedDivider(true);
                cell.setSendRecommondListener(new MemberEditCell.SendRecommondListener() {
                    @Override
                    public void SendRecommond() {
                        Toast.makeText(mContext, "点击验证码", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                cell.setDrawableLeft(mContext.getResources().getDrawable(R.drawable.ic_recommond));
                cell.setVisible(true);
                cell.setHintText("请输入推荐码(选填)");
                cell.setNeedDivider(true);
            }
            cell.SetEditTextChangeListener(new MemberEditCell.EditTextChangeListener() {
                @Override
                public void EditTextChange(String value) {
                    if (itemViewType == MemberType.PHONE) {
                        phoneNumber = value;
                    } else if (itemViewType == MemberType.PSW) {
                        password = value;
                    } else {
                        recommend = value;
                    }
                }
            });
        } else {
            MemberButtonCell cell = (MemberButtonCell) holder.itemView;
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.sureButtonClickListener(phoneNumber, password, recommend);
                }
            });
        }
    }


    static class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        if (mTypes != null) {
            return mTypes.size();
        } else {
            return 0;
        }
    }

    private onItemClickListener mListener;
    private String phoneNumber, password, recommend;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public interface onItemClickListener {
        void sureButtonClickListener(String phoneValue, String pswValue, String recommendValue);
    }

    @Override
    public int getItemViewType(int position) {
        return mTypes.get(position);
    }

}

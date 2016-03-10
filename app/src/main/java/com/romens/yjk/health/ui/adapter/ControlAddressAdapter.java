package com.romens.yjk.health.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.romens.android.AndroidUtilities;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.ShadowSectionCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.ui.ControlAddressActivity;
import com.romens.yjk.health.ui.components.CheckableView;

import java.util.List;

/**
 * Created by anlc on 2015/10/19.
 */
public class ControlAddressAdapter extends RecyclerView.Adapter<ControlAddressAdapter.ControlAddressHolder> {

    private Context context;
    private List<AddressEntity> data;
    private int currDefaultAddressIndex = -1;
    private boolean isShowArrow = true;

    private onItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickLinstener(ControlAddressAdapter.onItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setIsShowArrow(boolean isShowArrow) {
        this.isShowArrow = isShowArrow;
    }

    public interface onItemLongClickListener {
        void itemLongClickListener(int position);

        void isDefaultClickLListener(int position);

        void itemClickListener(int position);

        void itemClickToEditListener(int position, int type);
    }

    public void setData(List<AddressEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

//    public void changeDefaultAddressIndex(int newIndex) {
//        if (newIndex == currDefaultAddressIndex) {
//            return;
//        }
//        if (currDefaultAddressIndex != -1) {
//            data.get(currDefaultAddressIndex).setISDEFAULT("0");
//        }
//        currDefaultAddressIndex = newIndex;
//        data.get(currDefaultAddressIndex).setISDEFAULT("1");
//        notifyDataSetChanged();
//    }

    public ControlAddressAdapter(Context context, List<AddressEntity> date) {
        this.context = context;
        this.data = date;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public ControlAddressHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_controladdress, null);
        } else if (viewType == 1) {
            view = new ShadowSectionCell(context);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ControlAddressHolder(view);
    }

    @Override
    public void onBindViewHolder(final ControlAddressHolder holder, final int position) {
        int type = getItemViewType(position);
        if (type == 0) {
            final int index = position / 2;
            AddressEntity entity = data.get(index);
            SpannableString user = new SpannableString(entity.getRECEIVER());
            user.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, user.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.nameView.setText(user);
            holder.telView.setText(entity.getCONTACTPHONE());
            holder.addressView.setText(entity.getADDRESS());
            holder.cityView.setText(entity.getPROVINCENAME() + "-" + entity.getCITYNAME() + "-" + entity.getREGIONNAME());
            holder.outsideLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemLongClickListener(index);
                    }
                    return false;
                }
            });
            holder.outsideLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemClickListener(position / 2);
                    }
                }
            });

            holder.isDefault.setChecked(false);
            holder.isDefault.setTextSize(14);
            holder.isDefault.shouldText(true);
            holder.isDefault.setText("默认地址");
            holder.arrowImg.setColorFilter(0xffe5e5e5);
            if (!isShowArrow) {
                holder.arrowImg.setVisibility(View.GONE);
            }

            if (entity.getISDEFAULT() != null && entity.getISDEFAULT().equals("1")) {
//                currDefaultAddressIndex = index;
                holder.isDefault.setChecked(true);
            }
            holder.isDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.isDefaultClickLListener(index);
                    }
                }
            });

            holder.addressLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemClickToEditListener(index, ControlAddressActivity.ADDRESS_CHECK_TYPE);
                    }
                }
            });

            holder.toEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemClickToEditListener(index, ControlAddressActivity.ADDRESS_EDIT_TYPE);
                    }
                }
            });

            holder.isDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.isDefaultClickLListener(index);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() * 2 - 1;
    }

    class ControlAddressHolder extends RecyclerView.ViewHolder {

        public TextView nameView;
        public TextView telView;
        public TextView addressView;
        public CheckableView isDefault;
        public LinearLayout outsideLayout;
        public RelativeLayout defaultLayout;
        public TextView toEditBtn;
        public TextView cityView;
        public RelativeLayout addressLayout;
        public ImageView arrowImg;

        public ControlAddressHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.control_address_name);
            telView = (TextView) itemView.findViewById(R.id.control_address_tel);
            addressView = (TextView) itemView.findViewById(R.id.control_address_address);
            isDefault = (CheckableView) itemView.findViewById(R.id.control_address_isdefault);
            cityView = (TextView) itemView.findViewById(R.id.control_address_city);
            outsideLayout = (LinearLayout) itemView.findViewById(R.id.control_address_layout);
            defaultLayout = (RelativeLayout) itemView.findViewById(R.id.control_address_defult_layout);
            toEditBtn = (TextView) itemView.findViewById(R.id.control_address_edit);
            addressLayout = (RelativeLayout) itemView.findViewById(R.id.control_address_address_layout);
            arrowImg = (ImageView) itemView.findViewById(R.id.control_address_arrow);
        }
    }
}
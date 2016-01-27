package com.romens.yjk.health.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

/**
 * Created by anlc on 2015/10/19.
 */
public class ControlAddressAdapter extends RecyclerView.Adapter<ControlAddressAdapter.ControlAddressHolder> {

    private Context context;
    private List<AddressEntity> data;
    private int currDefaultAddressIndex = -1;

    private onItemLongClickLinstener onItemLongClickLinstener;

    public void setOnItemLongClickLinstener(ControlAddressAdapter.onItemLongClickLinstener onItemLongClickLinstener) {
        this.onItemLongClickLinstener = onItemLongClickLinstener;
    }

    public interface onItemLongClickLinstener {
        void itemLongClickLinstener(int position);

        void isDefaultClickLLinstener(int postion);
    }

    public void setData(List<AddressEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void changeDefaultAddressIndex(int newIndex) {
        if (newIndex == currDefaultAddressIndex) {
            return;
        }
        if (currDefaultAddressIndex != -1) {
            data.get(currDefaultAddressIndex).setISDEFAULT("0");
        }
        currDefaultAddressIndex = newIndex;
        data.get(currDefaultAddressIndex).setISDEFAULT("1");
        notifyDataSetChanged();
    }

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
            holder.nameView.setText(data.get(index).getRECEIVER());
            holder.telView.setText(data.get(index).getCONTACTPHONE());
            holder.addressview.setText(data.get(index).getADDRESS());
//            holder.del.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    removeItem(index);
//                }
//            });
            holder.outsideLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickLinstener != null) {
                        onItemLongClickLinstener.itemLongClickLinstener(index);
                    }
                    return false;
                }
            });
            holder.outsideLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onViewClickListener != null) {
                        onViewClickListener.onClick(position / 2);
                    }
                }
            });
            holder.defaultImg.setImageResource(R.drawable.ic_check_unchoice);
            AddressEntity entity = data.get(index);

            if (entity.getISDEFAULT() != null && entity.getISDEFAULT().equals("1")) {
                currDefaultAddressIndex = index;
                holder.defaultImg.setImageResource(R.drawable.ic_check_choice);
            }
            holder.defaultLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickLinstener != null) {
                        onItemLongClickLinstener.isDefaultClickLLinstener(index);
                    }
                }
            });

            holder.isDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickLinstener != null) {
                        onItemLongClickLinstener.isDefaultClickLLinstener(index);
                    }
                }
            });
        }
    }


//    public void removeItem(final int position) {
//        new AlertDialog.Builder(context).setTitle("真的要删除吗？").setNegativeButton("不了", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).setPositiveButton("是的", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                requestDeleteDataChanged(data.get(position).getADDRESSID());
//            }
//        }).show();
//    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() * 2 - 1;
    }

    class ControlAddressHolder extends RecyclerView.ViewHolder {

        public TextView nameView;
        public TextView telView;
        public TextView addressview;
        public TextView isDefault;
        public ImageView defaultImg;
        public LinearLayout outsideLayout;
        public RelativeLayout defaultLayout;

        public ControlAddressHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.control_address_name);
            telView = (TextView) itemView.findViewById(R.id.control_address_tel);
            addressview = (TextView) itemView.findViewById(R.id.control_address_address);
            isDefault = (TextView) itemView.findViewById(R.id.control_address_isdefault);
            defaultImg = (ImageView) itemView.findViewById(R.id.controladdress_deafult_img);
            outsideLayout = (LinearLayout) itemView.findViewById(R.id.control_address_layout);
            defaultLayout = (RelativeLayout) itemView.findViewById(R.id.control_address_defult_layout);
        }
    }

    private OnViewClickListener onViewClickListener;

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener {
        void onClick(int position);
    }
}
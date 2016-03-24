package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.android.ui.cells.TextInfoCell;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.ui.ControlAddressActivity;
import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.components.CheckableView;

import java.util.List;

/**
 * Created by anlc on 2015/10/19.
 */
public class ControlAddressAdapter extends RecyclerView.Adapter<ADHolder> {

    private Context context;
    private List<AddressEntity> data;
    //    private int currDefaultAddressIndex = -1;
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

        void itemClickToCheckListner(int position, int type);
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
        return position >= data.size() ? 1 : 0;
    }

    @Override
    public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            TextInfoCell cell = new TextInfoCell(context);
            cell.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            return new ADHolder(cell);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_controladdress, parent, false);
            return new ControlAddressHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ADHolder viewHolder, final int position) {
        int type = getItemViewType(position);
        if (type == 1) {
            TextInfoCell cell = (TextInfoCell) viewHolder.itemView;
            cell.setText("小提示:长按可以删除地址哦");
        } else {
            ControlAddressHolder holder = (ControlAddressHolder) viewHolder;
            AddressEntity entity = data.get(position);
            String receiver = entity.getRECEIVER();
            String tel = entity.getCONTACTPHONE();
            String result = receiver + "    " + tel;
            SpannableString user = new SpannableString(result);
            user.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, receiver.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            user.setSpan(new ForegroundColorSpan(Color.parseColor("#2baf2b")), result.length() - tel.length(), result.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.itemView.setBackgroundResource(R.drawable.list_selector);
            holder.nameView.setText(user);
//            holder.telView.setText(entity.getCONTACTPHONE());
            holder.addressView.setText(entity.getADDRESS());
            holder.cityView.setText(entity.getPROVINCENAME() + "-" + entity.getCITYNAME() + "-" + entity.getREGIONNAME());
//        holder.outsideLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (onItemLongClickListener != null) {
//                    onItemLongClickListener.itemLongClickListener(position);
//                }
//                return false;
//            }
//        });
            holder.outsideLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemClickListener(position);
                    }
                }
            });

            holder.isDefault.setChecked(false);
            holder.isDefault.setTextSize(16);
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
                        onItemLongClickListener.isDefaultClickLListener(position);
                    }
                }
            });

            holder.addressLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemClickToCheckListner(position, ControlAddressActivity.ADDRESS_CHECK_TYPE);
                    }
                }
            });
            holder.addressLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemLongClickListener(position);
                    }
                    return false;
                }
            });

            holder.toEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.itemClickToEditListener(position, ControlAddressActivity.ADDRESS_EDIT_TYPE);
                    }
                }
            });

            holder.isDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.isDefaultClickLListener(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int size = data.size();
        return size > 0 ? (size + 1) : 0;
//        return data.size() == 0 ? 0 : data.size();
    }

    class ControlAddressHolder extends ADHolder {

        public TextView nameView;
        //        public TextView telView;
        public TextView addressView;
        public CheckableView isDefault;
        public LinearLayout outsideLayout;
        public View defaultLayout;
        public TextView toEditBtn;
        public TextView cityView;
        public View addressLayout;
        public ImageView arrowImg;

        public ControlAddressHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.control_address_name);
//            telView = (TextView) itemView.findViewById(R.id.control_address_tel);
            addressView = (TextView) itemView.findViewById(R.id.control_address_address);
            isDefault = (CheckableView) itemView.findViewById(R.id.control_address_isdefault);
            cityView = (TextView) itemView.findViewById(R.id.control_address_city);
            outsideLayout = (LinearLayout) itemView.findViewById(R.id.control_address_layout);
            defaultLayout = itemView.findViewById(R.id.control_address_defult_layout);
            toEditBtn = (TextView) itemView.findViewById(R.id.control_address_edit);
            addressLayout = itemView.findViewById(R.id.control_address_address_layout);
            arrowImg = (ImageView) itemView.findViewById(R.id.control_address_arrow);
        }
    }
}
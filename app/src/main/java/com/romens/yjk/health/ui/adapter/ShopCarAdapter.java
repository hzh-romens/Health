package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.romens.android.AndroidUtilities;
import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ShopCarTestEntity;
import com.romens.yjk.health.ui.components.CheckableFrameLayout;


import java.util.List;

/**
 * Created by romens007 on 2015/8/23.
 */
public class ShopCarAdapter extends RecyclerView.Adapter {
    private SparseArray<ShopCarTestEntity> mDatas;
    private Context mContext;
    //商品数量
    private int num;
    //商品的总价钱
    private double allMoney;

    private final SparseBooleanArray itemStates = new SparseBooleanArray();



    public boolean isAllSelected() {
        int index = itemStates.indexOfValue(false);
        return index < 0;
    }


    public interface AdapterCallback {
        void onDataChanged();

        void sumMoneyChange(String money);
    }

    private AdapterCallback adapterCallback;

    public void AdapterListener(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }


    public ShopCarAdapter(Context context, SparseArray<ShopCarTestEntity> datas) {
        this.mDatas = datas;
        this.mContext = context;
        itemStates.clear();
        int size = datas == null ? 0 : datas.size();
        for (int i = 0; i < size; i++) {
            itemStates.put(i, datas.get(i).getCheck());
            if (datas.get(i).getCheck()) {
                allMoney = allMoney + datas.get(i).getPrice() * datas.get(i).getNum();
            }
        }
        //switchAllSelect(false);
    }

    public void switchAllSelect(boolean value) {
        int size = mDatas == null ? 0 : mDatas.size();
         double all = 0;
        for (int i = 0; i < size; i++) {
            itemStates.append(i, value);
            if (value) {
                double price = mDatas.get(i).getPrice();
                double prices = price * mDatas.get(i).getNum();
                all = all + prices;

            }
        }
        allMoney=all;
        updateData();
        String s = all + "";
        updateMoney(s);

    }

    public void switchItemSelect(int position, boolean value) {
        itemStates.put(position, value);
        updateData();
        String s = null;
        if (value) {
            double price = mDatas.get(position).getPrice();
            double prices = price * mDatas.get(position).getNum();
            allMoney = allMoney + prices;
        } else {
            double price = mDatas.get(position).getPrice();
            double prices = price * mDatas.get(position).getNum();
            allMoney = allMoney - prices;
        }
        s = allMoney + "";
        updateMoney(s);
    }

    private void updateMoney(String s) {
        if (adapterCallback != null) {
            adapterCallback.sumMoneyChange(s);
        }
    }

    public void updateData() {
        notifyDataSetChanged();
        if (adapterCallback != null) {
            adapterCallback.onDataChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.list_item_shop, null);
        RecyclerView.ViewHolder holder = new ShopHolder(view);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ShopHolder shopHolder = (ShopHolder) holder;

        shopHolder.iv_detail.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        shopHolder.iv_detail.setImageBitmap(ImageUtils.bindLocalImage("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg"));
        Drawable defaultDrawables = shopHolder.iv_detail.getDrawable();
        ImageManager.loadForView(mContext, shopHolder.iv_detail, "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg", defaultDrawables, defaultDrawables);
        shopHolder.tv_num.setText("1");
        shopHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchItemSelect(position, !shopHolder.checkBox.isChecked());
            }
        });
        shopHolder.checkBox.setChecked(itemStates.get(position));
        shopHolder.tv_infor.setText(mDatas.get(position).getJson());
        shopHolder.tv_price.setText(mDatas.get(position).getPrice() + "");
        shopHolder.tv_active.setText("8月24日活动，药品八折");
        shopHolder.tv_num.setText(mDatas.get(position).getNum() + "");
        shopHolder.tv_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出一个dialog
            }
        });
    }

    public SparseArray<ShopCarTestEntity> getLastData() {
        return mDatas;
    }

    @Override
    public int getItemCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    public class ShopHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckableFrameLayout checkBox;
        private Button bt_reduce, bt_add;
        private ImageView iv_detail;
        private TextView tv_infor, tv_price, tv_active;
        private TextView tv_num;

        public ShopHolder(View view) {
            super(view);
            checkBox = (CheckableFrameLayout) view.findViewById(R.id.checkbox);
            bt_reduce = (Button) view.findViewById(R.id.bt_reduce);
            bt_add = (Button) view.findViewById(R.id.bt_add);
            iv_detail = (ImageView) view.findViewById(R.id.iv_detail);
            tv_infor = (TextView) view.findViewById(R.id.tv_shop_infor);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_num = (TextView) view.findViewById(R.id.et_num);
            tv_active = (TextView) view.findViewById(R.id.tv_active);
            bt_add.setOnClickListener(this);
            bt_reduce.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            num = Integer.parseInt(tv_num.getText().toString());
            switch (v.getId()) {
                case R.id.bt_reduce:
                    if (num > 1) {
                        num--;
                        tv_num.setText(num + "");
                    } else {
                        tv_num.setText(num + "");
                    }
                    break;
                case R.id.bt_add:
                    num++;
                    tv_num.setText(num + "");
                    break;
                case R.id.et_num:
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getType();
    }
}

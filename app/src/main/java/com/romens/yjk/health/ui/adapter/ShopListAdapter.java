package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.ShopCarEntity;
import com.romens.yjk.health.ui.components.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AUSU on 2015/9/24.
 */
public class ShopListAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private final List<ShopCarEntity> mResult=new ArrayList<ShopCarEntity>();

    public ShopListAdapter(Context context){
        this.mContext=context;
    }
    public void BindData(List<ShopCarEntity> result){
        mResult.clear();
        if(result!=null||result.size()>0){
            mResult.addAll(result);
        }
        notifyDataSetChanged();
    }
    public void addData(List<ShopCarEntity> nextResult){
        if(nextResult.size()>0||nextResult!=null){
            mResult.addAll(nextResult);
        }
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.list_item_shop_list, null);
        ItemHolder itemHolder=new ItemHolder(view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder= (ItemHolder) holder;
        ShopCarEntity shopCarEntity = mResult.get(position);
        Log.i("网址----", shopCarEntity.getGOODURL());
        itemHolder.iv.setImageBitmap(ImageUtils.bindLocalImage(shopCarEntity.getGOODURL()));
        Drawable defaultDrawables =  itemHolder.iv.getDrawable();
        ImageManager.loadForView(mContext, itemHolder.iv, shopCarEntity.getGOODURL(), defaultDrawables, defaultDrawables);
        itemHolder.name.setText(shopCarEntity.getGOODSCLASSNAME());
        itemHolder.realPrice.setText(shopCarEntity.getGOODSPRICE() + "");
        itemHolder.realPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        itemHolder.discountPrice.setText(shopCarEntity.getGOODSPRICE()+"");
        itemHolder.shop.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_shopping_cart_grey600_48dp));
        itemHolder.comment.setText("999评论数");
    }

    @Override
    public int getItemCount() {
        return mResult.size()==0?0:mResult.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView iv,shop;
        private TextView name,discountPrice,realPrice,comment;
        public ItemHolder(View view){
            super(view);
            iv= (ImageView) view.findViewById(R.id.iv);
            shop= (ImageView) view.findViewById(R.id.shop);
            name= (TextView) view.findViewById(R.id.name);
            discountPrice= (TextView) view.findViewById(R.id.discountPrice);
            realPrice= (TextView) view.findViewById(R.id.realPrice);
            comment= (TextView) view.findViewById(R.id.comment);
        }
    }
}

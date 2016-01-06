package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.Image.BackupImageView;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.GoodListEntity;
import com.romens.yjk.health.ui.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AUSU on 2015/9/24.
 */
public class ShopListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private final List<GoodListEntity> mResult = new ArrayList<GoodListEntity>();

    public ShopListAdapter(Context context) {
        this.mContext = context;
    }

    public void BindData(List<GoodListEntity> result) {
        mResult.clear();
        if (result != null || result.size() > 0) {
            mResult.addAll(result);
        }
        notifyDataSetChanged();
    }

    public void addData(List<GoodListEntity> nextResult) {
        if (nextResult.size() > 0 || nextResult != null) {
            mResult.addAll(nextResult);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.list_item_shop_list, null);
        ItemHolder itemHolder = new ItemHolder(view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        final GoodListEntity goodListEntity = mResult.get(position);


        if (goodListEntity.getPICBIG() != null && !("".equals(goodListEntity.getPICBIG()))) {
            itemHolder.icon.setImageUrl(goodListEntity.getPICBIG(), "64_64", null);
        } else {
            itemHolder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.picture_fail));
        }

        itemHolder.name.setText(goodListEntity.getMEDICINENAME());
        if ("".equals(goodListEntity.getPRICE()) || goodListEntity == null) {
            itemHolder.realPrice.setVisibility(View.INVISIBLE);
            itemHolder.discountPrice.setVisibility(View.INVISIBLE);
        } else {
            // itemHolder.realPrice.setVisibility(View.VISIBLE);
            itemHolder.discountPrice.setVisibility(View.VISIBLE);
            itemHolder.shop.setVisibility(View.VISIBLE);
            itemHolder.realPrice.setText("¥" + goodListEntity.getMEMBERPRICE());
            itemHolder.realPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            itemHolder.discountPrice.setText("¥" + goodListEntity.getPRICE());
            itemHolder.shop.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_list_shopcaricon));
        }
        itemHolder.comment.setText(goodListEntity.getSHOPNAME());
        itemHolder.comment.setVisibility(View.GONE);
        itemHolder.shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserConfig.isClientLogined()) {
                    requestToBuy(mResult.get(position).getPRICE(), mResult.get(position).getMERCHANDISEID());
                } else {
                    Toast.makeText(mContext, "请您先登录", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
        itemHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIOpenHelper.openMedicineActivity(mContext, goodListEntity.getMERCHANDISEID());
            }
        });
    }

    public void requestToBuy(String PRICE, String GUID) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("GOODSGUID", GUID);
        args.put("USERGUID", UserConfig.getClientUserEntity().getGuid());
        args.put("BUYCOUNT", "1");
        args.put("PRICE", PRICE);

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "InsertIntoCar", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(mContext, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                android.util.Log.e("InsertIntoCar", msg.msg);
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    if ("ERROE".equals(response)) {
                        Toast.makeText(mContext, "加入购物车异常", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "成功加入购物车", Toast.LENGTH_SHORT).show();
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 1);
                    }
                } else {
                    android.util.Log.e("InsertIntoCar", errorMsg.msg);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mResult.size() == 0 ? 0 : mResult.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView shop;
        private BackupImageView icon;
        private TextView name, discountPrice, realPrice, comment;
        private LinearLayout itemLayout;

        public ItemHolder(View view) {
            super(view);
            icon = (BackupImageView) view.findViewById(R.id.iv);
            shop = (ImageView) view.findViewById(R.id.shop);
            name = (TextView) view.findViewById(R.id.name);
            discountPrice = (TextView) view.findViewById(R.id.discountPrice);
            realPrice = (TextView) view.findViewById(R.id.realPrice);
            comment = (TextView) view.findViewById(R.id.comment);
            itemLayout = (LinearLayout) view.findViewById(R.id.linear_item);
        }
    }
}

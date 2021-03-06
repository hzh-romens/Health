package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.model.GoodListEntity;
import com.romens.yjk.health.ui.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HZH on 2015/11/3.
 */
public class ShopListNoPictureAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private final List<GoodListEntity> mResult=new ArrayList<GoodListEntity>();
    public ShopListNoPictureAdapter(Context context){
        this.mContext=context;
    }
    public void BindData(List<GoodListEntity> result){
        mResult.clear();
        if(result!=null||result.size()>0){
            mResult.addAll(result);
        }
        notifyDataSetChanged();
    }
    public void addData(List<GoodListEntity> nextResult){
        if(nextResult.size()>0||nextResult!=null){
            mResult.addAll(nextResult);
        }
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.list_item_nopicture, null);
        ItemHolder itemHolder=new ItemHolder(view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return itemHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder= (ItemHolder) holder;
        GoodListEntity goodListEntity = mResult.get(position);
        itemHolder.tv_shopname.setText(goodListEntity.getSHOPNAME());
        itemHolder.tv_name.setText(goodListEntity.getMEDICINENAME());
        itemHolder.btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserConfig.isClientLogined()) {
                    requestToBuy(mResult.get(position).getPRICE(), mResult.get(position).getMERCHANDISEID());
                }else{
                    //跳转至登录页面
                    Toast.makeText(mContext, "请您先登录", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });


    }
    public class ItemHolder extends RecyclerView.ViewHolder{
        private TextView tv_name,tv_shopname;
        private ImageView btn_buy;
        public ItemHolder(View view){
            super(view);
            tv_name= (TextView) view.findViewById(R.id.name);
            tv_shopname= (TextView) view.findViewById(R.id.saleCount);
            btn_buy= (ImageView) view.findViewById(R.id.shop);
        }
    }

    @Override
    public int getItemCount() {
        return mResult.size()==0?0:mResult.size();
    }

    public void requestToBuy(String PRICE,String GUID){
        int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("GOODSGUID",GUID);
        args.put("USERGUID",UserConfig.getInstance().getClientUserEntity().getGuid());
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
}

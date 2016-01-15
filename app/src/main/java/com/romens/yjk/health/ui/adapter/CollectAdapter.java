package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.core.AppNotificationCenter;
import com.romens.yjk.health.db.DBInterface;
import com.romens.yjk.health.db.entity.FavoritesEntity;
import com.romens.yjk.health.helper.UIOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/16.
 */
public class CollectAdapter extends BaseAdapter {

    private Context context;
    private final List<FavoritesEntity> favoritesEntities =new ArrayList<>();
    private final Map<String,Boolean> favoritesSelects=new HashMap<>();

    public CollectAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<FavoritesEntity> data) {
        favoritesEntities.clear();
        if(data!=null&&data.size()>0){
            favoritesEntities.addAll(data);
        }
        favoritesSelects.clear();
        notifyDataSetChanged();
    }

    public List<FavoritesEntity> getFavoritesEntities() {
        return favoritesEntities;
    }

    @Override
    public int getCount() {
        return favoritesEntities ==null?0: favoritesEntities.size();
    }

    @Override
    public FavoritesEntity getItem(int position) {
        return favoritesEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CollectViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_collect, null);
            holder = new CollectViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (CollectViewHolder) convertView.getTag();
        final FavoritesEntity entity = favoritesEntities.get(position);
        if (entity.getPicSmall() == null || entity.getPicSmall().equals("") || entity.getPicSmall().equals("null")) {
            holder.imageView.setImageResource(R.drawable.no_img_upload);
        } else {
            holder.imageView.setImageUrl(entity.getPicSmall(), "64_64", null);
        }
        holder.drugNameTextView.setText(entity.getMedicineName());
        holder.moneyTextView.setText("￥" + entity.getPrice());
        holder.moneyTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.memberMoneyTextView.setText("￥" + entity.getMemberPrice());

        holder.specTextView.setText(entity.getMedicineSpec());
        holder.isSelectImgView.setVisibility(View.VISIBLE);
        String tempCount = entity.getSaleCount() + "件已售  " + entity.getAssessCount() + "条评论";
        holder.sellCountTextView.setText(tempCount);
        if (isSelect(entity.getId())) {
            holder.isSelectImgView.setImageResource(R.drawable.ic_check_choice);
        } else {
            holder.isSelectImgView.setImageResource(R.drawable.ic_check_unchoice);
        }
        holder.addShopCarImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestToBuy(entity.getMemberPrice(), entity.getMerchandiseId());
            }
        });

        holder.isSelectImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesEntity temp=getItem(position);
                switchSelect(temp.getId());
            }
        });
        convertView.setBackgroundColor(Color.WHITE);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, MedicinalDetailActivity.class);
//                intent.putExtra("guid", entity.getMerchandiseId());
//                context.startActivity(intent);

                UIOpenHelper.openMedicineActivity(context,entity.getMerchandiseId());
            }
        });

        return convertView;
    }

    public boolean isSelect(String id){
        if(!favoritesSelects.containsKey(id)) {
            favoritesSelects.put(id, false);
        }
        return favoritesSelects.get(id);
    }

    public void switchSelect(String id){
        if(!favoritesSelects.containsKey(id)){
            favoritesSelects.put(id, false);
        }
        favoritesSelects.put(id,!favoritesSelects.get(id));
        notifyDataSetChanged();
    }

    class CollectViewHolder {

        private BackupImageView imageView;
        private TextView drugNameTextView;
        private TextView moneyTextView;
        private TextView memberMoneyTextView;
        private TextView specTextView;
        private TextView sellCountTextView;
        private ImageView addShopCarImgView;
        private ImageView isSelectImgView;

        public CollectViewHolder(View view) {
            imageView = (BackupImageView) view.findViewById(R.id.collect_img);
            drugNameTextView = (TextView) view.findViewById(R.id.collect_title);
            moneyTextView = (TextView) view.findViewById(R.id.collect_money);
            memberMoneyTextView = (TextView) view.findViewById(R.id.collect_member_money);
            specTextView = (TextView) view.findViewById(R.id.collect_spec);
            sellCountTextView = (TextView) view.findViewById(R.id.collect_sell_count);
            addShopCarImgView = (ImageView) view.findViewById(R.id.collect_add_shopcar);
            isSelectImgView = (ImageView) view.findViewById(R.id.collect_select_img);
        }
    }

    public void requestToBuy(String PRICE, String GUID) {
        //加入购物车,用户暂定为2222
        int lastTime = DBInterface.instance().getDiscoveryDataLastTime();
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("GOODSGUID", GUID);
        args.put("USERGUID", UserGuidConfig.USER_GUID);
        args.put("BUYCOUNT", "1");
        args.put("PRICE", PRICE);

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "InsertIntoCar", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(context, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("InsertIntoCar", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    if ("ERROE".equals(response)) {
                        Toast.makeText(context, "加入购物车异常", Toast.LENGTH_SHORT).show();
                        Log.e("tag", "---newOrder--error->");
                    } else {
                        Toast.makeText(context, "成功加入购物车", Toast.LENGTH_SHORT).show();
                        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.shoppingCartCountChanged, 1);
                        Log.e("tag", "---newOrder--ok->");
                    }
                } else {
                    Log.e("InsertIntoCar", errorMsg.toString() + "====" + errorMsg.msg);
                }
            }
        });
    }
}

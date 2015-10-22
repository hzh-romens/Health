package com.romens.yjk.health.ui.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.AddRemindActivity;
import com.romens.yjk.health.ui.ControlAddressActivity;
import java.util.List;

/**
 * Created by AUSU on 2015/9/26.
 */
public class ModifyViewAdapter extends BaseAdapter {
    private List<String> mResult;
    private Context mContext;

    public ModifyViewAdapter(List<String> result, Context context) {
        this.mResult = result;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return mResult == null ? 0 : mResult.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.grid_item_person, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.container = (LinearLayout) convertView.findViewById(R.id.container);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if(position>3){}
        holder.name.setText(mResult.get(position));
        if ("用药提醒".equals(mResult.get(position))) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_medicinal_tip));
        } else if ("退换货记录".equals(mResult.get(position))) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_record));
        } else if ("收藏".equals(mResult.get(position))) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_favorite));
        } else if ("浏览历史".equals(mResult.get(position))) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_history));
        } else if ("收货地址管理".equals(mResult.get(position))) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_address));
        } else if ("账号管理".equals(mResult.get(position))) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_user));
        } else if ("帮助".equals(mResult.get(position))) {
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_help));
        } else if("意见反馈".equals(mResult.get(position))){
            //用户反馈
            holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_advice));
        }else{
            holder.name.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                if("收货地址管理".equals(mResult.get(position))){
                    intent.setClass(mContext,ControlAddressActivity.class);
                    mContext.startActivity(intent);
                }
                if ("用药提醒".equals(mResult.get(position))) {
                    intent.setClass(mContext, AddRemindActivity.class);
                    mContext.startActivity(intent);
                } else if ("退换货记录".equals(mResult.get(position))) {
                    Toast.makeText(mContext,"功能正在建设中...",Toast.LENGTH_SHORT).show();
                } else if ("收藏".equals(mResult.get(position))) {
                    Toast.makeText(mContext,"功能正在建设中...",Toast.LENGTH_SHORT).show();
                } else if ("浏览历史".equals(mResult.get(position))) {
                    Toast.makeText(mContext,"功能正在建设中...",Toast.LENGTH_SHORT).show();
                } else if ("收货地址管理".equals(mResult.get(position))) {
                    intent.setClass(mContext, ControlAddressActivity.class);
                    mContext.startActivity(intent);
                } else if ("账号管理".equals(mResult.get(position))) {
                    Toast.makeText(mContext,"功能正在建设中...",Toast.LENGTH_SHORT).show();
                } else if ("帮助".equals(mResult.get(position))) {
                    Toast.makeText(mContext,"功能正在建设中...",Toast.LENGTH_SHORT).show();
                } else if("意见反馈".equals(mResult.get(position))){
                    //用户反馈
                    Toast.makeText(mContext,"功能正在建设中...",Toast.LENGTH_SHORT).show();
                }else{

                }
                //mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class Holder {
        private TextView name;
        private ImageView imageView;
        private LinearLayout container;
    }
}

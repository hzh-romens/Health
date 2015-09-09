package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.AboutTestEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romens007 on 2015/8/17.
 */
public class GridViewAdapter extends BaseAdapter {
  //  private List<AboutTestEntity> datas=new ArrayList<AboutTestEntity>();
    private List<String> datas=new ArrayList<String>();
    private Context mContext;
    public GridViewAdapter(List<String> data,Context context){
        this.datas=data;
        this.mContext=context;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(datas!=null){
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.grid_item_about,null);
            holder.iv_medicinal= (ImageView) convertView.findViewById(R.id.iv_medicinal);
            holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_price= (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        int totalHeight = parent.getWidth();
        int height = (int)(totalHeight / 2);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height - 15);

        convertView.setLayoutParams(lp);
        //String imageUrl = datas.get(position).getImageUrl();
        String imageUrl = datas.get(position);
        holder.iv_medicinal.setImageBitmap(ImageUtils.bindLocalImage(imageUrl));
        Drawable defaultDrawable = holder.iv_medicinal.getDrawable();
        ImageManager.loadForView(mContext, holder.iv_medicinal, imageUrl, defaultDrawable, defaultDrawable);
        holder.tv_name.setVisibility(View.INVISIBLE);
        holder.tv_price.setVisibility(View.INVISIBLE);
        return convertView;
    }
    class ViewHolder{
        private ImageView iv_medicinal;
        private TextView tv_name;
        private TextView tv_price;
    }
}

package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.IllnessActivity;

import java.util.List;

/**
 * Created by romens007 on 2015/8/14.
 */
public class IllnessAdapter extends RecyclerView.Adapter{
    private List<String> mdatas;
    private Context mContext;
    private View mFootView;

    public IllnessAdapter(List<String> data,Context context,View footView){
        this.mdatas=data;
        this.mContext=context;
        this.mFootView=footView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view=View.inflate(mContext, R.layout.list_item_store_item2, null);
       RecyclerView.ViewHolder holder=new itemViewholder(view);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        itemViewholder itemViewholder= (IllnessAdapter.itemViewholder) viewHolder;
        itemViewholder.iv1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        itemViewholder.tv3.setText("item" + i);
        itemViewholder.tv4.setText("item"+(i+1));
    }

    @Override
    public int getItemCount() {
        if(mdatas!=null){
            return mdatas.size();
        }
        return 0;
    }
    public class itemViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv3,tv4;
        public ImageView iv1;
        private View item;
        public itemViewholder(View itemView){
            super(itemView);
                tv3= (TextView) itemView.findViewById(R.id.tv3);
                tv4= (TextView) itemView.findViewById(R.id.tv4);
                iv1= (ImageView) itemView.findViewById(R.id.iv1);
                item=itemView.findViewById(R.id.item2);
                item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,mdatas.get(getPosition())+"",Toast.LENGTH_SHORT).show();
        }
    }

}


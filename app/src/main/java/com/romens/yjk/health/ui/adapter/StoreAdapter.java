package com.romens.yjk.health.ui.adapter;

/**
 * Created by hzh on 2015/8/13.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.yjk.health.R;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter{
    private List<String> mdatas;
    private Context mContext;
    private View mFootView;

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);

        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }


    public StoreAdapter(List<String> datas, Context context, View headView, View footView){
        this.mdatas=datas;
        this.mContext=context;
        this.mFootView=mFootView;

    }

    //创建视图
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        RecyclerView.ViewHolder holder=null;
        if(viewType==1){
            view = View.inflate(mContext, R.layout.list_item_store_item1, null);
            holder=new itemViewholder1(view);

        }else if(viewType==2){
            view = View.inflate(mContext, R.layout.list_item_store_item2, null);
            holder=new itemViewholder2(view);
        }else{
            view = View.inflate(mContext, R.layout.list_item_store_item3, null);
            holder=new itemViewholder3(view);
        }
        //        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return holder;
    }
    //绑定视图
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 1:
                itemViewholder1 itemViewholder1= (StoreAdapter.itemViewholder1) holder;
                itemViewholder1.tv1.setText("tv"+mdatas.get(position));
                itemViewholder1.tv2.setText("tv"+mdatas.get(position));
                break;
            case 2:
                itemViewholder2 itemViewholder2= (StoreAdapter.itemViewholder2) holder;
                itemViewholder2.tv3.setText("tv"+mdatas.get(position));
                itemViewholder2.tv4.setText("tv" + mdatas.get(position));

                itemViewholder2.iv1.setImageDrawable(mContext.getResources().getDrawable(R.drawable.attach_location));
              //  itemViewholder2.iv1.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.attach_location));

                break;
            default:
                itemViewholder3 itemViewholder3= (StoreAdapter.itemViewholder3) holder;
                itemViewholder3.tv5.setText("tv"+mdatas.get(position));

                break;
        }
    }
    //获取条目的数量
    @Override
    public int getItemCount() {
        if(mdatas!=null){
            return mdatas.size();
        }
        return 0;
    }
    //获取条目的类型,用于判断footView和一般的item;
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    //footView的ViewHolder
    class footViewHolder{

    }
    //一般Item的ViewHolder
    public class itemViewholder1 extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView tv1,tv2;
        private View item1;
        public itemViewholder1(View itemView1){
            super(itemView1);
            tv1= (TextView) itemView1.findViewById(R.id.tv1);
            tv2= (TextView) itemView1.findViewById(R.id.tv2);
            item1=itemView1.findViewById(R.id.item1);
            item1.setOnClickListener(this);
            item1.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,mdatas.get(getPosition()) + "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != onRecyclerViewListener) {
                return onRecyclerViewListener.onItemLongClick(this.getPosition());
            }

            return false;
        }
    }
    class itemViewholder2 extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView tv3,tv4;
        public ImageView iv1;
        private View item2;
        public itemViewholder2(View itemView2){
            super(itemView2);
            tv3= (TextView) itemView2.findViewById(R.id.tv3);
            tv4= (TextView) itemView2.findViewById(R.id.tv4);
            iv1= (ImageView) itemView2.findViewById(R.id.iv1);
            item2=itemView2.findViewById(R.id.item2);
            item2.setOnClickListener(this);
            item2.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,mdatas.get(getPosition()) + "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(mContext,mdatas.get(getPosition()) + "", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    class itemViewholder3 extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView tv5;
        private View item3;
        public itemViewholder3(View itemView3){
            super(itemView3);
            tv5= (TextView) itemView3.findViewById(R.id.tv5);
            item3=itemView3.findViewById(R.id.item3);
            item3.setOnClickListener(this);
            item3.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(mContext,mdatas.get(getPosition()) + "", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,mdatas.get(getPosition()) + "", Toast.LENGTH_SHORT).show();
        }
    }
}
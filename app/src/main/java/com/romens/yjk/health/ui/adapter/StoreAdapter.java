package com.romens.yjk.health.ui.adapter;

/**
 * Created by hzh on 2015/8/13.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.yjk.health.R;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.model.TestEntity;
import com.romens.yjk.health.ui.MedicinalDetailActivity;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter{
    private List<TestEntity> mdatas;
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


    public StoreAdapter(List<TestEntity> datas, Context context, View headView, View footView){
        this.mdatas=datas;
        this.mContext=context;
        this.mFootView=mFootView;

    }

    //创建视图
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        RecyclerView.ViewHolder holder=null;
        if(viewType==100){
            view = View.inflate(mContext, R.layout.list_item_store_item1, null);
            holder=new itemViewholder1(view);
        }else if(viewType==2200){
            view = View.inflate(mContext, R.layout.list_item_store_item3, null);
            holder=new itemViewholder3(view);
        }else{
            view = View.inflate(mContext, R.layout.list_item_medicinal_store, null);
            holder=new itemViewholder2(view);
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
            case 100:
                itemViewholder1 itemViewholder1= (StoreAdapter.itemViewholder1) holder;
                itemViewholder1.tv1.setText("tv"+mdatas.get(position));
                itemViewholder1.tv2.setText("tv"+mdatas.get(position));
                break;
            case 2200:
                itemViewholder3 itemViewholder3= (StoreAdapter.itemViewholder3) holder;
                itemViewholder3.tv5.setText("tv"+mdatas.get(position));
                break;
            default:
                itemViewholder2 itemViewholder2= (StoreAdapter.itemViewholder2) holder;
                itemViewholder2.tv_name.setText(mdatas.get(position).getJson());
                itemViewholder2.tv_info.setText(mdatas.get(position).getInfor());
                itemViewholder2.iv.setImageBitmap(ImageUtils.bindLocalImage(mdatas.get(position).getImageUrl()));
                Drawable defaultDrawables =  itemViewholder2.iv.getDrawable();
                ImageManager.loadForView(mContext, itemViewholder2.iv,mdatas.get(position).getImageUrl(), defaultDrawables, defaultDrawables);
                itemViewholder2.view.setVisibility(View.GONE);
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
        return mdatas.get(position).getType();
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
        public TextView tv_name,tv_info;
        public ImageView iv;
        private RelativeLayout rl_container;
        private  View view;
        public itemViewholder2(View itemView2){
            super(itemView2);
            tv_name= (TextView) itemView2.findViewById(R.id.tv_store_name);
            tv_info= (TextView) itemView2.findViewById(R.id.tv_store_infor);
            iv= (ImageView) itemView2.findViewById(R.id.iv_store);
            rl_container= (RelativeLayout) itemView2.findViewById(R.id.rl_container);
            view=itemView2.findViewById(R.id.view);
            rl_container.setOnClickListener(this);
            rl_container.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Intent i=new Intent(mContext, MedicinalDetailActivity.class);
//            mContext.startActivity(i);
            UIOpenHelper.openMedicineActivity(mContext,"");
        }

        @Override
        public boolean onLongClick(View v) {

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
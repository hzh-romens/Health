package com.romens.yjk.health.ui.adapter;

import android.content.Context;
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
import com.romens.yjk.health.ui.IllnessActivity;

import java.util.List;

/**
 * Created by hzh on 2015/8/14.
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
        View view=View.inflate(mContext, R.layout.list_item_medicinal_store, null);
           RecyclerView.ViewHolder holder=new itemViewholder(view);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        itemViewholder itemViewholder= (IllnessAdapter.itemViewholder) viewHolder;
        itemViewholder.tv_name.setText("药品带回家");
        itemViewholder.tv_info.setText("电视动画看手机");
        itemViewholder.iv.setImageBitmap(ImageUtils.bindLocalImage("http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg"));
        Drawable defaultDrawables =  itemViewholder.iv.getDrawable();
        ImageManager.loadForView(mContext, itemViewholder.iv, "http://img1.imgtn.bdimg.com/it/u=2891821452,2907039089&fm=21&gp=0.jpg", defaultDrawables, defaultDrawables);
        itemViewholder.view.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if(mdatas!=null){
            return mdatas.size();
        }
        return 0;
    }
    public class itemViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_name,tv_info;
        public ImageView iv;
        private RelativeLayout item;
        private View view;

        public itemViewholder(View itemView){
            super(itemView);
            tv_name= (TextView) itemView.findViewById(R.id.tv_store_name);
            tv_info= (TextView) itemView.findViewById(R.id.tv_store_infor);
                iv= (ImageView) itemView.findViewById(R.id.iv_store);
                item= (RelativeLayout) itemView.findViewById(R.id.rl_container);
            view=itemView.findViewById(R.id.view);
                item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,mdatas.get(getPosition())+"",Toast.LENGTH_SHORT).show();
        }
    }

}


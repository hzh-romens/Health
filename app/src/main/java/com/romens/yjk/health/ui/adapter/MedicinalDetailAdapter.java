package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.romens.android.io.image.ImageManager;
import com.romens.android.io.image.ImageUtils;
import com.romens.yjk.health.R;
import com.romens.yjk.health.model.AboutTestEntity;
import com.romens.yjk.health.model.TestEntity;
import com.romens.yjk.health.ui.components.ModifyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romens007 on 2015/8/17.
 */
public class MedicinalDetailAdapter extends RecyclerView.Adapter {
    private List<TestEntity> mdatas;
    private Context mContext;

    public MedicinalDetailAdapter(List<TestEntity> data, Context context) {
        this.mdatas = data;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (viewType == 0) {
            view = View.inflate(mContext, R.layout.list_item_medicinal_information3, null);
            holder = new ImageHolder(view);
        } else if (viewType == 1) {
            view = View.inflate(mContext, R.layout.list_item_medicinal_info, null);
            holder = new informationHolder(view);
        }
        //else if (viewType == 2) {
           // view = View.inflate(mContext, R.layout.list_item_medicinal_information2, null);
            //holder = new aboutHolder(view);
      //  } else if (viewType == 3) {
          //  view = View.inflate(mContext, R.layout.list_item_medicinal_information2, null);
            //holder = new aboutHolder(view);
       // }
        else if (viewType == 4) {
            view = View.inflate(mContext, R.layout.list_shadow_item, null);
            holder = new ShadowHolder(view);
        } else if (viewType == 5) {
            view = View.inflate(mContext, R.layout.list_item_group_name, null);
            holder = new GroupNameHolder(view);
        } else if (viewType == 6) {
            view = View.inflate(mContext, R.layout.list_item_medicinal_store, null);
            holder = new StoreHolder(view);
        } else if (viewType == 7) {
            view = View.inflate(mContext, R.layout.list_item_medicinalmore, null);
            holder = new MoreHolder(view);
        } else if(viewType==8){
            view = View.inflate(mContext, R.layout.list_item_medicinal_information2, null);
            holder = new aboutHolder(view);
        }else{
            view=View.inflate(mContext,R.layout.list_item_empty_view,null);
            holder=new EmptyHolder(view);
        }
        //        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TestEntity testEntity = mdatas.get(position);
        switch (getItemViewType(position)) {
            case 0:
                ImageHolder imageHolder = (ImageHolder) holder;
                String imageUrl = mdatas.get(position).getImageUrl();
                imageHolder.iv.setImageBitmap(ImageUtils.bindLocalImage(imageUrl));
                Drawable defaultDrawable = imageHolder.iv.getDrawable();
                ImageManager.loadForView(mContext, imageHolder.iv,imageUrl, defaultDrawable, defaultDrawable);

                break;
            case 1:
                informationHolder inforHolder = (informationHolder) holder;
                inforHolder.tv_name.setText(testEntity.getJson());
                inforHolder.tv_information.setText(testEntity.getInfor());
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                ShadowHolder shadowHolder= (ShadowHolder) holder;
                break;
            case 5:
                GroupNameHolder groupNameHolder= (GroupNameHolder) holder;
                groupNameHolder.tv_group_name.setText(testEntity.getJson());
                if("在线药店".equals(testEntity.getJson())){
                    groupNameHolder.more.setVisibility(View.GONE);
                }
                break;
            case 6:
                StoreHolder storeHolder= (StoreHolder) holder;
                storeHolder.iv_store.setImageBitmap(ImageUtils.bindLocalImage(testEntity.getImageUrl()));
                Drawable defaultDrawables =  storeHolder.iv_store.getDrawable();
                ImageManager.loadForView(mContext,  storeHolder.iv_store,testEntity.getImageUrl(), defaultDrawables, defaultDrawables);
                storeHolder.tv_store_name.setText(testEntity.getJson());
                storeHolder.tv_store_infor.setText(testEntity.getInfor());
                break;
            case 7:
                MoreHolder moreHolder= (MoreHolder) holder;
                break;
            case 8:
                aboutHolder holders = (aboutHolder) holder;
                List<AboutTestEntity>datas = new ArrayList<AboutTestEntity>();
                datas.add(new AboutTestEntity(testEntity.getImageUrl(),testEntity.getJson(),testEntity.getInfor()));
                datas.add(new AboutTestEntity(testEntity.getImageUrl(),testEntity.getJson(),testEntity.getInfor()));
                datas.add(new AboutTestEntity(testEntity.getImageUrl(),testEntity.getJson(),testEntity.getInfor()));
                datas.add(new AboutTestEntity(testEntity.getImageUrl(),testEntity.getJson(),testEntity.getInfor()));
                GridViewAdapter gridViewAdapter = new GridViewAdapter(datas, mContext);
                holders.gridView.setAdapter(gridViewAdapter);
                break;
            default:
                EmptyHolder emptyHolder= (EmptyHolder) holder;
                break;
        }
    }


    @Override
    public int getItemCount() {
        if (mdatas != null) {
            return mdatas.size();
        }
        return 0;
    }
    public class EmptyHolder extends RecyclerView.ViewHolder{
        public EmptyHolder(View view){
            super(view);
        }
    }
    //医药信息的holder
    public class informationHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_information;

        public informationHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_medicinal_name);
            tv_information = (TextView) view.findViewById(R.id.tv_medicinal_information);
            //这里设置监听事件
        }
    }
    //相关医药的holder
    public class aboutHolder extends RecyclerView.ViewHolder {
        private ModifyGridView gridView;

        public aboutHolder(View view) {
            super(view);
            gridView = (ModifyGridView) view.findViewById(R.id.gridView);
            //这里设置监听事件
        }
    }
    //药品图片的holder
    public class ImageHolder extends RecyclerView.ViewHolder {
        private ImageView iv;

        public ImageHolder(View view) {
            super(view);
            iv = (ImageView) view.findViewById(R.id.iv_detail);
        }
    }
    //在线药店的holder
    public class StoreHolder extends RecyclerView.ViewHolder{
        private  ImageView iv_store;
        private  TextView tv_store_name,tv_store_infor;
        public StoreHolder(View view){
            super(view);
            iv_store= (ImageView) view.findViewById(R.id.iv_store);
            tv_store_name= (TextView) view.findViewById(R.id.tv_store_name);
            tv_store_infor= (TextView) view.findViewById(R.id.tv_store_infor);
        }
    }
    //group的holder
    public class GroupNameHolder extends RecyclerView.ViewHolder{
        private TextView tv_group_name;
        private Button more;
        public GroupNameHolder(View view){
            super(view);
            tv_group_name= (TextView) view.findViewById(R.id.group_name);
            more= (Button) view.findViewById(R.id.moreButton);
        }
    }
    //Shadow的holder
    public class ShadowHolder extends RecyclerView.ViewHolder{
        private View empty_view;
        public ShadowHolder(View view){
            super(view);
            empty_view=view.findViewById(R.id.empty_view);
        }
    }
    //更多的holder
    public class MoreHolder extends  RecyclerView.ViewHolder{
        private TextView tv_more;
        public MoreHolder(View view){
            super(view);
            tv_more= (TextView) view.findViewById(R.id.tv_more);
        }

    }

    //获取条目的类型,用于判断footView和一般的item;
    @Override
    public int getItemViewType(int position) {
        return mdatas.get(position).getType();
    }

}

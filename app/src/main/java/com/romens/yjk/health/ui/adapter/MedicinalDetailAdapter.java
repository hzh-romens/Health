package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.controls.ADBaseControl;
import com.romens.yjk.health.ui.controls.ADEmptyControl;
import com.romens.yjk.health.ui.controls.ADErrorControl;
import com.romens.yjk.health.ui.controls.ADGroupControl;
import com.romens.yjk.health.ui.controls.ADGroupNameControls;
import com.romens.yjk.health.ui.controls.ADHorizontalScrollControl;
import com.romens.yjk.health.ui.controls.ADIllustrationControl;
import com.romens.yjk.health.ui.controls.ADMedicinalDetailControl;
import com.romens.yjk.health.ui.controls.ADMoreControl;
import com.romens.yjk.health.ui.controls.ADPagerControl;
import com.romens.yjk.health.ui.controls.ADStoreControls;
import com.romens.yjk.health.ui.controls.ControlType;

import java.util.LinkedList;

/**
 * Created by AUSU on 2015/10/14.
 */
public class MedicinalDetailAdapter extends RecyclerView.Adapter<ADHolder>{
    private Context mContext;
   private SparseArray<ADBaseControl> adBaseControls =new SparseArray<ADBaseControl>();
   public MedicinalDetailAdapter(Context context){
       this.mContext=context;
   }
    public void bindData(SparseArray<ADBaseControl> mAdBaseControls){
        adBaseControls.clear();
        if(mAdBaseControls!=null){
            this.adBaseControls=mAdBaseControls;
        }
        notifyDataSetChanged();
    }
    @Override
    public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType== ControlType.TYPE_AD_PAGER){
            return ADPagerControl.createViewHolder(mContext);
        }else if(viewType==6){
            return ADMedicinalDetailControl.createViewHolder(mContext);
        }else if(viewType==11){
          return ADIllustrationControl.createViewHolder(mContext);
        } else if(viewType==2){
            return ADGroupControl.createViewHolder(mContext);
        }else if(viewType==7){
            return ADStoreControls.createViewHolder(mContext);
        }else if(viewType==9){
            return ADMoreControl.createViewHolder(mContext);
        }else if(viewType==12){
            return ADGroupNameControls.createViewHolder(mContext);
        }else if(viewType==13){
            return ADHorizontalScrollControl.createViewHolder(mContext);
        }else if(viewType==-2){
            return ADErrorControl.createViewHolder(mContext);
        }
        return ADEmptyControl.createViewHolder(mContext);
    }

    @Override
    public void onBindViewHolder(ADHolder holder, int position) {
        adBaseControls.get(position).bindViewHolder(mContext,holder);
    }

    @Override
    public int getItemCount() {
        return adBaseControls.size()==0?0:adBaseControls.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(!("".equals(adBaseControls.get(position).getType()))) {
            return adBaseControls.get(position).getType();
        }else{
            return ControlType.TYPE_EMPTY;
        }
    }
}

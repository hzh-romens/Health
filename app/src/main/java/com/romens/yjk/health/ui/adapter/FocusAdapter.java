package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.romens.yjk.health.ui.cells.ADHolder;
import com.romens.yjk.health.ui.controls.ADBaseControl;
import com.romens.yjk.health.ui.controls.ADEmptyControl;
import com.romens.yjk.health.ui.controls.ADFunctionControl;
import com.romens.yjk.health.ui.controls.ADGroupControl;
import com.romens.yjk.health.ui.controls.ADImageControl;
import com.romens.yjk.health.ui.controls.ADPagerControl;

/**
 * Created by siery on 15/8/13.
 */
public class FocusAdapter extends RecyclerView.Adapter<ADHolder> {
    private Context context;

    private SparseArray<ADBaseControl> adControls = new SparseArray<>();

    public FocusAdapter(Context _context) {
        this.context = _context;
    }

    public void bindData(SparseArray<ADBaseControl> controls) {
        adControls.clear();
        if (controls != null) {
            adControls = controls;
        }
        notifyDataSetChanged();
    }

    @Override
    public ADHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == ADPagerControl.TYPE) {
            return ADPagerControl.createViewHolder(context);
        } else if (viewType == ADFunctionControl.TYPE) {
            return ADFunctionControl.createViewHolder(context);
        } else if (viewType == ADImageControl.TYPE) {
            return ADImageControl.createViewHolder(context);
        } else if (viewType == ADGroupControl.TYPE) {
            return ADGroupControl.createViewHolder(context);
        }
        return ADEmptyControl.createViewHolder(context);
    }

    @Override
    public void onBindViewHolder(ADHolder holder, int position) {
        adControls.get(position).bindViewHolder(context, holder);
    }

    @Override
    public int getItemCount() {
        return adControls.size();
    }

    @Override
    public int getItemViewType(int position) {
        return adControls.get(position).getType();
    }
}

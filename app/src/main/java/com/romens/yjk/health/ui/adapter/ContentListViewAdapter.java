package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.ui.cells.DrugMenuChildCell;
import com.romens.yjk.health.ui.cells.TextViewCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2016/1/12.
 */
public class ContentListViewAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<DrugGroupEntity> data;
    private ContentDelegate delegate;

    public ContentListViewAdapter(Context context, ContentDelegate delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    public void setData(List<DrugGroupEntity> data) {
        this.data = data;
    }

    public List<DrugGroupEntity> getData() {
        return data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DrugMenuChildCell cell = new DrugMenuChildCell(context);
        cell.setBackgroundResource(R.drawable.drug_child_states);
        cell.setClickable(true);
        return new Holder(cell);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.itemView instanceof DrugMenuChildCell) {
            DrugMenuChildCell cell = (DrugMenuChildCell) holder.itemView;
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delegate != null) {
                        delegate.onCellSelected(data.get(position));
                    }
                }
            });
            cell.setValue(data.get(position).getName());
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public interface ContentDelegate {
        void onCellSelected(DrugGroupEntity entity);
    }
}

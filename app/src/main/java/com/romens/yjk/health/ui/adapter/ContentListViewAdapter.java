package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.db.entity.DrugGroupEntity;
import com.romens.yjk.health.ui.cells.TextViewCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2016/1/12.
 */
public class ContentListViewAdapter extends BaseAdapter {

    private Context context;
    private List<DrugGroupEntity> data;

    public ContentListViewAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    public void setData(List<DrugGroupEntity> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextViewCell cell = new TextViewCell(context);
        cell.setText(data.get(position).getName(), false);
        return cell;
    }
}

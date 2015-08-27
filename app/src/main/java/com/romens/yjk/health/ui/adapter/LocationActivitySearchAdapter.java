package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.romens.yjk.health.model.LocationEntity;
import com.romens.yjk.health.ui.cells.LocationCell;


public class LocationActivitySearchAdapter extends BaseLocationAdapter {

    public LocationActivitySearchAdapter(Context context) {
        super(context);
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public boolean isEmpty() {
        return places.isEmpty();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new LocationCell(mContext);
        }
        ((LocationCell) view).setLocation(places.get(i), i != places.size() - 1);
        return view;
    }

    @Override
    public LocationEntity getItem(int i) {
        if (i >= 0 && i < places.size()) {
            return places.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

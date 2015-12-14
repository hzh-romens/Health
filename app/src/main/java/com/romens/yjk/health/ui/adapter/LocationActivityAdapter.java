package com.romens.yjk.health.ui.adapter;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.ui.cells.EmptyCell;
import com.romens.yjk.health.model.LocationEntity;
import com.romens.yjk.health.ui.cells.GreySectionCell;
import com.romens.yjk.health.ui.cells.LocationItemCell;
import com.romens.yjk.health.ui.cells.LocationLoadingCell;
import com.romens.yjk.health.ui.cells.SendLocationCell;

public class LocationActivityAdapter extends BaseLocationAdapter {


    private int overScrollHeight;
    private SendLocationCell sendLocationCell;
    private Location gpsLocation;
    private Location customLocation;

    public LocationActivityAdapter(Context context) {
        super(context);
    }

    public void setOverScrollHeight(int value) {
        overScrollHeight = value;
    }

    public void setGpsLocation(Location location) {
        gpsLocation = location;
        updateCell();
    }

    public void setCustomLocation(Location location) {
        customLocation = location;
        updateCell();
    }

    private void updateCell() {
        if (sendLocationCell != null) {
            if (customLocation != null) {
                sendLocationCell.setText("搜索当前地图位置附近药店)", String.format("(%f,%f)", customLocation.getLatitude(), customLocation.getLongitude()));
            } else {
                if (gpsLocation != null) {
                    sendLocationCell.setText("搜索我的位置附近药店", String.format("已定位,精确度 (%d)", (int) gpsLocation.getAccuracy()));
                } else {
                    sendLocationCell.setText("我的位置", "定位中...");
                }
            }
        }
    }

    @Override
    public int getCount() {
        if (searching || !searching && places.isEmpty()) {
            return 4;
        }
        return 3 + places.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i == 0) {
            if (view == null) {
                view = new EmptyCell(mContext);
            }
            ((EmptyCell) view).setHeight(overScrollHeight);
        } else if (i == 1) {
            if (view == null) {
                view = new SendLocationCell(mContext);
            }
            sendLocationCell = (SendLocationCell) view;
            updateCell();
            return view;
        } else if (i == 2) {
            if (view == null) {
                view = new GreySectionCell(mContext);
            }
            ((GreySectionCell) view).setText("或者选择列表中的位置");
        } else if (searching || !searching && places.isEmpty()) {
            if (view == null) {
                view = new LocationLoadingCell(mContext);
            }
            ((LocationLoadingCell) view).setLoading(searching);
        } else {
            //                view = new LocationCell(mContext);
//            }
//            ((LocationCell) view).setLocation(places.get(i - 3), true);
            if (view == null) {
                view = new LocationItemCell(mContext);
            }
            LocationItemCell cell = (LocationItemCell) view;
            LocationEntity entity = places.get(i - 3);
            cell.setLocation(entity.name, entity.address, entity.distance, true, entity.typeDesc);
        }
        return view;
    }

    @Override
    public LocationEntity getItem(int i) {
        if (i > 2 && i < places.size() + 3) {
            return places.get(i - 3);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else if (position == 2) {
            return 2;
        } else if (searching || !searching && places.isEmpty()) {
            return 4;
        }
        return 3;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !(position == 2 || position == 0 || position == 3 && (searching || !searching && places.isEmpty()));
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

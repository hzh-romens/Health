package com.romens.yjk.health.model;

import android.text.TextUtils;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by siery on 15/7/14.
 */
public class LocationEntity {
    public String id;
    public String name;
    public String address;
    public double lat;
    public double lon;
    public int distance;
    public String typeDesc;

    public LocationEntity() {
    }

    public LocationEntity(PoiItem poiItem) {
        id = poiItem.getPoiId();
        name = poiItem.getTitle();
        address = poiItem.getSnippet();
        LatLonPoint point = poiItem.getLatLonPoint();
        lat = point.getLatitude();
        lon = point.getLongitude();
        distance = poiItem.getDistance();
        typeDesc = poiItem.getTypeDes();
    }

    public static LocationEntity mapToEntity(LinkedTreeMap<String, String> map) {
        LocationEntity entity = new LocationEntity();
        entity.id = map.get("GUID");
        entity.name = map.get("NAME");
        entity.address = map.get("ADDRESS");
        String latStr = map.get("lat");
        entity.lat = Double.valueOf(TextUtils.isEmpty(latStr) ? "0" : latStr);
        String lngStr = map.get("lng");
        entity.lon = Double.valueOf(TextUtils.isEmpty(lngStr) ? "0" : lngStr);
        String disStr = map.get("dis");
        entity.distance = Integer.valueOf(TextUtils.isEmpty(disStr) ? "0" : disStr);
        entity.typeDesc = "药店";
        return entity;
    }
}

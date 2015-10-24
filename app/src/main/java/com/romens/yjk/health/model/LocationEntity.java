package com.romens.yjk.health.model;

import android.text.TextUtils;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

/**
 * Created by siery on 15/7/14.
 */
public class LocationEntity implements Serializable {
    public String id;
    public String name;
    public String address;
    public double lat;
    public double lon;
    public double distance;
    public String typeDesc;

    public String getId() {
        return id;
    }

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
        entity.id = map.get("ID");
        entity.name = map.get("SHOPNAME");
        entity.address = map.get("ADDRESS");
        String latStr = map.get("LAT");
        entity.lat = Double.valueOf(TextUtils.isEmpty(latStr) ? "0" : latStr);
        String lngStr = map.get("LON");
        entity.lon = Double.valueOf(TextUtils.isEmpty(lngStr) ? "0" : lngStr);
        String disStr = map.get("DISTANCE");
        entity.distance = Double.valueOf(TextUtils.isEmpty(disStr) ? "0" : disStr);
        entity.typeDesc = "药店";
        return entity;
    }
}

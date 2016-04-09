package com.romens.yjk.health.model;

import android.location.Location;
import android.text.TextUtils;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

/**
 * Created by siery on 15/7/14.
 */
public class LocationEntity implements Serializable {
    public String id;
    public String name;
    public String address;

    public String cityCode;
    public String city;

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

        cityCode = poiItem.getCityCode();
        city = poiItem.getCityName();

        LatLonPoint point = poiItem.getLatLonPoint();
        lat = point.getLatitude();
        lon = point.getLongitude();
        distance = poiItem.getDistance();
        typeDesc = poiItem.getTypeDes();
    }

    public LatLng createLocation() {
        LatLng latLng = new LatLng(lat, lon);
        return latLng;
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

    public static LocationEntity jsonToEntity(JsonNode map) {
        LocationEntity entity = new LocationEntity();
        entity.id = map.get("ID").asText();
        entity.name = map.get("SHOPNAME").asText();
        entity.address = map.get("ADDRESS").asText();
        entity.lat = map.get("LAT").asDouble(0);
        //String lngStr = map.get("LON");
        entity.lon = map.get("LON").asDouble(0);
        //String disStr = map.get("DISTANCE");
        entity.distance = map.get("DISTANCE").asDouble(0);
        entity.typeDesc = "药店";
        return entity;
    }
}

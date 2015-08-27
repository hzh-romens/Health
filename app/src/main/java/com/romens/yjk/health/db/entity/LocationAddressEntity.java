package com.romens.yjk.health.db.entity;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.romens.yjk.health.db.DBHelper;

/**
 * Created by siery on 15/8/20.
 */
public class LocationAddressEntity {
    public String id;
    public String parentId;
    public String code;
    public String name;
    public String zipCode;
    public String description;
    public int state;
    public int createdTime;
    public int updatedTime;

    public LocationAddressEntity() {
    }

    public static LocationAddressEntity mapToEntity(LinkedTreeMap<String, String> map) {
        LocationAddressEntity entity = new LocationAddressEntity();
        entity.id = DBHelper.toDBString(map.get("GUID"));
        entity.parentId = DBHelper.toDBString(map.get("PARENTGUID"));
        entity.code = DBHelper.toDBString(map.get("CODE"));
        entity.name = DBHelper.toDBString(map.get("NAME"));
        entity.zipCode = DBHelper.toDBString(map.get("ZIPCODE"));
        entity.description = DBHelper.toDBString(map.get("DESCRIPTION"));

        String stateStr = map.get("state");
        entity.state = Integer.valueOf(TextUtils.isEmpty(stateStr) ? "0" : stateStr);
        String createdTimeStr = map.get("created");
        entity.createdTime = Integer.valueOf(TextUtils.isEmpty(createdTimeStr) ? "0" : createdTimeStr);
        String updatedTimeStr = map.get("updated");
        entity.updatedTime = Integer.valueOf(TextUtils.isEmpty(updatedTimeStr) ? "0" : updatedTimeStr);
        return entity;
    }
}

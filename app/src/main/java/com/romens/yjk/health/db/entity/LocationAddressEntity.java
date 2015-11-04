package com.romens.yjk.health.db.entity;

import com.google.gson.internal.LinkedTreeMap;
import com.romens.yjk.health.db.DBHelper;

/**
 * Created by siery on 15/8/20.
 */
public class LocationAddressEntity {
    private String key;
    private String parentId;
    private String name;

    public LocationAddressEntity() {
    }

    public String getKey() {
        return key;
    }

    public String getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LocationAddressEntity serverMapToEntity(LinkedTreeMap<String, String> map) {
        LocationAddressEntity entity = new LocationAddressEntity();
        entity.key = DBHelper.toDBString(map.get("GUID"));
        entity.parentId = DBHelper.toDBString(map.get("PAEENTGUID"));
        entity.name = DBHelper.toDBString(map.get("NAME"));
        return entity;
    }

//    public static LocationAddressEntity cacheMapToEntity(LinkedTreeMap<String, String> map) {
//        LocationAddressEntity entity = new LocationAddressEntity();
//        entity.key = DBHelper.toDBString(map.get("GUID"));
//        entity.parentId = DBHelper.toDBString(map.get("PARENTGUID"));
//        entity.code = DBHelper.toDBString(map.get("CODE"));
//        entity.name = DBHelper.toDBString(map.get("NAME"));
//        entity.zipCode = DBHelper.toDBString(map.get("ZIPCODE"));
//        entity.description = DBHelper.toDBString(map.get("DESCRIPTION"));
//
//        String stateStr = map.get("state");
//        entity.state = Integer.valueOf(TextUtils.isEmpty(stateStr) ? "0" : stateStr);
//        String createdTimeStr = map.get("created");
//        entity.createdTime = Integer.valueOf(TextUtils.isEmpty(createdTimeStr) ? "0" : createdTimeStr);
//        String updatedTimeStr = map.get("updated");
//        entity.updatedTime = Integer.valueOf(TextUtils.isEmpty(updatedTimeStr) ? "0" : updatedTimeStr);
//        return entity;
//    }
}

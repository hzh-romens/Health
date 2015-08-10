package com.romens.yjk.health.db.entity;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by siery on 15/5/22.
 */
public class DiscoveryEntity {
    private long id;
    private String key;
    private String iconRes;
    private String iconUrl;
    private String name;
    private int status;
    private int created;
    private int updated;

    private String value;

    private boolean isCover = false;
    private int sortIndex;

    private int primaryColor;

    public DiscoveryEntity() {
    }

    //get

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getIconRes() {
        return iconRes;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public int getCreated() {
        return created;
    }

    public int getUpdated() {
        return updated;
    }

    public String getValue() {
        return value;
    }

    public boolean isCover() {
        return isCover;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public int getPrimaryColor(){
        return primaryColor;
    }

    //set

    public void setId(long id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setIconRes(String iconRes) {
        this.iconRes = iconRes;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setIsCover(boolean value) {
        isCover = value;
    }

    public void setSortIndex(int index) {
        this.sortIndex = index;
    }

    public void setPrimaryColor(int color){
        this.primaryColor=color;
    }

    public static DiscoveryEntity mapToEntity(LinkedTreeMap<String, String> map){
        DiscoveryEntity entity=new DiscoveryEntity();
        return entity;
    }
}

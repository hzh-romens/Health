package com.romens.yjk.health.model;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by HZH on 2015/12/9.
 */
public class DeliverytypeEntity {
    private String GUID;
    private String NAME;

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public static DeliverytypeEntity mapToEntity(LinkedTreeMap<String, String> item) {
        DeliverytypeEntity entity = new DeliverytypeEntity();
        entity.setGUID(item.get("GUID"));
        entity.setNAME(item.get("NAME"));
        return entity;
    }
}

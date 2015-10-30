package com.romens.yjk.health.db.entity;

import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by anlc on 2015/9/19.
 * 城市选择的实体
 */
public class CitysEntity {
    private long id;
    private String guid;
    private String parentGuid;
    private String name;
    private String level;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getParentGuid() {
        return parentGuid;
    }

    public void setParentGuid(String parentGuid) {
        this.parentGuid = parentGuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public static CitysEntity mapToEntity(LinkedTreeMap<String, String> item) {
        CitysEntity entity = new CitysEntity();
        entity.setGuid(item.get("GUID"));
        entity.setParentGuid(item.get("PAEENTGUID"));
        entity.setName(item.get("NAME"));
        entity.setLevel(item.get("LEVEL"));
        return entity;
    }

}

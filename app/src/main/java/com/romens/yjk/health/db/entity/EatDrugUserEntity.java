package com.romens.yjk.health.db.entity;

/**
 * Created by anlc on 2015/9/11.
 * 用药提醒的实体
 */
public class EatDrugUserEntity {

    private String name;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

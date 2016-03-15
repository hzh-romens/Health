package com.romens.yjk.health.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by HZH on 2016/2/4.
 */
public class CuoponEntity {
    private String couponguid;
    private String guid;
    private String orgguid;
    private String gettime;
    private String isused;
    private String name;
    private String amount;
    private String limitamount;
    private String startdate;
    private String enddate;
    private String shuoming;

    public String getCouponguid() {
        return couponguid;
    }

    public void setCouponguid(String couponguid) {
        this.couponguid = couponguid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getOrgguid() {
        return orgguid;
    }

    public void setOrgguid(String orgguid) {
        this.orgguid = orgguid;
    }

    public String getGettime() {
        return gettime;
    }

    public void setGettime(String gettime) {
        this.gettime = gettime;
    }

    public String getIsused() {
        return isused;
    }

    public void setIsused(String isused) {
        this.isused = isused;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLimitamount() {
        return limitamount;
    }

    public void setLimitamount(String limitamount) {
        this.limitamount = limitamount;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getShuoming() {
        return shuoming;
    }

    public void setShuoming(String shuoming) {
        this.shuoming = shuoming;
    }

    public static CuoponEntity toEntity(JsonNode jsonNode) {
        CuoponEntity entity = new CuoponEntity();
        entity.setCouponguid(jsonNode.get("COUPONGUID").asText());
        entity.setGuid(jsonNode.get("GUID").asText());
        entity.setOrgguid(jsonNode.get("ORGGUID").asText());
        entity.setGettime(jsonNode.get("GETTIME").asText());
        entity.setAmount(jsonNode.get("AMOUNT").asText());
        entity.setEnddate(jsonNode.get("ENDDATE").asText());
        entity.setIsused(jsonNode.get("ISUSED").asText());
        entity.setLimitamount(jsonNode.get("LIMITAMOUNT").asText());
        entity.setName(jsonNode.get("NAME").asText());
        entity.setShuoming(jsonNode.get("SHUOMING").asText());
        entity.setStartdate(jsonNode.get("STARTDATE").asText());
        return entity;
    }
}

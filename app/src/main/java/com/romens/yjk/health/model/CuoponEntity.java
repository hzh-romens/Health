package com.romens.yjk.health.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static List<CuoponEntity> toEntity(String json) {
        List<CuoponEntity> result = new ArrayList<CuoponEntity>();
        CuoponEntity entity = new CuoponEntity();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                entity.setCouponguid(jsonObject.getString("COUPONGUID"));

                entity.setGuid(jsonObject.getString("GUID"));

                entity.setOrgguid(jsonObject.getString("ORGGUID"));

                entity.setGettime(jsonObject.getString("GETTIME"));

                entity.setAmount(jsonObject.getString("AMOUNT"));

                entity.setEnddate(jsonObject.getString("ENDDATE"));

                entity.setIsused(jsonObject.getString("ISUSED"));

                entity.setLimitamount(jsonObject.getString("LIMITAMOUNT"));

                entity.setName(jsonObject.getString("NAME"));

                entity.setShuoming(jsonObject.getString("SHUOMING"));

                entity.setStartdate(jsonObject.getString("STARTDATE"));

                result.add(entity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}

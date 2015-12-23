package com.romens.yjk.health.model;

import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by AUSU on 2015/9/8.
 */
public class ShopCarEntity implements Serializable {

    private int BUYCOUNT;//数量
    private Double GOODSPRICE;// 商品价格
    private String CREATETIME;// 日期
    private String NAME; //商品名称
    private String GOODSCLASSNAME; //商品分类名称
    private String GOODURL; //商品图片url
    private String CODE;// 商品code
    private String DETAILDESCRIPTION;// 商品描述
    private String SPEC;// 商品规格
    private String SHOPID;// 药店id
    private String SHOPNAME;//药店名称
    private String CHECK;
    public Long id;
    private String GOODSGUID;//药品ID
    private String USERGUID;
    public int createdTime;
    public int updatedTime;
    private String memberId;

    public String getUSERGUID() {
        return USERGUID;
    }

    public void setUSERGUID(String USERGUID) {
        this.USERGUID = USERGUID;
    }

    public String getGOODSGUID() {
        return GOODSGUID;
    }

    public void setGOODSGUID(String GOODSGUID) {
        this.GOODSGUID = GOODSGUID;
    }

    public String getCHECK() {
        return CHECK;
    }

    public void setCHECK(String CHECK) {
        this.CHECK = CHECK;
    }

    public int getBUYCOUNT() {
        return BUYCOUNT;
    }

    public void setBUYCOUNT(int BUYCOUNT) {
        this.BUYCOUNT = BUYCOUNT;
    }

    public Double getGOODSPRICE() {
        return GOODSPRICE;
    }

    public void setGOODSPRICE(Double GOODSPRICE) {
        this.GOODSPRICE = GOODSPRICE;
    }

    public String getCREATETIME() {
        return CREATETIME;
    }

    public void setCREATETIME(String CREATETIME) {
        this.CREATETIME = CREATETIME;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getGOODSCLASSNAME() {
        return GOODSCLASSNAME;
    }

    public void setGOODSCLASSNAME(String GOODSCLASSNAME) {
        this.GOODSCLASSNAME = GOODSCLASSNAME;
    }

    public String getGOODURL() {
        return GOODURL;
    }

    public void setGOODURL(String GOODURL) {
        this.GOODURL = GOODURL;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getDETAILDESCRIPTION() {
        return DETAILDESCRIPTION;
    }

    public void setDETAILDESCRIPTION(String DETAILDESCRIPTION) {
        this.DETAILDESCRIPTION = DETAILDESCRIPTION;
    }

    public String getSPEC() {
        return SPEC;
    }

    public void setSPEC(String SPEC) {
        this.SPEC = SPEC;
    }

    public String getSHOPID() {
        return SHOPID;
    }

    public void setSHOPID(String SHOPID) {
        this.SHOPID = SHOPID;
    }

    public String getSHOPNAME() {
        return SHOPNAME;
    }

    public void setSHOPNAME(String SHOPNAME) {
        this.SHOPNAME = SHOPNAME;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public int getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(int updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public static ShopCarEntity jsonObjectToEntity(JSONObject jsonObject) throws JSONException {
        ShopCarEntity entity = new ShopCarEntity();
        entity.setSPEC(jsonObject.getString("SPEC"));
        entity.setNAME(jsonObject.getString("NAME"));
        entity.setBUYCOUNT(jsonObject.getInt("BUYCOUNT"));
        entity.setCODE(jsonObject.getString("CODE"));
        entity.setCREATETIME(jsonObject.getString("CREATETIME"));
        entity.setDETAILDESCRIPTION(jsonObject.getString("DETAILDESCRIPTION"));
        entity.setGOODSCLASSNAME(jsonObject.getString("GOODSCLASSNAME"));
        entity.setGOODSGUID(jsonObject.getString("GOODSGUID"));
        entity.setGOODSPRICE(jsonObject.getDouble("GOODSPRICE"));
        entity.setGOODURL(jsonObject.getString("GOODURL"));
        entity.setSHOPID(jsonObject.getString("SHOPID"));
        entity.setSHOPNAME(jsonObject.getString("SHOPNAME"));
        entity.setUSERGUID(jsonObject.getString("GUID"));
        entity.setMemberId(jsonObject.getString("memberId"));
        return entity;
    }

    public static ShopCarEntity mapToEntity(LinkedTreeMap<String, String> map) {
        ShopCarEntity entity = new ShopCarEntity();
        entity.setSPEC(map.get("SPEC"));
        entity.setNAME(map.get("NAME"));
        entity.setBUYCOUNT(Integer.parseInt(map.get("BUYCOUNT")));
        entity.setCODE(map.get("CODE"));
        entity.setCREATETIME(map.get("CREATETIME"));
        entity.setDETAILDESCRIPTION(map.get("DETAILDESCRIPTION"));
        entity.setGOODSCLASSNAME(map.get("GOODSCLASSNAME"));
        entity.setGOODSGUID(map.get("GOODSGUID"));
        entity.setGOODSPRICE(Double.parseDouble(map.get("GOODSPRICE")));
        entity.setGOODURL(map.get("GOODURL"));
        entity.setSHOPID(map.get("SHOPID"));
        entity.setSHOPNAME(map.get("SHOPNAME"));
        entity.setUSERGUID(map.get("GUID"));
        entity.setMemberId(map.get("memberId"));
        return entity;
    }

}
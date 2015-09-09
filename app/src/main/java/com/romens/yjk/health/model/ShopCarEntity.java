package com.romens.yjk.health.model;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by AUSU on 2015/9/8.
 */
public class ShopCarEntity {
    public Long id;
    private String USERGUID;

    private String GUID;

    private String GOODSGUID;

    private int BUYCOUNT;

    private Double GOODSPRICE;

    private String CREATETIME;

    private String NAME;

    private String GOODSCLASSNAME;

    private String CODE;

    private String GOODURL;

    private String DETAILDESCRIPTION;

    private String SPEC;

    private String GOODSSORTGUID;

    private int NUM;

    public String CHECK;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCHECK() {
        return CHECK;
    }

    public void setCHECK(String CHECK) {
        this.CHECK = CHECK;
    }

    public void setUSERGUID(String USERGUID){
        this.USERGUID = USERGUID;
    }
    public String getUSERGUID(){
        return this.USERGUID;
    }
    public void setGUID(String GUID){
        this.GUID = GUID;
    }
    public String getGUID(){
        return this.GUID;
    }
    public void setGOODSGUID(String GOODSGUID){
        this.GOODSGUID = GOODSGUID;
    }
    public String getGOODSGUID(){
        return this.GOODSGUID;
    }
    public void setBUYCOUNT(int BUYCOUNT){
        this.BUYCOUNT = BUYCOUNT;
    }
    public int getBUYCOUNT(){
        return this.BUYCOUNT;
    }
    public void setGOODSPRICE(Double GOODSPRICE){
        this.GOODSPRICE = GOODSPRICE;
    }
    public Double getGOODSPRICE(){
        return this.GOODSPRICE;
    }
    public void setCREATETIME(String CREATETIME){
        this.CREATETIME = CREATETIME;
    }
    public String getCREATETIME(){
        return this.CREATETIME;
    }
    public void setNAME(String NAME){
        this.NAME = NAME;
    }
    public String getNAME(){
        return this.NAME;
    }
    public void setGOODSCLASSNAME(String GOODSCLASSNAME){
        this.GOODSCLASSNAME = GOODSCLASSNAME;
    }
    public String getGOODSCLASSNAME(){
        return this.GOODSCLASSNAME;
    }
    public void setCODE(String CODE){
        this.CODE = CODE;
    }
    public String getCODE(){
        return this.CODE;
    }
    public void setGOODURL(String GOODURL){
        this.GOODURL = GOODURL;
    }
    public String getGOODURL(){
        return this.GOODURL;
    }
    public void setDETAILDESCRIPTION(String DETAILDESCRIPTION){
        this.DETAILDESCRIPTION = DETAILDESCRIPTION;
    }
    public String getDETAILDESCRIPTION(){
        return this.DETAILDESCRIPTION;
    }
    public void setSPEC(String SPEC){
        this.SPEC = SPEC;
    }
    public String getSPEC(){
        return this.SPEC;
    }
    public void setGOODSSORTGUID(String GOODSSORTGUID){
        this.GOODSSORTGUID = GOODSSORTGUID;
    }
    public String getGOODSSORTGUID(){
        return this.GOODSSORTGUID;
    }
    public void setNUM(int NUM){
        this.NUM = NUM;
    }
    public int getNUM(){
        return this.NUM;
    }
    public static ShopCarEntity mapToEntity(LinkedTreeMap<String, String> map){
        ShopCarEntity entity=new ShopCarEntity();
        entity.setSPEC(map.get("SPEC"));
        entity.setNAME(map.get("NAME"));
        entity.setBUYCOUNT(Integer.parseInt(map.get("BUYCOUNT")));
        entity.setCODE(map.get("CODE"));
        entity.setCREATETIME(map.get("CREATEDTIME"));
        entity.setDETAILDESCRIPTION(map.get("DETAILDESCRIPTION"));
        entity.setGOODSCLASSNAME(map.get("GOODSCLASSNAME"));
        entity.setGOODSGUID(map.get("GOODSGUID"));
        entity.setGOODSPRICE(Double.parseDouble(map.get("GOODSPRICE")));
        entity.setGOODSSORTGUID(map.get("GOODSSORTGUID"));
        entity.setGOODURL(map.get("GOODURL"));
        entity.setUSERGUID(map.get("USERGUID"));
        if("".equals(map.get("NUM"))||map.get("NUM")==null){
            entity.setNUM(0);
        }else {
            entity.setNUM(Integer.parseInt(map.get("NUM")));
        }

        return entity;
    }

}
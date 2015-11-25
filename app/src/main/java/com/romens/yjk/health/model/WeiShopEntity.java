package com.romens.yjk.health.model;

import java.util.List;

/**
 * Created by AUSU on 2015/10/13.
 * 药品详情Entity
 */
public class WeiShopEntity {
    private String GUID;

    private String GOODSSORTGUID;

    private String NAME;

    private String USERPRICE;

    private String MARKETPRICE;

    private String SHORTDESCRIPTION;

    private String SPEC;

    private String PZWH;

    private String URL;

    private String CD;
    private String SHOPID;
    private String SHOPNAME;
    private String SHOPADDRESS;
    private String STORECOUNT;
    private String DETAILDESCRIPTION;
    private String manufacturerId;
    private String ASSESSCOUNT;
    private String TOTLESALEDCOUNT;

    public String getTOTLESALEDCOUNT() {
        return TOTLESALEDCOUNT;
    }

    public void setTOTLESALEDCOUNT(String TOTLESALEDCOUNT) {
        this.TOTLESALEDCOUNT = TOTLESALEDCOUNT;
    }

    public String getASSESSCOUNT() {
        return ASSESSCOUNT;
    }

    public void setASSESSCOUNT(String ASSESSCOUNT) {
        this.ASSESSCOUNT = ASSESSCOUNT;
    }

    public String getDETAILDESCRIPTION() {
        return DETAILDESCRIPTION;
    }

    public void setDETAILDESCRIPTION(String DETAILDESCRIPTION) {
        this.DETAILDESCRIPTION = DETAILDESCRIPTION;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getSTORECOUNT() {
        return STORECOUNT;
    }

    public void setSTORECOUNT(String STORECOUNT) {
        this.STORECOUNT = STORECOUNT;
    }

    public String getSHOPADDRESS() {
        return SHOPADDRESS;
    }

    public void setSHOPADDRESS(String SHOPADDRESS) {
        this.SHOPADDRESS = SHOPADDRESS;
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

    private List<GoodSpicsEntity> GOODSPICS ;
    public void setGUID(String GUID){
        this.GUID = GUID;
    }
    public String getGUID(){
        return this.GUID;
    }
    public void setGOODSSORTGUID(String GOODSSORTGUID){
        this.GOODSSORTGUID = GOODSSORTGUID;
    }
    public String getGOODSSORTGUID(){
        return this.GOODSSORTGUID;
    }
    public void setNAME(String NAME){
        this.NAME = NAME;
    }
    public String getNAME(){
        return this.NAME;
    }
    public void setUSERPRICE(String USERPRICE){
        this.USERPRICE = USERPRICE;
    }
    public String getUSERPRICE(){
        return this.USERPRICE;
    }
    public void setMARKETPRICE(String MARKETPRICE){
        this.MARKETPRICE = MARKETPRICE;
    }
    public String getMARKETPRICE(){
        return this.MARKETPRICE;
    }
    public void setSHORTDESCRIPTION(String SHORTDESCRIPTION){
        this.SHORTDESCRIPTION = SHORTDESCRIPTION;
    }
    public String getSHORTDESCRIPTION(){
        return this.SHORTDESCRIPTION;
    }
    public void setSPEC(String SPEC){
        this.SPEC = SPEC;
    }
    public String getSPEC(){
        return this.SPEC;
    }
    public void setPZWH(String PZWH){
        this.PZWH = PZWH;
    }
    public String getPZWH(){
        return this.PZWH;
    }
    public void setURL(String URL){
        this.URL = URL;
    }
    public String getURL(){
        return this.URL;
    }
    public void setCD(String CD){
        this.CD = CD;
    }
    public String getCD(){
        return this.CD;
    }
    public void setGOODSPICS(List<GoodSpicsEntity> GOODSPICS){
        this.GOODSPICS = GOODSPICS;
    }
    public List<GoodSpicsEntity> getGOODSPICS(){
        return this.GOODSPICS;
    }


}

package com.romens.yjk.health.model;

/**
 * Created by AUSU on 2015/9/8.
 */
public class WeiShopEntity {
//    NAME 商品名称
//    ，MARKETPRICE，会员价
//    ，USERPRICE用户价
//    ，DETAILDESCRIPTION商品详细介绍
//    ，SHORTDESCRIPTION 商品描述
//    ，SPEC商品规格
//    ，GOODSURL商品图片url
//    ，PZWH国药准字信息，
    private String GUID;

    private String GOODSSORTGUID;

    private String MARKETPRICE;

    private String USERPRICE;

    private String CODE;

    private String BARCODE;

    private String NAME;

    private String DETAILDESCRIPTION;

    private String SPEC;

    private String CD;

    private String PZWH;

    private String SHORTDESCRIPTION;
    private String GOODSURL;

    public String getGOODSURL() {
        return GOODSURL;
    }

    public void setGOODSURL(String GOODSURL) {
        this.GOODSURL = GOODSURL;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getGOODSSORTGUID() {
        return GOODSSORTGUID;
    }

    public void setGOODSSORTGUID(String GOODSSORTGUID) {
        this.GOODSSORTGUID = GOODSSORTGUID;
    }

    public String getMARKETPRICE() {
        return MARKETPRICE;
    }

    public void setMARKETPRICE(String MARKETPRICE) {
        this.MARKETPRICE = MARKETPRICE;
    }

    public String getUSERPRICE() {
        return USERPRICE;
    }

    public void setUSERPRICE(String USERPRICE) {
        this.USERPRICE = USERPRICE;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getBARCODE() {
        return BARCODE;
    }

    public void setBARCODE(String BARCODE) {
        this.BARCODE = BARCODE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
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

    public String getCD() {
        return CD;
    }

    public void setCD(String CD) {
        this.CD = CD;
    }

    public String getPZWH() {
        return PZWH;
    }

    public void setPZWH(String PZWH) {
        this.PZWH = PZWH;
    }

    public String getSHORTDESCRIPTION() {
        return SHORTDESCRIPTION;
    }

    public void setSHORTDESCRIPTION(String SHORTDESCRIPTION) {
        this.SHORTDESCRIPTION = SHORTDESCRIPTION;
    }
}

package com.romens.yjk.health.model;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by AUSU on 2015/9/28.
 */
public class GoodListEntity {
    private String MERCHANDISEID;                     //商品id
    private String MEDICINENAME;                      //药品名称
    private String MEDICINESPEC;                      //药品规格
    private String SHOPID;                           //药店id
    private String SHOPNAME;                         //药店名称
    private String PICBIG;                           //药品大图
    private String PICSMALL;                        //药品小图
    private String PRICE;                           //商品价格
    private String MEMBERPRICE;                    //会员价格
    private String ISRX;                            //是否是处方药
    private String ISCARE;                          //是否是医保产品

    public String getISRX() {
        return ISRX;
    }

    public void setISRX(String ISRX) {
        this.ISRX = ISRX;
    }

    public String getISCARE() {
        return ISCARE;
    }

    public void setISCARE(String ISCARE) {
        this.ISCARE = ISCARE;
    }

    public void setMERCHANDISEID(String MERCHANDISEID) {
        this.MERCHANDISEID = MERCHANDISEID;
    }

    public void setMEDICINENAME(String MEDICINENAME) {
        this.MEDICINENAME = MEDICINENAME;
    }

    public void setMEDICINESPEC(String MEDICINESPEC) {
        this.MEDICINESPEC = MEDICINESPEC;
    }

    public void setSHOPID(String SHOPID) {
        this.SHOPID = SHOPID;
    }

    public void setSHOPNAME(String SHOPNAME) {
        this.SHOPNAME = SHOPNAME;
    }

    public void setPICBIG(String PICBIG) {
        this.PICBIG = PICBIG;
    }

    public void setPICSMALL(String PICSMALL) {
        this.PICSMALL = PICSMALL;
    }

    public void setPRICE(String PRICE) {
        this.PRICE = PRICE;
    }

    public void setMEMBERPRICE(String MEMBERPRICE) {
        this.MEMBERPRICE = MEMBERPRICE;
    }

    public String getMERCHANDISEID() {
        return MERCHANDISEID;
    }

    public String getMEDICINENAME() {
        return MEDICINENAME;
    }

    public String getMEDICINESPEC() {
        return MEDICINESPEC;
    }

    public String getSHOPID() {
        return SHOPID;
    }

    public String getSHOPNAME() {
        return SHOPNAME;
    }

    public String getPICBIG() {
        return PICBIG;
    }

    public String getPICSMALL() {
        return PICSMALL;
    }

    public String getPRICE() {
        return PRICE;
    }

    public String getMEMBERPRICE() {
        return MEMBERPRICE;
    }

    public static GoodListEntity toEntity(JsonNode jsonNode) {
        GoodListEntity entity = new GoodListEntity();
        entity.setMEDICINENAME(jsonNode.get("MEDICINENAME").asText());
        entity.setMEDICINESPEC(jsonNode.get("MEDICINESPEC").asText());
        entity.setMERCHANDISEID(jsonNode.get("MERCHANDISEID").asText());
        entity.setPICBIG(jsonNode.get("PICBIG").asText());
        entity.setMEMBERPRICE(jsonNode.get("PRICE").asText());
        entity.setPICSMALL(jsonNode.get("PICSMALL").asText());
        entity.setPRICE(jsonNode.get("PRICE").asText());
        entity.setSHOPID(jsonNode.get("SHOPID").asText());
        entity.setSHOPNAME(jsonNode.get("SHOPNAME").asText());
        entity.setISRX(jsonNode.get("ISRX").asText());
        entity.setISCARE(jsonNode.get("ISCARE").asText());
        return entity;
    }
}

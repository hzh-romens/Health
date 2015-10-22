package com.romens.yjk.health.model;

/**
 * Created by AUSU on 2015/9/28.
 */
public class NearbyPharmacyEntity {

    private String GUID;
    private String  NAME;                     //药店名称
    private String  ADDRESS;                            //详细地址
    private String  PHONE;                               //联系电话1
    private String  PHONE1;                          //联系电话2
    private String  ZIPCODE;                                //邮政编码
    private String  EMAIL;                                 //电子邮件
    private String  FAXNUM;                             //传真
    private String  BRANCHIMAGEPATH;                         // 药店图片
    private String  lat;                               //纬度
    private String  lng;                           //经度
    private String  dis;                                      //距离
    private String  DISTRCITID;               //行政区划id
    private String  DELIVERYLIMIT;                            //配送范围

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

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getPHONE1() {
        return PHONE1;
    }

    public void setPHONE1(String PHONE1) {
        this.PHONE1 = PHONE1;
    }

    public String getZIPCODE() {
        return ZIPCODE;
    }

    public void setZIPCODE(String ZIPCODE) {
        this.ZIPCODE = ZIPCODE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getFAXNUM() {
        return FAXNUM;
    }

    public void setFAXNUM(String FAXNUM) {
        this.FAXNUM = FAXNUM;
    }

    public String getBRANCHIMAGEPATH() {
        return BRANCHIMAGEPATH;
    }

    public void setBRANCHIMAGEPATH(String BRANCHIMAGEPATH) {
        this.BRANCHIMAGEPATH = BRANCHIMAGEPATH;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public String getDISTRCITID() {
        return DISTRCITID;
    }

    public void setDISTRCITID(String DISTRCITID) {
        this.DISTRCITID = DISTRCITID;
    }

    public String getDELIVERYLIMIT() {
        return DELIVERYLIMIT;
    }

    public void setDELIVERYLIMIT(String DELIVERYLIMIT) {
        this.DELIVERYLIMIT = DELIVERYLIMIT;
    }
}

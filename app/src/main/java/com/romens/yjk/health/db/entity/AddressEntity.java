package com.romens.yjk.health.db.entity;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by anlc on 2015/9/19.
 * 收获地址的实体
 */
public class AddressEntity {
    private String ADDRESSID;
    private String RECEIVER;
    private String CONTACTPHONE;
    private String DISTRCTID;
    private String ADDRESS;
    private String ISDEFAULT;
    private String ADDRESSTYPE;
//    private String USERGUID;

//    public String getUSERGUID() {
//        return USERGUID;
//    }
//
//    public void setUSERGUID(String USERGUID) {
//        this.USERGUID = USERGUID;
//    }

    public String getADDRESSID() {
        return ADDRESSID;
    }

    public void setADDRESSID(String ADDRESSID) {
        this.ADDRESSID = ADDRESSID;
    }

    public String getRECEIVER() {
        return RECEIVER;
    }

    public void setRECEIVER(String RECEIVER) {
        this.RECEIVER = RECEIVER;
    }

    public String getCONTACTPHONE() {
        return CONTACTPHONE;
    }

    public void setCONTACTPHONE(String CONTACTPHONE) {
        this.CONTACTPHONE = CONTACTPHONE;
    }

    public String getDISTRCTID() {
        return DISTRCTID;
    }

    public void setDISTRCTID(String DISTRCTID) {
        this.DISTRCTID = DISTRCTID;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getISDEFAULT() {
        return ISDEFAULT;
    }

    public void setISDEFAULT(String ISDEFAULT) {
        this.ISDEFAULT = ISDEFAULT;
    }

    public String getADDRESSTYPE() {
        return ADDRESSTYPE;
    }

    public void setADDRESSTYPE(String ADDRESSTYPE) {
        this.ADDRESSTYPE = ADDRESSTYPE;
    }

    public static AddressEntity mapToEntity(LinkedTreeMap<String, String> item) {
        AddressEntity entity = new AddressEntity();
        entity.setISDEFAULT(item.get("ISDEFAULT"));
        entity.setADDRESS(item.get("ADDRESS"));
        entity.setADDRESSID(item.get("ADDRESSID"));
        entity.setADDRESSTYPE(item.get("ADDRESSTYPE"));
        entity.setCONTACTPHONE(item.get("CONTACTPHONE"));
        entity.setDISTRCTID(item.get("DISTRCTID"));
        entity.setRECEIVER(item.get("RECEIVER"));
//        entity.setUSERGUID(item.get("USERGUID"));
        return entity;
    }
}

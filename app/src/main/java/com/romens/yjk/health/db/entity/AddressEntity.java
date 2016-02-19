package com.romens.yjk.health.db.entity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

/**
 * Created by anlc on 2015/9/19.
 * 收获地址的实体
 */
public class AddressEntity implements Serializable {
    private String ADDRESSID;
    private String RECEIVER;
    private String CONTACTPHONE;
    //    private String DISTRCTID;
    private String PROVINCE;
    private String CITY;
    private String REGION;
    private String ADDRESS;
    private String ISDEFAULT;
    private String ADDRESSTYPE;

    private String PROVINCENAME;
    private String CITYNAME;
    private String REGIONNAME;
//    private String USERGUID;

//    public String getUSERGUID() {
//        return USERGUID;
//    }
//
//    public void setUSERGUID(String USERGUID) {
//        this.USERGUID = USERGUID;
//    }


    public String getPROVINCENAME() {
        return PROVINCENAME;
    }

    public void setPROVINCENAME(String PROVINCENAME) {
        this.PROVINCENAME = PROVINCENAME;
    }

    public String getCITYNAME() {
        return CITYNAME;
    }

    public void setCITYNAME(String CITYNAME) {
        this.CITYNAME = CITYNAME;
    }

    public String getREGIONNAME() {
        return REGIONNAME;
    }

    public void setREGIONNAME(String REGIONNAME) {
        this.REGIONNAME = REGIONNAME;
    }

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

    public String getPROVINCE() {
        return PROVINCE;
    }

    public void setPROVINCE(String PROVINCE) {
        this.PROVINCE = PROVINCE;
    }

    public String getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    public String getREGION() {
        return REGION;
    }

    public void setREGION(String REGION) {
        this.REGION = REGION;
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
//        entity.setDISTRCTID(item.get("DISTRCTID"));
        entity.setPROVINCE(item.get("PROVINCE"));
        entity.setCITY(item.get("CITY"));
        entity.setREGION(item.get("REGION"));
        entity.setRECEIVER(item.get("RECEIVER"));
//        entity.setUSERGUID(item.get("USERGUID"));
        entity.setPROVINCENAME(item.get("PROVINCENAME"));
        entity.setCITYNAME(item.get("CITYNAME"));
        entity.setREGIONNAME(item.get("REGIONNAME"));

        return entity;
    }
}

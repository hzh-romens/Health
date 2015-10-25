package com.romens.yjk.health.model;

/**
 * Created by AUSU on 2015/10/18.
 */
public class NearByOnSaleEntity {
    private String ID;

    private String SHOPNAME;

    private String ADDRESS;

    private String SHOPNO;

    private String LAT;

    private String LON;

    private String DISTANCE;

    private String PRICE;

    private String TOTLESALEDCOUNT;

    public String getTOTLESALEDCOUNT() {
        return TOTLESALEDCOUNT;
    }

    public void setTOTLESALEDCOUNT(String TOTLESALEDCOUNT) {
        this.TOTLESALEDCOUNT = TOTLESALEDCOUNT;
    }

    public String getPRICE() {
        return PRICE;
    }

    public void setPRICE(String PRICE) {
        this.PRICE = PRICE;
    }

    public void setID(String ID){
        this.ID = ID;
    }
    public String getID(){
        return this.ID;
    }
    public void setSHOPNAME(String SHOPNAME){
        this.SHOPNAME = SHOPNAME;
    }
    public String getSHOPNAME(){
        return this.SHOPNAME;
    }
    public void setADDRESS(String ADDRESS){
        this.ADDRESS = ADDRESS;
    }
    public String getADDRESS(){
        return this.ADDRESS;
    }
    public void setSHOPNO(String SHOPNO){
        this.SHOPNO = SHOPNO;
    }
    public String getSHOPNO(){
        return this.SHOPNO;
    }
    public void setLAT(String LAT){
        this.LAT = LAT;
    }
    public String getLAT(){
        return this.LAT;
    }
    public void setLON(String LON){
        this.LON = LON;
    }
    public String getLON(){
        return this.LON;
    }
    public void setDISTANCE(String DISTANCE){
        this.DISTANCE = DISTANCE;
    }
    public String getDISTANCE(){
        return this.DISTANCE;
    }
}

package com.romens.yjk.health.model;

/**
 * Created by HZH on 2015/12/9.
 */
public class CommentEntity {
    private String ID;
    private String MEMBERID;
    private String ISAPPEND;
    private String ADVICE;
    private String QUALITYLEVEL;
    private String DILEVERYLEVEL;
    private String ALLCOUNT;
    private String ASSESSDATE;

    public String getASSESSDATE() {
        return ASSESSDATE;
    }

    public void setASSESSDATE(String ASSESSDATE) {
        this.ASSESSDATE = ASSESSDATE;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMEMBERID() {
        return MEMBERID;
    }

    public void setMEMBERID(String MEMBERID) {
        this.MEMBERID = MEMBERID;
    }

    public String getISAPPEND() {
        return ISAPPEND;
    }

    public void setISAPPEND(String ISAPPEND) {
        this.ISAPPEND = ISAPPEND;
    }

    public String getADVICE() {
        return ADVICE;
    }

    public void setADVICE(String ADVICE) {
        this.ADVICE = ADVICE;
    }

    public String getQUALITYLEVEL() {
        return QUALITYLEVEL;
    }

    public void setQUALITYLEVEL(String QUALITYLEVEL) {
        this.QUALITYLEVEL = QUALITYLEVEL;
    }

    public String getDILEVERYLEVEL() {
        return DILEVERYLEVEL;
    }

    public void setDILEVERYLEVEL(String DILEVERYLEVEL) {
        this.DILEVERYLEVEL = DILEVERYLEVEL;
    }

    public String getALLCOUNT() {
        return ALLCOUNT;
    }

    public void setALLCOUNT(String ALLCOUNT) {
        this.ALLCOUNT = ALLCOUNT;
    }
}


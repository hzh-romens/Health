package com.romens.yjk.health.model;

import java.io.Serializable;

/**
 * Created by AUSU on 2015/10/25.
 */
public class PersonalEntity implements Serializable {
    private String PERSONNAME;  //姓名
    private String GENDER;         //性别： 1为男 2位女
    private String JOB;             //职业
    private String BIRTHDAY;        //出生年月
    private String HASINHERITED;    //有无遗传病
    private String HASSERIOUS;      //有无大病史
    private String HASGUOMIN;       //有无过敏史
    private String FOODHOBBY;       //饮食偏好
    private String SLEEPHOBBY;      //作息习惯
    private String OTHER;          //其他

    public String getPERSONNAME() {
        return PERSONNAME;
    }

    public void setPERSONNAME(String PERSONNAME) {
        this.PERSONNAME = PERSONNAME;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getJOB() {
        return JOB;
    }

    public void setJOB(String JOB) {
        this.JOB = JOB;
    }

    public String getBIRTHDAY() {
        return BIRTHDAY;
    }

    public void setBIRTHDAY(String BIRTHDAY) {
        this.BIRTHDAY = BIRTHDAY;
    }

    public String getHASINHERITED() {
        return HASINHERITED;
    }

    public void setHASINHERITED(String HASINHERITED) {
        this.HASINHERITED = HASINHERITED;
    }

    public String getHASSERIOUS() {
        return HASSERIOUS;
    }

    public void setHASSERIOUS(String HASSERIOUS) {
        this.HASSERIOUS = HASSERIOUS;
    }

    public String getHASGUOMIN() {
        return HASGUOMIN;
    }

    public void setHASGUOMIN(String HASGUOMIN) {
        this.HASGUOMIN = HASGUOMIN;
    }

    public String getFOODHOBBY() {
        return FOODHOBBY;
    }

    public void setFOODHOBBY(String FOODHOBBY) {
        this.FOODHOBBY = FOODHOBBY;
    }

    public String getSLEEPHOBBY() {
        return SLEEPHOBBY;
    }

    public void setSLEEPHOBBY(String SLEEPHOBBY) {
        this.SLEEPHOBBY = SLEEPHOBBY;
    }

    public String getOTHER() {
        return OTHER;
    }

    public void setOTHER(String OTHER) {
        this.OTHER = OTHER;
    }
}

package com.romens.yjk.health.model;

/**
 * Created by HZH on 2015/10/24.
 */
public class PersonalInformationEntity {
    private String titleName;
    private int type;

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

   public PersonalInformationEntity(String titleName,int type){
       this.titleName=titleName;
       this.type=type;
   }
}

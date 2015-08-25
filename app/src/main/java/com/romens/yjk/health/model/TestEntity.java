package com.romens.yjk.health.model;

/**
 * Created by romens007 on 2015/8/19.
 * 用于测试的bean
 */
public class TestEntity {
    private int Type;
    private String json;
    private String imageUrl;
    private String infor;

    public String getInfor() {
        return infor;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
    public TestEntity(int type,String json,String imageUrl,String infor){
        this.Type=type;
        this.json=json;
        this.imageUrl=imageUrl;
        this.infor=infor;
    }
}

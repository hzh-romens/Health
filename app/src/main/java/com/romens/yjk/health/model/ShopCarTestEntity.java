package com.romens.yjk.health.model;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.romens.yjk.health.db.DBHelper;

/**
 * Created by romens007 on 2015/8/19.
 * 用于测试的bean
 */
public class ShopCarTestEntity {
    public Long id;
    public String code;
    public int type;
    public String name;
    public String imageUrl;
    public String infor;
    public String check;
    public Double price;
    public int num;
    //用于更新数据
    public int createdTime;
    public int updatedTime;


    public int getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(int updatedTime) {
        this.updatedTime = updatedTime;
    }

    public int getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(int createdTime) {
        this.createdTime = createdTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

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



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public ShopCarTestEntity(){
        super();
    }

    public ShopCarTestEntity(String code,int type, String name, String imageUrl, String infor, String check, Double price, int num) {
        super();
        this.type = type;
        this.name = name;
        this.imageUrl = imageUrl;
        this.infor = infor;
        this.check = check;
        this.price = price;
        this.num = num;
    }
    public static ShopCarTestEntity mapToEntity(LinkedTreeMap<String, String> map) {
        ShopCarTestEntity entity = new ShopCarTestEntity();
        entity.code = DBHelper.toDBString(map.get("CODE"));
        String type = map.get("TYPE");
        entity.type =  Integer.valueOf(TextUtils.isEmpty(type) ? "0" : type);
        entity.name = DBHelper.toDBString(map.get("NAME"));
        entity.imageUrl = DBHelper.toDBString(map.get("ZIPCODE"));
        entity.infor= DBHelper.toDBString(map.get("INFOR"));
        entity.check = DBHelper.toDBString(map.get("check"));
        String priceStr = map.get("PRICE");
        entity.price=Double.parseDouble(priceStr);
        String numStr = map.get("NUM");
        entity.num=Integer.valueOf(TextUtils.isEmpty(numStr) ? "0" : numStr);
//        String createdTimeStr = map.get("CREATED_TIME");
//        entity.createdTime=Integer.valueOf(TextUtils.isEmpty(createdTimeStr) ? "0" : createdTimeStr);
//        String updatedTimeStr = map.get("UPDATED_TIME");
//        entity.updatedTime=Integer.valueOf(TextUtils.isEmpty(updatedTimeStr) ? "0" : updatedTimeStr);
        return entity;
    }
}

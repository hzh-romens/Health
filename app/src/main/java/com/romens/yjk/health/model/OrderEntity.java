package com.romens.yjk.health.model;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by AUSU on 2015/9/12.
 */
public class OrderEntity {
    private String imageUrl;
    private String name;
    private String comment;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static OrderEntity mapToEntity(LinkedTreeMap<String, String> map){
        OrderEntity orderEntity=new OrderEntity();
        return orderEntity;
    }
}

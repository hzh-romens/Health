package com.romens.yjk.health.model;

/**
 * Created by romens007 on 2015/8/20.
 */
public class AboutTestEntity {
    private String ImageUrl;
    private String name;
    private String price;
    public AboutTestEntity(String imageUrl,String name,String price){
        this.ImageUrl=imageUrl;
        this.name=name;
        this.price=price;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

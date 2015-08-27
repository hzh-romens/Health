package com.romens.yjk.health.db.entity;

/**
 * Created by anlc on 2015/8/20.
 */
public class ControlAddressEntity {
    private String name;
    private String tel;
    private String address;
    private boolean isDefault;

    public ControlAddressEntity() {
    }

    public ControlAddressEntity(String name, String tel, String address, boolean isDefault) {
        this.name = name;
        this.tel = tel;
        this.address = address;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
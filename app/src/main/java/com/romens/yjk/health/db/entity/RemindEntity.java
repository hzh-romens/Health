package com.romens.yjk.health.db.entity;

import java.io.Serializable;

/**
 * Created by anlc on 2015/8/23.
 */
public class RemindEntity implements Serializable {
    private int userIcon;
    private String user;
    private String drug;
    private String count;

    public int getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(int userIcon) {
        this.userIcon = userIcon;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDrug() {
        return drug;
    }

    public void setDrug(String drug) {
        this.drug = drug;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}

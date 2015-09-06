package com.romens.yjk.health.db.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by anlc on 2015/8/23.
 */
public class RemindEntity implements Serializable {
    private long id;
    private int userIcon;
    private String user;
    private String drug;
    private String count;
    private String startDate;

    private String firstTime;
    private String secondtime;
    private String threeTime;
    private String fourTime;
    private String fiveTime;
    private int times = 1;

    private int isRemind = 0;

    public int getIsRemind() {
        return isRemind;
    }

    public void setIsRemind(int isRemind) {
        this.isRemind = isRemind;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getSecondtime() {
        return secondtime;
    }

    public void setSecondtime(String secondtime) {
        this.secondtime = secondtime;
    }

    public String getThreeTime() {
        return threeTime;
    }

    public void setThreeTime(String threeTime) {
        this.threeTime = threeTime;
    }

    public String getFourTime() {
        return fourTime;
    }

    public void setFourTime(String fourTime) {
        this.fourTime = fourTime;
    }

    public String getFiveTime() {
        return fiveTime;
    }

    public void setFiveTime(String fiveTime) {
        this.fiveTime = fiveTime;
    }

    public RemindEntity() {
    }

    ;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

package com.romens.yjk.health.db.entity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.romens.yjk.health.service.RemindAlarmServiceBroadcastReceiver;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by anlc on 2015/8/23.
 * 用药提醒中保存所有信息的实体
 */
public class RemindEntity implements Serializable {
    private long id;
    private String user;
    private String drug;
    private int intervalDay;
    private String startDate;

    private String firstTime;
    private String secondtime;
    private String threeTime;
    private String fourTime;
    private String fiveTime;
    private int timesInDay = 1;

    private String dosage;
    private String remark;

    private int isRemind = 0;
    private boolean isAdd;

    public RemindEntity() {
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsRemind() {
        return isRemind;
    }

    public void setIsRemind(int isRemind) {
        this.isRemind = isRemind;
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

    public int getIntervalDay() {
        return intervalDay;
    }

    public void setIntervalDay(int intervalDay) {
        this.intervalDay = intervalDay;
    }

    public int getTimesInDay() {
        return timesInDay;
    }

    public void setTimesInDay(int timesInDay) {
        this.timesInDay = timesInDay;
    }
}

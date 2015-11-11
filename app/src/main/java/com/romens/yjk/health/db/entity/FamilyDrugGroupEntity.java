package com.romens.yjk.health.db.entity;

/**
 * Created by anlc on 2015/11/9.
 */
public class FamilyDrugGroupEntity {
    private long id;
    private String drugName;
    private String remark;
    private String drugGuid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDrugGuid() {
        return drugGuid;
    }

    public void setDrugGuid(String drugGuid) {
        this.drugGuid = drugGuid;
    }
}

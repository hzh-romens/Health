package com.romens.yjk.health.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by anlc on 2015/10/22.
 */
public class EvaluateDatailsEntity {
    private String advice;//评价内容
    private String isAppend;//是否追加
    private String assessData;//评价日期
    private String dileveryStar;//速度评价
    private String qualityStar;//质量评价

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getIsAppend() {
        return isAppend;
    }

    public void setIsAppend(String isAppend) {
        this.isAppend = isAppend;
    }

    public String getAssessData() {
        return assessData;
    }

    public void setAssessData(String assessData) {
        this.assessData = assessData;
    }

    public String getDileveryStar() {
        return dileveryStar;
    }

    public void setDileveryStar(String dileveryStar) {
        this.dileveryStar = dileveryStar;
    }

    public String getQualityStar() {
        return qualityStar;
    }

    public void setQualityStar(String qualityStar) {
        this.qualityStar = qualityStar;
    }

    public static EvaluateDatailsEntity mapToEntity(LinkedTreeMap<String, String> item) {
        EvaluateDatailsEntity entity = new EvaluateDatailsEntity();
        entity.setAdvice(item.get("ADVICE"));
        entity.setIsAppend(item.get("ISAPPEND"));
        entity.setAssessData(item.get("ASSESSDATE"));
        entity.setDileveryStar(item.get("DILEVERYSTAR"));
        entity.setQualityStar(item.get("QUALITYSTAR"));
        Log.e("tag", "-->" + new Gson().toJson(entity).toString());
        return entity;
    }
}

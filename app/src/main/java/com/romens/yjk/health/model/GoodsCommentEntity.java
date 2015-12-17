package com.romens.yjk.health.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siery on 15/12/16.
 */
public class GoodsCommentEntity {
    public final String id;
    public final String memberId;
    public final String isAppEnd;
    public final String advice;
    public final int qualityLevel;
    public final int dileveryLevel;
    public final int allCount;
    public final String assessDate;

    /**
     * [{"ID":"5655636dbc75d",
     * "MEMBERID":"56386405b5acc1446536197",
     * "ASSESSDATE":"2015-11-25",
     * "ISAPPEND":"0",
     * "ADVICE":"cvhvv",
     * "QUALITYLEVEL":"5",
     * "DILEVERYLEVEL":"5",
     * "ALLCOUNT":"1"}]
     **/
    public GoodsCommentEntity(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("ID");
        memberId = jsonObject.getString("MEMBERID");
        isAppEnd = jsonObject.getString("ISAPPEND");
        advice = jsonObject.getString("ADVICE");
        qualityLevel = jsonObject.getInt("QUALITYLEVEL");
        dileveryLevel = jsonObject.getInt("DILEVERYLEVEL");
        allCount = jsonObject.getInt("ALLCOUNT");
        assessDate = jsonObject.getString("ASSESSDATE");
    }
}

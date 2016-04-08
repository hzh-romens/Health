package com.romens.yjk.health.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siery on 15/12/16.
 */
public class GoodsCommentEntity {
    public final String id;
    public final String memberId;
    private final String memberName;
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
        memberName = jsonObject.has("MEMBERNAME") ? jsonObject.getString("MEMBERNAME") : "***";
        isAppEnd = jsonObject.getString("ISAPPEND");
        advice = jsonObject.getString("ADVICE");
        qualityLevel = jsonObject.getInt("QUALITYLEVEL");
        dileveryLevel = jsonObject.getInt("DILEVERYLEVEL");
        allCount = jsonObject.getInt("ALLCOUNT");
        assessDate = jsonObject.getString("ASSESSDATE");
    }

    public String getMemberName() {
        StringBuilder member = new StringBuilder();
        if (!TextUtils.isEmpty(memberName)) {
            final int length = memberName.length();
            if (length > 2) {
                member.append(memberName.substring(0, 2));
            } else if (length > 1) {
                member.append(memberName.substring(0, 1));
            }
            member.append("***");
        } else {
            member.append("***");
        }
        return member.toString();
    }
}

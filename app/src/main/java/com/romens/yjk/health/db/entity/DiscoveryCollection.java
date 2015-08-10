package com.romens.yjk.health.db.entity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by siery on 15/5/22.
 */
public class DiscoveryCollection {
    public static boolean onDiscoveryItemAction(Context context, DiscoveryEntity entity) {
        final String key = entity.getKey();
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        if (key.startsWith("intent")) {
            String action = entity.getValue();
            if (!TextUtils.isEmpty(action)) {
                context.startActivity(new Intent(action));
                return true;
            }
        }
        return false;
    }

    public static final class NearbyPharmacy {
        public static final String key = "intent_nearby_pharmacy";
        public static final String value = "com.romens.rhealth.NEARBYPHARMACY";
        public static final String iconRes = "assets://icon/ic_discovery_map.png";
        public static final String iconUrl = "";
        public static final String name = "附近药店";
        public static final int isCover = 1;
        public static final int sortIndex=0;
        public static final int primaryColor=0xFF8BC34A;
    }

    public static final class CommonDisease {
        public static final String key = "intent_common_disease";
        public static final String value = "com.romens.rhealth.COMMONDISEASE";
        public static final String iconRes = "assets://icon/ic_discovery_map.png";
        public static final String iconUrl = "";
        public static final String name = "常见疾病";
    }

    public static final class PharmicCounseling {
        public static final String key = "intent_pharmic_counseling";
        public static final String value = "com.romens.rhealth.PHARMICCOUNSELING";
        public static final String iconRes = "assets://icon/ic_discovery_map.png";
        public static final String iconUrl = "";
        public static final String name = "用药咨询";
    }

    public static final class MedicationReminders {
        public static final String key = "intent_medication_reminders";
        public static final String value = "com.romens.rhealth.MEDICATIONREMINDERS";
        public static final String iconRes = "assets://icon/ic_discovery_map.png";
        public static final String iconUrl = "";
        public static final String name = "服药提醒";
    }

    public static final class OrderSheet {
        public static final String key = "intent_order_sheet";
        public static final String value = "com.romens.rhealth.ORDERSHEET";
        public static final String iconRes = "assets://icon/ic_discovery_map.png";
        public static final String iconUrl = "";
        public static final String name = "预购单";
    }

    public static final class InformationNews {
        public static final String key = "intent_information_new";
        public static final String value = "com.romens.rhealth.INFORMATION_NEWS";
        public static final String iconRes = "assets://icon/ic_discovery_map.png";
        public static final String iconUrl = "";
        public static final String name = "最新资讯";
        public static final int isCover = 0;
        public static final int sortIndex=1;
        public static final int primaryColor=0xFFFF8A65;
    }

}

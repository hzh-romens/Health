package com.romens.yjk.health.db.entity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.romens.yjk.health.R;

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
            if (!action.equals(PharmicCounseling.value)) {
                if (!TextUtils.isEmpty(action)) {
                    context.startActivity(new Intent(action));
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean onFocusItemAction(Context context, String id, String action) {
        if (TextUtils.isEmpty(action)) {
            return false;
        }

        Intent intent = new Intent(action);
        if (TextUtils.equals("YBZQ", id)) {
            intent.putExtra("title", "医保专区");
        }
        context.startActivity(intent);

//        if (btnString.equals(FindDrugWithScanner.name)) {
//            String action = FindDrugWithScanner.value;
//            if (!TextUtils.isEmpty(action)) {
//                context.startActivity(new Intent(action));
//            }
//        } else if (btnString.equals(NearbyPharmacy.name)) {
//            String action = NearbyPharmacy.value;
//            if (!TextUtils.isEmpty(action)) {
//                context.startActivity(new Intent(action));
//            }
//        } else if (btnString.equals(MedicationReminders.name)) {
//            String action = MedicationReminders.value;
//            if (!TextUtils.isEmpty(action)) {
//                context.startActivity(new Intent(action));
//            }
//        } else if (btnString.equals(InformationNews.name)) {
//            String action = InformationNews.value;
//            if (!TextUtils.isEmpty(action)) {
//                context.startActivity(new Intent(action));
//            }
//        }
        return false;
    }

    public static final class NearbyPharmacy {
        public static final String key = "intent_nearby_pharmacy";
        public static final String value = "com.romens.yjk.health.NEARBYPHARMACY";
        public static final int iconRes = R.drawable.attach_location_states;
        public static final String iconUrl = "";
        public static final String name = "附近药店";
        public static final int isCover = 1;
        public static final int sortIndex = 0;
        public static final int primaryColor = 0xFF8BC34A;
    }

    public static final class CommonDisease {
        public static final String key = "intent_common_disease";
        public static final String value = "com.romens.rhealth.COMMONDISEASE";
        public static final int iconRes = R.drawable.attach_sort_states;
        public static final String iconUrl = "";
        public static final String name = "常见疾病";
    }

    public static final class PharmicCounseling {
        public static final String key = "intent_pharmic_counseling";
        public static final String value = "com.romens.rhealth.PHARMICCOUNSELING";
        public static final int iconRes = R.drawable.attach_counsel_states;
        public static final String iconUrl = "";
        public static final String name = "用药咨询";
        public static final int isCover = 0;
        public static final int sortIndex = 4;
        public static final int primaryColor = 0xFFA1887F;
    }

    public static final class MedicationReminders {
        public static final String key = "intent_medication_reminders";
        public static final String value = "com.romens.rhealth.MEDICATIONREMINDERS";
        public static final int iconRes = R.drawable.attach_remind_states;
        public static final String iconUrl = "";
        public static final String name = "用药提醒";
        public static final int isCover = 0;
        public static final int sortIndex = 5;
        public static final int primaryColor = 0xFFA1887F;
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
        public static final int iconRes = R.drawable.attach_new_states;
        public static final String iconUrl = "";
        public static final String name = "最新资讯";
        public static final int isCover = 0;
        public static final int sortIndex = 1;
        public static final int primaryColor = 0xFFFF8A65;
    }

    public static final class FindDrugWithScanner {
        public static final String key = "intent_find_drug_with_scanner";
        public static final String value = "com.romens.yjk.health.QRSCANNER";
        public static final int iconRes = R.drawable.attach_sort_states;
        public static final String iconUrl = "";
        public static final String name = "扫码识药";
        public static final int isCover = 0;
        public static final int sortIndex = 2;
        public static final int primaryColor = 0xFFA1887F;
    }

}

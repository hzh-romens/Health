package com.romens.yjk.health.db.entity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.HealthNewsActivity;
import com.romens.yjk.health.ui.LocationActivity;
import com.romens.yjk.health.ui.RemindActivity;
import com.romens.yjk.health.ui.activity.ScannerNewActivity;
import com.romens.yjk.health.ui.im.HealthConsultActivity;

/**
 * Created by siery on 15/5/22.
 */
public class DiscoveryCollection {
    public static boolean onDiscoveryItemAction(Context context, DiscoveryEntity entity) {
        final String key = entity.getKey();
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        if (key.startsWith("http")) {

        } else {
            String value = entity.getValue();
            if (!TextUtils.isEmpty(value)) {
                onFocusItemAction(context, entity.getKey(), value);
                return true;
            }
        }
        return false;
    }

    public static boolean onFocusItemAction(Context context, String id, String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        Intent intent = null;
        if (TextUtils.equals(PharmicCounseling.value, className)) {
            intent = new Intent(context, HealthConsultActivity.class);
        } else if (TextUtils.equals(IHealth.value, className)) {
            Toast.makeText(context, "正在开发，尽请期待!", Toast.LENGTH_SHORT).show();
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName(context, className);
            intent.setComponent(component);
            if (TextUtils.equals("YBZQ", id)) {
                intent.putExtra("title", "医保专区");
            }
        }
        if (intent != null) {
            context.startActivity(intent);
        }


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
        public static final String value = LocationActivity.class.getName();
        public static final int iconRes = R.drawable.attach_location_states;
        public static final String iconUrl = "";
        public static final String name = "附近药店";
        public static final int isCover = 1;
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
        public static final String value = "IM_Pharmic_Counseling";
        public static final int iconRes = R.drawable.attach_counsel_states;
        public static final String iconUrl = "";
        public static final String name = "用药咨询";
        public static final int isCover = 0;
        public static final int primaryColor = 0xFFA1887F;
    }

    public static final class MedicationReminders {
        public static final String key = "intent_medication_reminders";
        public static final String value = RemindActivity.class.getName();
        public static final int iconRes = R.drawable.attach_remind_states;
        public static final String iconUrl = "";
        public static final String name = "用药提醒";
        public static final int isCover = 0;
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
        public static final String value = HealthNewsActivity.class.getName();
        public static final int iconRes = R.drawable.attach_new_states;
        public static final String iconUrl = "";
        public static final String name = "最新资讯";
        public static final int isCover = 0;
        public static final int primaryColor = 0xFFFF8A65;
    }

    public static final class FindDrugWithScanner {
        public static final String key = "intent_find_drug_with_scanner";
        public static final String value = ScannerNewActivity.class.getName();
        public static final int iconRes = R.drawable.attach_sort_states;
        public static final String iconUrl = "";
        public static final String name = "扫码识药";
        public static final int isCover = 0;
        public static final int primaryColor = 0xFFA1887F;
    }

    public static final class IHealth {
        public static final String key = "intent_person_health";
        public static final String value = "com.romens.rhealth.ihealth";
        public static final int iconRes = R.drawable.ic_health;
        public static final String iconUrl = "";
        public static final String name = "个人健康";
        public static final int isCover = 0;
        public static final int primaryColor = 0xFFA1887F;
    }
}

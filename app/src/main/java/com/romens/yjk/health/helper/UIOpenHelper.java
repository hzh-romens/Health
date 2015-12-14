package com.romens.yjk.health.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.romens.yjk.health.ui.MedicinalDetailActivity;
import com.romens.yjk.health.ui.activity.SearchActivity;

/**
 * Created by siery on 15/12/14.
 */
public class UIOpenHelper {
    public static void openQRScanActivity(Activity context, int requestCode) {
        Intent intent = new Intent("com.romens.yjk.health.QRSCANNER");
        context.startActivityForResult(intent, requestCode);
    }

    public static void openDrugDetailActivity(Context context, String drugId) {
        Intent intent = new Intent(context, MedicinalDetailActivity.class);
        intent.putExtra("guid", drugId);
        context.startActivity(intent);
    }

    public static void openSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }
}

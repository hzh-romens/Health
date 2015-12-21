package com.romens.yjk.health.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.service.MedicineFavoriteService;
import com.romens.yjk.health.ui.ControlAddressActivity;
import com.romens.yjk.health.ui.NewShoppingAddressActivity;
import com.romens.yjk.health.ui.ShopCarActivity;
import com.romens.yjk.health.ui.activity.ADWebActivity;
import com.romens.yjk.health.ui.activity.FavoritesActivity;
import com.romens.yjk.health.ui.activity.GoodsDetailActivity;
import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.activity.SearchActivity;
import com.romens.yjk.health.ui.activity.UserLabelsActivity;

/**
 * Created by siery on 15/12/14.
 */
public class UIOpenHelper {

    public static void openLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void openLoginActivity(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void openLoginActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openQRScanActivity(Activity context, int requestCode) {
        Intent intent = new Intent("com.romens.yjk.health.QRSCANNER");
        context.startActivityForResult(intent, requestCode);
    }

//    public static void openDrugDetailActivity(Context context, String drugId) {
//        Intent intent = new Intent(context, MedicinalDetailActivity.class);
//        intent.putExtra("guid", drugId);
//        context.startActivity(intent);
//    }

    public static void openShoppingCartActivity(Context context) {
        Intent intent = new Intent(context, ShopCarActivity.class);
        context.startActivity(intent);
    }

    public static void openShoppingCartActivityForCheckLogin(Activity context, int requestCode) {
        if (UserConfig.isClientLogined()) {
            openShoppingCartActivity(context);
        } else {
            openLoginActivity(context, requestCode);
        }
    }

    public static void openSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void openMedicineActivity(Context context, String guid) {
        openMedicineActivity(context, guid, true);
    }

    public static void openMedicineActivity(Context context, String guid, boolean checkNearStore) {
        Intent intent = new Intent(context, GoodsDetailActivity.class);
        Bundle arguments = new Bundle();
        arguments.putString(GoodsDetailActivity.ARGUMENTS_KEY_ID, guid);
        arguments.putBoolean(GoodsDetailActivity.ARGUMENTS_KEY_CHECK_NEAR_STORE, checkNearStore);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }

    public static void openMedicineManualActivity(Context context, String medicineId, String medicineName) {
        String host = FacadeConfig.getUrl();
        String targetUrl = host + "MedicineDetail?flag=0&id={0}&user={1}";
        //"http://115.28.244.190/index.php/MedicineDetail?flag=0&id={0}&user={1}";
        targetUrl = targetUrl.replace("{0}", medicineId);
        if (UserConfig.isClientLogined()) {
            targetUrl = targetUrl.replace("{0}", UserConfig.getClientUserId());
        } else {
            targetUrl = targetUrl.replace("{1}", "");
        }
        Intent intent = new Intent(context, ADWebActivity.class);
        Bundle arguments = new Bundle();
        arguments.putString(ADWebActivity.ARGUMENTS_KEY_TITLE, medicineName);
        arguments.putString(ADWebActivity.ARGUMENTS_KEY_TARGET_URL, targetUrl);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }

    public static void openFavoritesActivity(Context context) {
        Intent intent = new Intent(context, FavoritesActivity.class);
        context.startActivity(intent);
    }

    public static void syncFavorites(Activity context) {
        if (UserConfig.isClientLogined()) {
            Intent service = new Intent(context, MedicineFavoriteService.class);
            service.putExtra(MedicineFavoriteService.ARGUMENTS_KEY_ACTION, MedicineFavoriteService.ACTION_SYNC);
            context.startService(service);
        }
    }

    public static void openAddShippingAddress(Activity context, int requestCode) {
        Intent intent = new Intent(context, NewShoppingAddressActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void openShippingAddress(Activity context, int requestCode) {
        Intent intent = new Intent(context, ControlAddressActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void openUserLabelsActivity(Context context) {
        Intent intent = new Intent(context, UserLabelsActivity.class);
        context.startActivity(intent);
    }

}

package com.romens.yjk.health.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.romens.yjk.health.R;
import com.romens.yjk.health.common.GoodsFlag;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.entity.AddressEntity;
import com.romens.yjk.health.pay.Pay;
import com.romens.yjk.health.service.MedicineFavoriteService;
import com.romens.yjk.health.ui.AccountSettingActivity;
import com.romens.yjk.health.ui.AddNewRemindActivity;
import com.romens.yjk.health.ui.AddRemindTimesActivity;
import com.romens.yjk.health.ui.ControlAddressActivity;
import com.romens.yjk.health.ui.CuoponActivity;
import com.romens.yjk.health.ui.EditActivity;
import com.romens.yjk.health.ui.FamilyDrugGroupActivity;
import com.romens.yjk.health.ui.FamilyMemberActivity;
import com.romens.yjk.health.ui.HistoryActivity;
import com.romens.yjk.health.ui.HomeActivity;
import com.romens.yjk.health.ui.LocationActivity;
import com.romens.yjk.health.ui.MemberActivity;
import com.romens.yjk.health.ui.MyOrderActivity;
import com.romens.yjk.health.ui.NewShoppingAddressActivity;
import com.romens.yjk.health.ui.OrderDetailActivity;
import com.romens.yjk.health.ui.ShopListActivity;
import com.romens.yjk.health.ui.activity.ADWebActivity;
import com.romens.yjk.health.ui.activity.ChangePasswordActivity;
import com.romens.yjk.health.ui.activity.FavoritesActivity;
import com.romens.yjk.health.ui.activity.GoodsDetailActivity;
import com.romens.yjk.health.ui.activity.LoginActivity;
import com.romens.yjk.health.ui.activity.MedicineGroupActivity;
import com.romens.yjk.health.ui.activity.SearchActivity;
import com.romens.yjk.health.ui.activity.ShoppingCartActivity;
import com.romens.yjk.health.ui.activity.UserLabelsActivity;
import com.romens.yjk.health.ui.fragment.HomeHealthNewFragment;
import com.yunuo.pay.PayActivity;

import java.io.Serializable;
import java.util.ArrayList;

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

    public static void openChangedPasswordActivity(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
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
        Intent intent = new Intent(context, ShoppingCartActivity.class);
        context.startActivity(intent);
    }

    public static void openShoppingCartActivityForCheckLogin(Activity context, int requestCode) {
        if (UserConfig.isClientLogined()) {
            openShoppingCartActivity(context);
        } else {
            openLoginActivity(context, requestCode);
        }
    }

    /**
     * 打开商品分类
     *
     * @param context
     */
    public static void openMedicineGroupActivity(Context context) {
        Intent intent = new Intent(context, MedicineGroupActivity.class);
        context.startActivity(intent);
    }

    //打开搜索页面
    public static void openSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    /**
     * 打开药品详情页
     *
     * @param context
     * @param guid
     */
    public static void openMedicineActivity(Context context, String guid) {
        openMedicineActivity(context, guid, GoodsFlag.NORMAL);
    }

    /**
     * 打开药品详情页
     *
     * @param context
     * @param guid
     * @param goodsFlag {@link GoodsFlag}
     */
    public static void openMedicineActivity(Context context, String guid, int goodsFlag) {
        openMedicineActivity(context, guid, goodsFlag, true);
    }

    /**
     * 打开药品详情页
     *
     * @param context
     * @param guid
     * @param checkNearStore
     */
    public static void openMedicineActivity(Context context, String guid, boolean checkNearStore) {
        openMedicineActivity(context, guid, GoodsFlag.NORMAL, checkNearStore);
    }

    /**
     * 打开药品详情页
     *
     * @param context
     * @param guid
     * @param goodsFlag      {@link GoodsFlag}
     * @param checkNearStore
     */
    public static void openMedicineActivity(Context context, String guid, int goodsFlag, boolean checkNearStore) {
        Intent intent = new Intent(context, GoodsDetailActivity.class);
        Bundle arguments = new Bundle();
        arguments.putInt(GoodsFlag.ARGUMENT_KEY_GOODS_FLAG, goodsFlag);
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
            targetUrl = targetUrl.replace("{1}", UserConfig.getInstance().getClientUserId());
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

    /**
     * 打开web页面
     *
     * @param context
     * @param title   标题
     * @param url     链接
     */
    public static void openWebActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, ADWebActivity.class);
        Bundle arguments = new Bundle();
        if (!TextUtils.isEmpty(title)) {
            arguments.putString(ADWebActivity.ARGUMENTS_KEY_TITLE, title);
        }
        arguments.putString(ADWebActivity.ARGUMENTS_KEY_TARGET_URL, url);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }

    /**
     * 传入html dom 载入web页面
     *
     * @param context 上下文
     * @param title   标题
     * @param html    html内容
     */
    public static void openWebActivityWithHtml(Context context, String title, String html, String iconUrl) {
        Intent intent = new Intent(context, ADWebActivity.class);
        Bundle arguments = new Bundle();
        if (!TextUtils.isEmpty(title)) {
            arguments.putString(ADWebActivity.ARGUMENTS_KEY_TITLE, title);
        }
        arguments.putString(ADWebActivity.ARGUMENTS_KEY_HTML, html);
        arguments.putString(ADWebActivity.ARGUMENTS_KEY_ICON_URL, iconUrl);
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

    public static void openAddShippingAddress(Activity context, AddressEntity entity, int type) {
        Intent intent = new Intent(context, NewShoppingAddressActivity.class);
        intent.putExtra("responseUpDataEntity", entity);
        intent.putExtra("responseType", type);
        context.startActivity(intent);
    }

    public static void openShippingAddress(Activity context, int requestCode) {
        Intent intent = new Intent(context, ControlAddressActivity.class);
        intent.putExtra("chose", "chose");
        context.startActivityForResult(intent, requestCode);
    }

    //帐号管理打开编辑详细信息页面
    public static void openUserLabelsActivity(Activity context, Serializable entity) {
        Intent intent = new Intent(context, UserLabelsActivity.class);
        intent.putExtra("personEntity", entity);
        context.startActivityForResult(intent, UserGuidConfig.REQUEST_ACCOUNTSETTING2_TO_EDITACTIVITY);
    }

    /**
     * 打开账户管理界面
     *
     * @param context
     */
    public static void openAccountSettingActivity(Context context) {
        context.startActivity(new Intent(context, AccountSettingActivity.class));
    }

    //打开优惠卷界面
    public static void openCuoponActivityWithBundle(Context context, boolean canClick) {
        Intent intent = new Intent(context, CuoponActivity.class);
        intent.putExtra("canClick", canClick);
        intent.putExtra("position", -1);
        intent.putExtra("sumMoney", "0");
        context.startActivity(intent);
    }


    //帐号管理打开编辑姓名页面
    public static void openEditActivityForEditName(Activity context) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("activity_title", "编辑姓名");
        intent.putExtra("formActivityName", "AccountSettingActivity");
        context.startActivityForResult(intent, UserGuidConfig.REQUEST_ACCOUNTSETTING_TO_EDITACTIVITY);
    }

    //帐号管理打开编辑职业页面
    public static void openEditActivityForEditProfession(Activity context) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("activity_title", "编辑职业");
        intent.putExtra("formActivityName", "AccountSettingActivity");
        context.startActivityForResult(intent, UserGuidConfig.REQUEST_ACCOUNTSETTING2_TO_EDITACTIVITY);
    }

    //添加用药提醒打开家庭药箱页面
    public static void openFamilyDrugGroupActivity(Activity context) {
        Intent intent = new Intent(context, FamilyDrugGroupActivity.class);
        intent.putExtra("isFromAddRemindDrug", true);
        context.startActivityForResult(intent, UserGuidConfig.REQUEST_REMIND_TO_DRUGGROUP);
    }

    //添加用药提醒打开提醒时间的页面
    public static void openAddRemindTimesActivity(Activity context, ArrayList<String> timesData) {
        Intent intent = new Intent(context, AddRemindTimesActivity.class);
        intent.putStringArrayListExtra("timesDataList", timesData);
        context.startActivityForResult(intent, UserGuidConfig.REQUEST_NEW_REMIND_TO_REMIND_TIMES);
    }

    //添加用药提醒打开家庭成员页面
    public static void openFamilyMemberActivity(Activity context) {
        Intent intent = new Intent(context, FamilyMemberActivity.class);
        intent.putExtra("isFromAddRemind", true);
        context.startActivityForResult(intent, UserGuidConfig.REQUEST_REMIND_TO_MEMBER);
    }

    //打开添加用药提醒页面
    public static void openAddNewRemindActivity(Context context) {
        context.startActivity(new Intent(context, AddNewRemindActivity.class));
    }


    //打开会员界面
    public static void openMemberActivity(Context context) {
        Intent intent = new Intent(context, MemberActivity.class);
        context.startActivity(intent);
    }

    public static void openControlAddressActivityForResult(Activity context, int requestCode) {
        Intent i = new Intent(context, ControlAddressActivity.class);
        i.putExtra("chose", "chose");
        context.startActivityForResult(i, requestCode);
    }

    public static void openMyOrderActivity(Context context) {
        Intent intent = new Intent(context, MyOrderActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void openOrderDetailForOrderNoActivity(Context context, String orderNo) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.ARGUMENT_KEY_ORDER_NO, orderNo);
        context.startActivity(intent);
    }

    public static void openHomeActivity(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        context.startActivity(i);
        ((Activity) context).finish();
    }

    public static void openHistoryActivity(Context context) {
        Intent intent = new Intent(context, HistoryActivity.class);
        context.startActivity(intent);
    }

    public static void openUserLocationActivity(Context context) {
        Intent intent = new Intent(context, LocationActivity.class);
        Bundle arguments = new Bundle();
        arguments.putBoolean(LocationActivity.ARGUMENT_KEY_FROM_USER, true);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }

    public static void openPayActivity(Context context, String deliveryName, double sumMoney, String orderNumber) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra("sumMoney", sumMoney);
        intent.putExtra("deliveryName", deliveryName);
        intent.putExtra("orderNumber", orderNumber);
        context.startActivity(intent);
    }

    public static void openMedicineClass(Context context, String id, String name) {
        Intent intent = new Intent(context, ShopListActivity.class);
        Bundle arguments = new Bundle();
        arguments.putString(HomeHealthNewFragment.ARGUMENTS_KEY_ID, id);
        arguments.putString(HomeHealthNewFragment.ARGUMENTS_KEY_NAME, name);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }


    public static boolean openPayPrepareActivity(Context context, String payType, Bundle arguments) {
        Intent intent = Pay.getInstance().createPayPrepareComponentName(context, payType);
        if (intent != null) {
//            Bundle arguments = new Bundle();
//            arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_NO, orderNo);
//            arguments.putString(PayPrepareBaseActivity.ARGUMENTS_KEY_ORDER_DATE, orderDate);
//            arguments.putDouble(PayPrepareBaseActivity.ARGUMENTS_KEY_NEED_PAY_AMOUNT, payAmount.doubleValue());
            intent.putExtras(arguments);
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}

package com.romens.yjk.health.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.romens.android.AndroidUtilities;
import com.romens.android.ApplicationLoader;
import com.romens.android.log.FileLog;
import com.romens.yjk.health.MyApplication;
import com.romens.yjk.health.model.LocationEntity;

/**
 * Created by siery on 15/8/18.
 */
public class LocationHelper {
    private static final String LOCATION_PREFERENCE_NAME = "my_location";

    private static final String KEY_LAST_LOCATION_CITY_CODE = "last_location_city_code";
    private static final String KEY_LAST_LOCATION_LAT = "last_location_lat";
    private static final String KEY_LAST_LOCATION_LON = "last_location_lon";

    public static boolean writeLastLocation(String cityCode, double lat, double lon) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LAST_LOCATION_CITY_CODE, cityCode);
        editor.putString(KEY_LAST_LOCATION_LAT, String.valueOf(lat));
        editor.putString(KEY_LAST_LOCATION_LON, String.valueOf(lon));
        return editor.commit();
    }

    public static SharedPreferences getSharedPreferences() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(LOCATION_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences;
    }

    public static String getLastLocationCityCode() {
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(KEY_LAST_LOCATION_CITY_CODE, "");
    }

    public static double getLastLocationLat() {
        SharedPreferences preferences = getSharedPreferences();
        String latStr = preferences.getString(KEY_LAST_LOCATION_LAT, "0");
        double lat = Double.valueOf(latStr);
        return lat;
    }

    public static double getLastLocationLon() {
        SharedPreferences preferences = getSharedPreferences();
        String lonStr = preferences.getString(KEY_LAST_LOCATION_LON, "0");
        double lon = Double.valueOf(lonStr);
        return lon;
    }


    public static String createUrl(double lon, double lat, int width, int height) {
        String locationKey;
        try {
            ApplicationInfo appInfo = MyApplication.applicationContext.getPackageManager()
                    .getApplicationInfo(MyApplication.applicationContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            locationKey = appInfo.metaData.getString("location_preview_key");
        } catch (PackageManager.NameNotFoundException e) {
            locationKey = null;
        }
        int scale = getScale();
        return createUrl(lon, lat, scale, width, height, locationKey);
    }

    public static String createUrl(double lon, double lat, int scale, int width, int height, String locationKey) {
        if (TextUtils.isEmpty(locationKey)) {
            return null;
        }
        String locationFormat = "http://restapi.amap.com/v3/staticmap?location=%f,%f&zoom=15&size=%d*%d&scale=%d&markers=large,,A:%f,%f&key=%s";
        String locationUrl = String.format(locationFormat, lon, lat, width, height, scale, lon, lat, locationKey);
        return locationUrl;
    }

    public static int getScale() {
        int scale = Math.min(2, (int) Math.ceil(AndroidUtilities.density));
        return scale;
    }

    /**
     * 是否开启了开发者模式中的模拟定位
     *
     * @param context
     * @return
     */
    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            return false;
        }
        return true;
    }

    /**
     * 检测是否被重新打包添加模拟定位权限
     *
     * @param context
     * @return
     */
    public static boolean areThereMockPermissionApps(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);

            // Get Permissions
            String[] requestedPermissions = packageInfo.requestedPermissions;

            if (requestedPermissions != null) {
                for (int i = 0; i < requestedPermissions.length; i++) {
                    if (requestedPermissions[i]
                            .equals("android.permission.ACCESS_MOCK_LOCATION")) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            FileLog.e("areThereMockPermissionApps", "Got exception " + e.getMessage());
        }
        return false;
    }


    public static boolean checkPermission(Context context) {
        if (context == null) {
            return false;
        }
        String packageName = context.getPackageName();
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            boolean permission = (PackageManager.PERMISSION_GRANTED ==
                    packageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION", packageName));
            if (!permission) {
                return false;
            }
            permission = (PackageManager.PERMISSION_GRANTED ==
                    packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", packageName));
            return permission;
        }
        return false;
    }

    private static final String PREFERENCE_NAME = "last_location";

    public static AMapLocation getLastLocation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        AMapLocation location = new AMapLocation("network");
        location.setLatitude(sharedPreferences.getFloat("lat", 0f));
        location.setLongitude(sharedPreferences.getFloat("lon", 0f));
        location.setAccuracy(sharedPreferences.getFloat("accuracy", 0f));
        location.setCityCode(sharedPreferences.getString("cityCode", ""));
        location.setCity(sharedPreferences.getString("city", ""));
        location.setAddress(sharedPreferences.getString("address", ""));
        return location;
    }

    public static void updateLastLocation(Context context, AMapLocation location) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (location == null) {
            editor.clear().commit();
        } else {
            editor.putFloat("lat", (float) location.getLatitude());
            editor.putFloat("lon", (float) location.getLongitude());
            editor.putFloat("accuracy", location.getAccuracy());
            editor.putString("cityCode", location.getCityCode());
            editor.putString("city", location.getCity());
            editor.putString("address", location.getAddress());
            editor.commit();
        }
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onLastLocationChanged);
    }

    public static void updateLastLocation(Context context, LocationEntity location) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (location == null) {
            editor.clear().commit();
        } else {
            editor.putFloat("lat", (float) location.lat);
            editor.putFloat("lon", (float) location.lon);
            editor.putFloat("accuracy", 0);
            editor.putString("cityCode", location.cityCode);
            editor.putString("city", location.cityCode);
            editor.putString("address", location.address);
            editor.commit();
        }
        AppNotificationCenter.getInstance().postNotificationName(AppNotificationCenter.onLastLocationChanged);
    }
}

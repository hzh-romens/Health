package com.romens.yjk.health.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.romens.android.ApplicationLoader;

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
}

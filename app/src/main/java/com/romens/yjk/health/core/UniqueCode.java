package com.romens.yjk.health.core;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UniqueCode {
    public static String imei(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
        return imei;
    }

    public static String deviceId() {
        String deviceid = "35"
                + // we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                + Build.USER.length() % 10; // 13 digits
        return deviceid;
    }

    public static String androidId(Context context) {
        String androidid = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return androidid;
    }

    public static String wifimac(Context context) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            String wifimac = wm.getConnectionInfo().getMacAddress();
            return wifimac;
        }
        return "";
    }

    public static String btmac(Context context) {
        BluetoothAdapter btadapter = null; // Local Bluetooth adapter
        btadapter = BluetoothAdapter.getDefaultAdapter();
        String btmac = btadapter.getAddress();
        return btmac;
    }

    public static String uniqueID(Context context) {
        String IMEI = imei(context);
        String deviceId = deviceId();
        String androidId = androidId(context);
        String wifiMAC = wifimac(context);
        // String btmac = btmac(context);
        // String key = imei + deviceid + androidid + wifimac + btmac;
        String id = IMEI + deviceId + androidId + wifiMAC;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(id.getBytes(), 0, id.length());
        byte p_md5Data[] = m.digest();

        String uniqueId = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper
            // padding)
            if (b <= 0xF)
                uniqueId += "0";
            // add number to string
            uniqueId += Integer.toHexString(b);
        }
        uniqueId = uniqueId.toUpperCase();
        return uniqueId;
    }
}

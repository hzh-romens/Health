package com.romens.yjk.health.helper;

import com.romens.android.network.core.Base64;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by siery on 15/4/25.
 */
public class Base64Helper {
    public static String encodeBase64String(String content) {
        if (content == null || content.length() <= 0) {
            return "";
        }
        byte[] contentBytes = content.getBytes(Charset.forName("utf-8"));
        String base64Str = Base64.encodeBase64String(contentBytes);
        base64Str = base64Str.replace("=", "-").replace("+", "_");
        return base64Str;
    }

    public static String decodeBase64(String base64Str) {
        if (base64Str == null || base64Str.length() <= 0) {
            return "";
        }
        base64Str = base64Str.replace("-", "=").replace("_", "+");
        String content = null;
        try {
            byte[] contentBytes = Base64.decodeBase64(base64Str);
            content = new String(contentBytes, Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
}

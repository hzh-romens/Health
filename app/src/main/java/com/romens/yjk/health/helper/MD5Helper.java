package com.romens.yjk.health.helper;

import com.romens.android.log.FileLog;
import com.romens.android.network.core.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by siery on 15/4/25.
 */
public class MD5Helper {
    public static String createMD5(String content) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            FileLog.e(MD5Helper.class.toString(), e);
            return null;
        }
        digest.update(content.getBytes());
        byte[] bytes = digest.digest();
        String md5Str = getHashString(bytes);
        return md5Str;
    }

    public static byte[] createMD5Base64(String content) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            FileLog.e(MD5Helper.class.toString(), e);
            return null;
        }
        digest.update(content.getBytes());
        byte[] bytes = digest.digest();
        String base64Str = Base64.encodeBase64String(bytes);
        base64Str = base64Str.replace("=", "-").replace("+", "_");
        return bytes;
    }

    public static String createMD5Base64Str(String content) {
        byte[] bytes = createMD5Base64(content);
        String base64Str = Base64.encodeBase64String(bytes);
        base64Str = base64Str.replace("=", "-").replace("+", "_");
        return base64Str;
    }

    public static String createMD5HashStr(String content) {
        byte[] bytes = createMD5Base64(content);
        String hashStr = getHashString(bytes);
        return hashStr;
    }

    private static String getHashString(byte[] digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }
}

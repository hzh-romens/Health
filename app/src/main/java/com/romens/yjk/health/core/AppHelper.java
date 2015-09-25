package com.romens.yjk.health.core;

import android.content.pm.PackageInfo;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by siery on 15/9/21.
 */
public class AppHelper {
    public static String getPublicKeyString(PackageInfo pi) {
        PublicKey pubKey = getPublicKey(pi);
        if (pubKey == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Base64.encodeToString(pubKey.getEncoded(),
                Base64.DEFAULT));
        return stringBuffer.toString();
    }

    private static PublicKey getPublicKey(PackageInfo pi) {
        try {
            if (pi.signatures == null || pi.signatures.length == 0) {
                return null;
            }
            PublicKey publicKey = null;
            if (pi.signatures.length > 0) {
                byte[] signature = pi.signatures[0].toByteArray();
                CertificateFactory certFactory = CertificateFactory
                        .getInstance("X.509");
                InputStream is = new ByteArrayInputStream(signature);
                X509Certificate cert = (X509Certificate) certFactory
                        .generateCertificate(is);
                publicKey = cert.getPublicKey();
            }
            return publicKey;
        } catch (Exception ex) {

        }
        return null;
    }
}

package com.romens.yjk.health.pay;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.romens.yjk.health.config.FacadeConfig;

import java.util.List;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class PayAppManager {

    private static boolean isSetup(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packageInfos.size(); i++) {
            if (packageInfos.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public static boolean isSetupYBHEB(Context context) {
        return isSetup(context, "com.yitong.hrb.people.android");
    }

    /**
     * 未安装支付App下载
     * @param context
     * @param payMode
     */
    public static void needDownloadPayApp(Context context, String payMode) {
        String downloadURL = String.format("%sPayApp?Mode=%s", FacadeConfig.getUrl(), payMode);
        Uri uri = Uri.parse(downloadURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}

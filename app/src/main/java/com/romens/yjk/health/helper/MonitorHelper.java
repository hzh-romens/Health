package com.romens.yjk.health.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.romens.bug.PgyerManager;
import com.romens.yjk.health.MyApplication;

/**
 * Created by siery on 16/1/5.
 */
public class MonitorHelper {
    public static final void init(Context context) {
        //PgyerManager.initCrash(context);
    }

    public static void unregisterUpdate() {
        PgyerManager.destroyUpdate();
    }

    public static void checkUpdate(final Activity context) {
        checkUpdate(context, false);
    }

    public static void checkUpdate(final Activity context, final boolean showNoUpdate) {

        PgyerManager.checkUpdate(context, new PgyerManager.UpdateCallback() {
            @Override
            public void onUpdateResponse(final Bundle bundle) {
                int newVersion;
                try {
                    newVersion = Integer.parseInt(bundle.getString(PgyerManager.KEY_VERSION_CODE));
                } catch (NumberFormatException e) {
                    newVersion = 0;
                }
                int currVersion = MyApplication.getAppVersion();
                if (newVersion > currVersion) {
                    String title = "新版本 " + bundle.getString(PgyerManager.KEY_VERSION_NAME);
                    String content = bundle.getString(PgyerManager.KEY_RELEASE_NOTE);
                    new AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(content)
                            .setPositiveButton("更新",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            String downloadURL = bundle.getString(PgyerManager.KEY_DOWNLOAD_URL);
                                            PgyerManager.startDownloadTask(context, downloadURL);
                                        }
                                    }).create().show();
                    return;
                }
            }

            @Override
            public void onNoUpdate() {
                if (showNoUpdate) {
                    Toast.makeText(context, "已经是最新版!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

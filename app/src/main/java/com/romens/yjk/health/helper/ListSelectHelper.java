package com.romens.yjk.health.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.romens.yjk.health.ui.activity.ListSelectActivity;

import java.util.ArrayList;

/**
 * Created by siery on 15/10/28.
 */
public class ListSelectHelper {

    public static void openSelectDialog(Context context, String title, CharSequence[] items, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, listener)
                .setNegativeButton("取消", null)
                .create().show();
    }

    public static void openSelectDialog(Activity context, int requestCode, String title, ArrayList<String> names) {
        Intent intent = new Intent(context, ListSelectActivity.class);
        intent.putExtra(ListSelectActivity.ARGUMENTS_KEY_TITLE, title);
        intent.putStringArrayListExtra(ListSelectActivity.ARGUMENTS_KEY_NAME_LIST, names);
        intent.putStringArrayListExtra(ListSelectActivity.ARGUMENTS_KEY_NAME_LIST, names);
        context.startActivityForResult(intent, requestCode);
    }

    public static void openSelectDialog(Activity context, int requestCode, String title, ArrayList<String> names, ArrayList<String> values) {
        Intent intent = new Intent(context, ListSelectActivity.class);
        intent.putExtra(ListSelectActivity.ARGUMENTS_KEY_TITLE, title);
        intent.putStringArrayListExtra(ListSelectActivity.ARGUMENTS_KEY_NAME_LIST, names);
        intent.putStringArrayListExtra(ListSelectActivity.ARGUMENTS_KEY_NAME_LIST, values);
        context.startActivityForResult(intent, requestCode);
    }
}

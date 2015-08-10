package com.romens.yjk.health.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.romens.android.log.FileLog;

/**
 * Created by zhoulisi on 15/4/23.
 */
public abstract class BaseFragment extends Fragment {
    protected AlertDialog visibleDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateRootView(inflater, container, savedInstanceState);
    }

    protected abstract View onCreateRootView(LayoutInflater inflater, ViewGroup container,
                                             Bundle savedInstanceState);


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onRootViewCreated(view, savedInstanceState);
    }

    protected abstract void onRootViewCreated(View view, Bundle savedInstanceState);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRootActivityCreated(savedInstanceState);
    }

    protected abstract void onRootActivityCreated(Bundle savedInstanceState);

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (visibleDialog != null && visibleDialog.isShowing()) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("BaseFragment.onPause", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public AlertDialog showAlertDialog(AlertDialog.Builder builder) {
        try {
            if (visibleDialog != null) {
                visibleDialog.dismiss();
                visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e("BaseFragment.showAlertDialog", e);
        }
        try {
            visibleDialog = builder.show();
            visibleDialog.setCanceledOnTouchOutside(true);
            visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    visibleDialog = null;
                    onDialogDismiss();
                }
            });
            return visibleDialog;
        } catch (Exception e) {
            FileLog.e("BaseFragment.showAlertDialog", e);
        }
        return null;
    }

    protected void onDialogDismiss() {

    }
}

package com.romens.yjk.health.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by siery on 15/12/15.
 */
public class ServiceFragment  extends Fragment {

    private static final String KServiceTagPrefix = "service:";

    @Override public void onCreate(final Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);
    }

    /** @see {@link android.support.v4.content.LocalBroadcastManager#sendBroadcast(Intent)} */
    protected boolean sendLocalBroadcast(final Intent intent) {
        return LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    public static <T extends ServiceFragment> T getService(final Class<T> service_class, final FragmentManager fm) {
        if (fm == null) throw new IllegalArgumentException("FragmentManager is null");
        final String service_name = KServiceTagPrefix + service_class.getCanonicalName();
        @SuppressWarnings("unchecked") T service = (T) fm.findFragmentByTag(service_name);
        if (service == null) {
            Log.i(TAG, "Starting service: " + service_class.getSimpleName());
            try {
                service = service_class.newInstance();
            } catch (final java.lang.InstantiationException e) {
                throw new IllegalArgumentException(service_class + " cannot be instantiated");
            } catch (final IllegalAccessException e) {
                throw new IllegalArgumentException(service_class + " is inaccessible");
            }
            final FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(service, service_name);
            transaction.commit();
            fm.executePendingTransactions();
        }
        return service;
    }

    private static final String TAG = ServiceFragment.class.getSimpleName();
}
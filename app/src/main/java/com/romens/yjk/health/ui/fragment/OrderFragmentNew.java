package com.romens.yjk.health.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by anlc on 2015/11/6.
 */
public class OrderFragmentNew extends Fragment {

    private static final String KEY_TYPE = "fragment_type";

    public static OrderFragmentNew newInstance(String type) {
        OrderFragmentNew orderFragmentNew = new OrderFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE, type);
        return orderFragmentNew;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

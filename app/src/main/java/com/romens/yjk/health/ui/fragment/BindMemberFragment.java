package com.romens.yjk.health.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.cells.EditTextCells;

/**
 * Created by HZH on 2016/2/1.
 */
public class BindMemberFragment extends Fragment {
    private FrameLayout pharmacy, container;
    private CardView btnOk;
    private TextView pharmacyName;
    private RadioGroup radioGroup;
    private EditTextCells cells;
    private int selectMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bindmember, container, false);
        return view;
    }
}

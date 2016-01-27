package com.romens.yjk.health.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.romens.yjk.health.R;
import com.romens.yjk.health.ui.adapter.CuoponAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2016/1/15.
 */
public class CuoponFragment extends Fragment {
    private ListView listView;
    private CuoponAdapter cuoponAdapter;
    private int mFlag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuopon, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        cuoponAdapter = new CuoponAdapter(getActivity());
        if (mFlag == 1) {
            getData();
            cuoponAdapter.bindData(result);
        } else {
            getOther();
            cuoponAdapter.bindData(others);
        }
        listView.setAdapter(cuoponAdapter);
        return view;
    }

    private List<String> result;
    private List<String> others;

    private void getOther() {
        others = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            others.add("其他" + i);
        }
    }


    private void getData() {
        result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add("测试" + i);
        }
    }

    public void setFlag(int flag) {
        this.mFlag = flag;
    }

}

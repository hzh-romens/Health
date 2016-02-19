package com.romens.yjk.health.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.model.CuoponEntity;
import com.romens.yjk.health.ui.CommitOrderActivity;
import com.romens.yjk.health.ui.adapter.CuoponAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by HZH on 2016/1/15.
 */
public class CuoponFragment extends Fragment {
    private ListView listView;
    private CuoponAdapter cuoponAdapter;
    private int mFlag;
    private String requestType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuopon, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        cuoponAdapter = new CuoponAdapter(getActivity());
        if (mFlag == 1) {
            requestType = "GetCoupon";
            getCuopon();
            //cuoponAdapter.bindData(result);
        } else {
            requestType = "GetCouponHistory";
            getCuopon();
            //cuoponAdapter.bindData(result);
        }
        // listView.setAdapter(cuoponAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CommitOrderActivity.class);
                getActivity().setResult(4, intent);
                getActivity().finish();
            }
        });
        return view;
    }

    private List<CuoponEntity> result;
    // private List<CuoponEntity> others;


    public void setFlag(int flag) {
        this.mFlag = flag;
    }

    public void getCuopon() {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("USERGUID", UserConfig.getClientUserEntity().getGuid()).build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", requestType, args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Log.e("GetCoupon", "ERROR");
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                //  needHideProgress();
                if (errorMsg == null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String response = responseProtocol.getResponse();
                    if (result != null) {
                        result.clear();
                    }
                    result = CuoponEntity.toEntity(response);
                    cuoponAdapter.bindData(result);
                    listView.setAdapter(cuoponAdapter);
                } else {
                    Log.e("GetCoupon", errorMsg.msg);
                }
            }
        });
    }

}

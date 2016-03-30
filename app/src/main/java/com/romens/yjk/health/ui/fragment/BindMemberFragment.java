package com.romens.yjk.health.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.model.MemberType;
import com.romens.yjk.health.ui.MemberBaseActivity;
import com.romens.yjk.health.ui.adapter.NewMemberAdapter;

import java.util.Map;

/**
 * Created by HZH on 2016/2/1.
 */
public class BindMemberFragment extends AppFragment {
    private RecyclerView listView;
    private NewMemberAdapter adapter;

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(getActivity());
        listView = new RecyclerView(getActivity());
        listView.setBackgroundColor(getResources().getColor(R.color.new_grey));
        content.addView(listView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setType();
        adapter = new NewMemberAdapter(getActivity());
        adapter.bindType(types);
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(new NewMemberAdapter.onItemClickListener() {
            @Override
            public void sureButtonClickListener(String phoneValue, String pswValue) {
                bindMember(phoneValue, pswValue);
            }
        });

        return content;
    }

    private void bindMember(String phoneValue, String pswValue) {
        Map<String, String> args = new FacadeArgs.MapBuilder()
                .put("PHONE", phoneValue)
                .put("CODE", pswValue)
                .build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "GetUserMemberCard", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Connect connect = new RMConnect.Builder(HomeHealthNewFragment.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(
                        new Connect.AckDelegate() {
                            @Override
                            public void onResult(Message message, Message errorMessage) {
                                if (errorMessage == null) {
                                    ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                                    JsonNode jsonNode = responseProtocol.getResponse();
                                    MemberBaseActivity activity = (MemberBaseActivity) getActivity();
                                    Handler handler = activity.handler;
                                    handler.sendEmptyMessage(1);
                                }
                            }
                        }
                ).build();

        ConnectManager.getInstance().request(getActivity(), connect);
    }


    private SparseIntArray types = new SparseIntArray();

    private void setType() {
        types.append(0, MemberType.TIP);
        types.append(1, MemberType.EMPTY);
        types.append(2, MemberType.PHONE);
        types.append(3, MemberType.PSW);
        types.append(4, MemberType.ADVICE);
        types.append(5, MemberType.BUTTON);
    }

    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }
}

package com.romens.yjk.health.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.zxing.BarcodeFormat;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.adapter.MemberAdapter;
import com.romens.yjk.health.ui.utils.CodeUtils;
import com.romens.yjk.health.ui.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2016/3/24.
 */
public class MemberFragment extends BaseFragment {
    private List<String> types;
    private ListView listview;
    private MemberAdapter memberAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dialogUtils.show_infor(getActivity(), codeImage, cardId);
                    break;
            }
        }
    };

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(getActivity());
        content.setBackgroundColor(Color.WHITE);
        listview = new ListView(getActivity());
        listview.setDividerHeight(0);
        initMember();
        Bundle arguments = getArguments();
        getBundleValue(arguments);
        content.addView(listview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return content;
    }

    private void getBundleValue(Bundle arguments) {
        cardId = arguments.getString("cardId");
        if (arguments != null) {
            memberAdapter.setData(cardId, arguments.getString("lvLevel"), arguments.getString("integral")
                    , arguments.getString("remainMoney"), arguments.getString("diybgc"), arguments.getString("cardbackbg"));
        }
    }

    private String cardId;
    private DialogUtils dialogUtils;

    private void initMember() {
        memberAdapter = new MemberAdapter(getActivity());
        getMemberData();
        memberAdapter.bindData(types);
        listview.setAdapter(memberAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    dialogUtils = new DialogUtils();
                    int mWidth = (int) (((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 1.2);
                    getCodeImage(mWidth);
                } else if (position == 4) {
                    //优惠卷
                    UIOpenHelper.openCuoponActivityWithBundle(getActivity(), false);
                } else if (position == 8) {
                    //跳转到个人资料
                    UIOpenHelper.openAccountSettingActivity(getActivity());
                } else if (position == 5) {
                    //跳转到会员权益页面
                    UIOpenHelper.openAbout(getActivity(), "会员权益");
                }
            }
        });
    }

    private Bitmap codeImage;

    private void getCodeImage(final int width) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                codeImage = CodeUtils.encodeAsBitmap(UserConfig.getInstance().getClientUserPhone(), BarcodeFormat.CODE_128, width - 24, 96);
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    public void getMemberData() {
        types = new ArrayList<String>();
        for (int i = 1; i <= 6; i++) {
            types.add(i + "");
        }
    }


    @Override
    protected void onRootViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void onRootActivityCreated(Bundle savedInstanceState) {

    }
}

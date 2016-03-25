package com.romens.yjk.health.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.zxing.BarcodeFormat;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.config.UserConfig;
import com.romens.yjk.health.helper.UIOpenHelper;
import com.romens.yjk.health.ui.adapter.CardListAdapter;
import com.romens.yjk.health.ui.adapter.MemberAdapter;
import com.romens.yjk.health.ui.utils.CodeUtils;
import com.romens.yjk.health.ui.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HZH on 2016/3/24.
 */
public class MemberFragment extends BaseFragment {
    private boolean isMember = true;
    private LinearLayout btnOk, pwdLayout;
    private ActionBar actionBar;
    private List<String> types;
    private ListView listview, cardList;
    private CardListAdapter cardListAdapter;
    private MemberAdapter memberAdapter;

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(getActivity());
        listview = new ListView(getActivity());
        initMember();
        Bundle arguments = getArguments();
        getBundleValue(arguments);
        content.addView(listview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return content;
    }

    private void getBundleValue(Bundle arguments) {
        memberAdapter.setData(arguments.getString("cardId"), arguments.getString("lvLevel"), arguments.getString("integral")
                , arguments.getString("remainMoney"), arguments.getString("diybgc"), arguments.getString("cardbackbg"));
    }

    private void initMember() {
        memberAdapter = new MemberAdapter(getActivity());
        getMemberData();
        memberAdapter.bindData(types);
        listview.setAdapter(memberAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3) {
                    Bitmap bitmap = CodeUtils.encodeAsBitmap(UserConfig.getInstance().getClientUserPhone(), BarcodeFormat.CODE_128, 300, 96);
                    DialogUtils dialogUtils = new DialogUtils();
                    dialogUtils.show_infor(getActivity(), bitmap);
                } else if (position == 5) {
                    //优惠卷
                    UIOpenHelper.openCuoponActivityWithBundle(getActivity(), false);
                } else if (position == 8) {
                    //跳转到个人资料
                    UIOpenHelper.openAccountSettingActivity(getActivity());
                }
            }
        });
    }

    public void getMemberData() {
        types = new ArrayList<String>();
        for (int i = 1; i <= 4; i++) {
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

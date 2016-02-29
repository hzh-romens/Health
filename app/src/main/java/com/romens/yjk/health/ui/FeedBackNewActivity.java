package com.romens.yjk.health.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.components.FlowLayoutCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2016/2/25.
 */
public class FeedBackNewActivity extends BaseActivity implements View.OnClickListener {

    private EditText feedBackInfo;
    private TextView submitBtn;
    private ActionBar actionBar;
    private TextView chooseTagBtn;

    private List<String> selectTagTxtList;
    private String userGuid = "3333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        setContentView(R.layout.activity_feed_back_new, R.id.feed_back_action);
        actionBarEvent();
        feedBackInfo = (EditText) findViewById(R.id.feed_back_info);
        submitBtn = (TextView) findViewById(R.id.feed_back_submit);
        chooseTagBtn = (TextView) findViewById(R.id.feed_back_choose_tag);

        submitBtn.setOnClickListener(this);
        chooseTagBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feed_back_submit:
                String info = feedBackInfo.getText().toString().trim();
                if (info == null || info.equals("")) {
                    Toast.makeText(FeedBackNewActivity.this, "请输入意见信息", Toast.LENGTH_SHORT).show();
                } else {
                    submitBtn.setClickable(false);
                    needShowProgress("正在提交.....");
                    requestFeedBack(userGuid, info, getTags());
                }
                break;
            case R.id.feed_back_choose_tag:
                Intent intent = new Intent(FeedBackNewActivity.this, FeedBackChooseTagActivity.class);
                startActivityForResult(intent, UserGuidConfig.REQUEST_FEEDBACK_TO_CHOOSETAG);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserGuidConfig.RESPONSE_CHOOSETAG_TO_FEEDBACK) {
            selectTagTxtList = data.getStringArrayListExtra("selectTagArray");
            String tagStr = Arrays.asList(selectTagTxtList).toString();
            chooseTagBtn.setText(tagStr.substring(2, tagStr.length() - 2));
        }
    }

    private String getTags() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < selectTagTxtList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("TAG", selectTagTxtList.get(i));
                jsonArray.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    private void actionBarEvent() {
        actionBar = getMyActionBar();
        actionBar.setTitle("意见反馈");
        actionBar.setBackgroundResource(R.color.theme_primary);
        actionBar.setMinimumHeight(AndroidUtilities.dp(100));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                super.onItemClick(i);
                if (i == -1) {
                    finish();
                }
            }
        });
    }

    //请求意见反馈
    private void requestFeedBack(String userGuid, String feedBackInfo, String tags) {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        args.put("USERGUID", userGuid);
        args.put("ADVICE", feedBackInfo);
        args.put("JSONDATA", tags);
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "Feedback", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(FeedBackNewActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    String requestCode = "";
                    try {
                        JSONObject jsonObject = new JSONObject(responseProtocol.getResponse());
                        requestCode = jsonObject.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (requestCode.equals("yes")) {
                        Toast.makeText(FeedBackNewActivity.this, "您好，我们已收到您的反馈", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(FeedBackNewActivity.this, "反馈未成功", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR" + errorMsg.msg);
                    Toast.makeText(FeedBackNewActivity.this, "反馈未成功", Toast.LENGTH_SHORT).show();
                }
                needHideProgress();
                submitBtn.setClickable(true);
            }
        });
    }
}

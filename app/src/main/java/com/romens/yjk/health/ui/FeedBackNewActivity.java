package com.romens.yjk.health.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.romens.yjk.health.helper.LabelHelper;

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
    private List<String> tagList;
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
        initData();

        submitBtn.setOnClickListener(this);
        chooseTagBtn.setOnClickListener(this);
    }

    private void initData() {
        tagList = new ArrayList<>();
        selectTagTxtList = new ArrayList<>();
        requestGetTags();
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
                String[] tagArray = tagList.toArray(new String[tagList.size()]);
                if (tagArray != null && tagArray.length > 0) {
                    showMulitChooseView(tagArray);
                } else {
                    Toast.makeText(FeedBackNewActivity.this, "没有可选的标签", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //请求获取评价的标签
    private void requestGetTags() {
        needShowProgress("正在加载...");
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetFeedbackTag", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                needHideProgress();
                Toast.makeText(FeedBackNewActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                needHideProgress();
                if (msg != null) {
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    try {
                        JSONArray array = new JSONArray(responseProtocol.getResponse());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (!object.getString("TAGNAME").equals("")) {
                                tagList.add(object.getString("TAGNAME"));
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(FeedBackNewActivity.this, "获取标签失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR" + errorMsg.msg);
                }
            }
        });
    }

    public void showMulitChooseView(final String[] tagList) {
        boolean[] selectFlag = new boolean[tagList.length];
        for (int i = 0; i < tagList.length; i++) {
            if (selectTagTxtList.contains(tagList[i])) {
                selectFlag[i] = true;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("意见标签").
                setMultiChoiceItems(tagList, selectFlag, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectTagTxtList.add(tagList[which]);
                        } else {
                            selectTagTxtList.remove(tagList[which]);
                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectTagTxtList.size() > 0) {
                    CharSequence labels = LabelHelper.createChipForUserInfoLabels(selectTagTxtList);
                    chooseTagBtn.setText(labels);
                } else {
                    chooseTagBtn.setText("点击选择意见标签");
                }
                dialog.dismiss();
            }
        }).show();
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

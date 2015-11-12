package com.romens.yjk.health.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.romens.android.AndroidUtilities;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JsonParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.ui.cells.EditTagCell;
import com.romens.yjk.health.ui.components.FlowLayout;
import com.romens.yjk.health.ui.components.FlowLayoutCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2015/10/14.
 */
public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

    private FlowLayout tagFlowLayout;
    private FlowLayout selectTagFlowLayout;
    //    private EditTagCell editTagTxt;
    private EditText feedBackInfo;
    //    private ImageView firstUpLoadImg;
//    private ImageView secondUpLoadImg;
//    private ImageView threeUpLoadImg;
    private TextView submitBtn;
    private TextView resetBtn;
    private ActionBar actionBar;
    private FrameLayout feedBackSelectTagLayout;

    private List<String> selectTagTxtList;
    private List<String> tagList;
    private String userGuid = "3333";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGuid = UserGuidConfig.USER_GUID;
        setContentView(R.layout.activity_feed_back, R.id.feed_back_action);
        actionBarEvent();
        initData();
        tagFlowLayout = (FlowLayout) findViewById(R.id.feed_back_flow_layout);
        feedBackSelectTagLayout = (FrameLayout) findViewById(R.id.feed_back_select_tag_layout);
//        editTagTxt = (EditTagCell) findViewById(R.key.feed_back_edit_tag);
        feedBackInfo = (EditText) findViewById(R.id.feed_back_info);
//        firstUpLoadImg = (ImageView) findViewById(R.key.feed_back_first_img);
//        secondUpLoadImg = (ImageView) findViewById(R.key.feed_back_second_img);
//        threeUpLoadImg = (ImageView) findViewById(R.key.feed_back_three_img);
        submitBtn = (TextView) findViewById(R.id.feed_back_submit);
        resetBtn = (TextView) findViewById(R.id.feed_back_reset);

        selectTagFlowLayout = new FlowLayout(this);
        flowlayoutEvent(tagFlowLayout, selectTagFlowLayout);
//        editTagEvent();
//        firstUpLoadImg.setOnClickListener(this);
//        secondUpLoadImg.setOnClickListener(this);
//        threeUpLoadImg.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.key.feed_back_first_img:
//                Toast.makeText(FeedBackActivity.this, "firstImgClick", Toast.LENGTH_SHORT).show();
//                break;
//            case R.key.feed_back_second_img:
//                Toast.makeText(FeedBackActivity.this, "secondImgClick", Toast.LENGTH_SHORT).show();
//                break;
//            case R.key.feed_back_three_img:
//                Toast.makeText(FeedBackActivity.this, "threeImgClick", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.feed_back_submit:
                String info = feedBackInfo.getText().toString().trim();
                if (info == null || info.equals("")) {
                    Toast.makeText(FeedBackActivity.this, "请输入意见信息", Toast.LENGTH_SHORT).show();
                } else {
                    submitBtn.setClickable(false);
                    needShowProgress("正在提交.....");
                    requestFeedBack(userGuid, info, getTags());
                }
                break;
            case R.id.feed_back_reset:
                initData();
                feedBackSelectTagLayout.removeAllViews();
                tagFlowLayout.updateLayout();
                selectTagFlowLayout.updateLayout();
//                editTagTxt.setEditViewText("");
                feedBackInfo.setText("");
                break;
        }
    }

    private String getTags() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < selectTagTxtList.size(); i++) {
                if (!selectTagTxtList.get(i).equals("      ")) {
                    JSONObject object = new JSONObject();
                    object.put("TAG", selectTagTxtList.get(i));
                    jsonArray.put(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    private void editTagEvent() {
//        editTagTxt.setRightViewText("贴上", false);
//        editTagTxt.setOnBtnClickListener(new EditTagCell.onBtnClickListener() {
//            @Override
//            public void onClick() {
//                selectTagTxtList.add(editTagTxt.getEditText());
//                selectTagFlowLayout.updateLayout();
//                editTagTxt.setEditViewText("");
//            }
//
//            @Override
//            public void editTextChange() {
//            }
//        });
    }

    private void flowlayoutEvent(final FlowLayout tagFlowLayout, final FlowLayout selectTaglayout) {
        tagFlowLayout.setHorizontalSpacing(AndroidUtilities.dp(4));
        tagFlowLayout.setVerticalSpacing(AndroidUtilities.dp(4));
        tagFlowLayout.setAdapter(new FlowLayoutCallback() {
            @Override
            public int getCount() {
                return tagList.size();
            }

            @Override
            public View getView(final int position, ViewGroup container) {
                TextView textView = new TextView(FeedBackActivity.this);
                textView.setBackgroundResource(R.drawable.bg_light_gray);
                textView.setText(tagList.get(position));
                textView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tagList.get(position).equals("      ")) {
                            return;
                        }
                        if (feedBackSelectTagLayout.getChildCount() < 1) {
                            feedBackSelectTagLayout.addView(selectTaglayout);
                        }
                        selectTagTxtList.add(tagList.get(position));
                        if (selectTagTxtList.get(0).equals("      ")) {
                            selectTagTxtList.remove(0);
                        }
                        selectTagLayoutEvent(selectTaglayout);
                        tagList.remove(position);
                        if (tagList.size() <= 0) {
                            tagList.add("      ");
                        }
                        tagFlowLayout.updateLayout();
                    }
                });
                return textView;
            }
        });
        tagFlowLayout.updateLayout();
    }

    private void selectTagLayoutEvent(final FlowLayout selectTaglayout) {
        selectTaglayout.setHorizontalSpacing(AndroidUtilities.dp(4));
        selectTaglayout.setVerticalSpacing(AndroidUtilities.dp(4));
        selectTaglayout.setAdapter(new FlowLayoutCallback() {
            @Override
            public int getCount() {
                return selectTagTxtList.size();
            }

            @Override
            public View getView(final int position, ViewGroup container) {
                TextView textView = new TextView(FeedBackActivity.this);
                textView.setBackgroundResource(R.drawable.btn_primary_default);
                textView.setText(selectTagTxtList.get(position));
                textView.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(4), AndroidUtilities.dp(16), AndroidUtilities.dp(4));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectTagTxtList.get(position).equals("      ")) {
                            return;
                        }
                        tagList.add(selectTagTxtList.get(position));
                        if (tagList.get(0).equals("      ")) {
                            tagList.remove(0);
                        }
                        selectTagTxtList.remove(position);
                        if (selectTagTxtList.size() <= 0) {
                            selectTagTxtList.add("      ");
                        }
                        selectTaglayout.updateLayout();
                        tagFlowLayout.updateLayout();
                    }
                });
                return textView;
            }
        });
        selectTaglayout.updateLayout();
    }

    private void initData() {
        selectTagTxtList = new ArrayList<>();
        tagList = new ArrayList<>();
//        tagList.add("服务");
//        tagList.add("药品");
//        tagList.add("药师");
//        tagList.add("医师");
        requestGetTags();
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
        Gson gson=new Gson();
        Log.e("tag", "--feedback___args--->" + gson.toJson(args));
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "Feedback", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(FeedBackActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FeedBackActivity.this, "您好，我们已收到您的反馈", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(FeedBackActivity.this, "反馈未成功", Toast.LENGTH_SHORT).show();
                    }
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR" + errorMsg.msg);
                    Toast.makeText(FeedBackActivity.this, "反馈未成功", Toast.LENGTH_SHORT).show();
                }
                needHideProgress();
                submitBtn.setClickable(true);
            }
        });
    }

    //请求获取评价的标签
    private void requestGetTags() {
        Map<String, String> args = new FacadeArgs.MapBuilder().build();
        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "UnHandle", "GetFeedbackTag", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());
        Message message = new Message.MessageBuilder()
                .withProtocol(protocol)
//                .withParser(new JsonParser(new TypeToken<List<LinkedTreeMap<String, String>>>() {
//                }))
                .build();
        FacadeClient.request(this, message, new FacadeClient.FacadeCallback() {
            @Override
            public void onTokenTimeout(Message msg) {
                Toast.makeText(FeedBackActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(Message msg, Message errorMsg) {
                if (msg != null) {
//                    ResponseProtocol<List<LinkedTreeMap<String, String>>> responseProtocol = (ResponseProtocol) msg.protocol;
//                    setQueryData(responseProtocol.getResponse());
                    ResponseProtocol<String> responseProtocol = (ResponseProtocol) msg.protocol;
                    Log.e("tag", "--feedBack-getTag-->" + responseProtocol.getResponse());
                    try {
                        JSONArray array = new JSONArray(responseProtocol.getResponse());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            if (!object.getString("TAGNAME").equals("")) {
                                tagList.add(object.getString("TAGNAME"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (errorMsg != null) {
                    Log.e("reqGetAllUsers", "ERROR");
                    Log.e("tag", "---feedbackgettag--error-->" + errorMsg.msg);
                }
                tagFlowLayout.updateLayout();
            }
        });
    }
}

package com.romens.yjk.health.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.romens.android.network.FacadeArgs;
import com.romens.android.network.FacadeClient;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.ActionBar.ActionBarMenu;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.UserGuidConfig;
import com.romens.yjk.health.db.entity.UserAttributeEntity;
import com.romens.yjk.health.helper.LabelHelper;
import com.romens.yjk.health.ui.cells.TextDetailSelectCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by anlc on 2016/2/29.
 */
public class FeedBackChooseTagActivity extends BaseActivity {

    private List<String> tagList;
    private List<String> selectTagTxtList;
    private TextDetailSelectCell cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestGetTags();

        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finish();
                } else if (id == 0) {
                    Intent intent = new Intent(FeedBackChooseTagActivity.this, FeedBackNewActivity.class);
                    intent.putCharSequenceArrayListExtra("selectTagArray", (ArrayList) selectTagTxtList);
                    setResult(UserGuidConfig.RESPONSE_CHOOSETAG_TO_FEEDBACK, intent);
                    finish();
                }
            }
        });
        setContentView(content, actionBar);
        selectTagTxtList = new ArrayList<>();

        ActionBarMenu actionBarMenu = actionBar.createMenu();
        actionBarMenu.addItem(0, R.drawable.ic_done);
        actionBar.setTitle("选择标签");

        cell = new TextDetailSelectCell(this);
        cell.setBackgroundColor(getResources().getColor(R.color.white));
        content.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        cell.setMultilineDetail(true);
        updateCellValue(null);
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tagArray = tagList.toArray(new String[tagList.size()]);
                if (tagArray != null && tagArray.length > 0) {
                    showMulitChooseView(tagArray);
                } else {
                    Toast.makeText(FeedBackChooseTagActivity.this, "没有可选的标签", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateCellValue(List<String> tagList) {
        if (tagList != null) {
            CharSequence labels = LabelHelper.createChipForUserInfoLabels(tagList);
            cell.setTextAndValue("请选择标签", labels, true);
        } else {
            cell.setTextAndValue("请选择标签", "无", true);
        }
    }

    //请求获取评价的标签
    private void requestGetTags() {
        needShowProgress("正在加载...");
        tagList = new ArrayList<>();
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
                Toast.makeText(FeedBackChooseTagActivity.this, msg.msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FeedBackChooseTagActivity.this, "获取标签失败", Toast.LENGTH_SHORT).show();
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
        selectTagTxtList.clear();
        new AlertDialog.Builder(this).setMultiChoiceItems(tagList, null, new DialogInterface.OnMultiChoiceClickListener() {
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
                updateCellValue(selectTagTxtList);
                dialog.dismiss();
            }
        }).show();
    }
}

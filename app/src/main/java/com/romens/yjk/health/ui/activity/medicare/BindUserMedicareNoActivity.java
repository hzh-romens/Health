package com.romens.yjk.health.ui.activity.medicare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.romens.android.AndroidUtilities;
import com.romens.android.common.UniqueCode;
import com.romens.android.network.Message;
import com.romens.android.network.parser.JSONNodeParser;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.romens.android.ui.ActionBar.ActionBar;
import com.romens.android.ui.ActionBar.ActionBarLayout;
import com.romens.android.ui.Components.LayoutHelper;
import com.romens.yjk.health.R;
import com.romens.yjk.health.config.FacadeConfig;
import com.romens.yjk.health.config.FacadeToken;
import com.romens.yjk.health.config.ResourcesConfig;
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.ui.activity.BaseActionBarActivityWithAnalytics;
import com.romens.yjk.health.ui.components.ToastCell;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description 社会保障卡号绑定（现阶段属于哈药人民同泰 applicationId hy_rmtt）
 */
public class BindUserMedicareNoActivity extends BaseActionBarActivityWithAnalytics {
    public static final String ARGUMENTS_KEY_MEDICARE_NO = "key_medicare_no";

    private MaterialEditText inputEditText;
    private boolean isBind = true;
    private String targetMedicareCardNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(ARGUMENTS_KEY_MEDICARE_NO)) {
            targetMedicareCardNo = intent.getStringExtra(ARGUMENTS_KEY_MEDICARE_NO);
        }
        ActionBarLayout.LinearLayoutContainer content = new ActionBarLayout.LinearLayoutContainer(this);
        ActionBar actionBar = new ActionBar(this);
        content.addView(actionBar, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setContentView(content, actionBar);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    onCancel();
                }
            }
        });
        final FrameLayout inputContainer = new FrameLayout(this);
        content.addView(inputContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        inputEditText = new MaterialEditText(this);
        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputEditText.setBaseColor(0xff212121);
        inputEditText.setPrimaryColor(ResourcesConfig.textPrimary);
        inputEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        inputEditText.setMaxLines(1);
        inputEditText.setSingleLine(true);
        inputEditText.setHint("请输入社会保障卡号");
        inputEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        AndroidUtilities.setMaterialTypeface(inputEditText);
        inputContainer.addView(inputEditText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 56, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 16, 16, 8));

        TextView bindBtn = new TextView(this);
        bindBtn.setBackgroundResource(R.drawable.btn_primary);
        bindBtn.setTextColor(0xffffffff);
        bindBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        bindBtn.setClickable(true);
        bindBtn.setGravity(Gravity.CENTER);
        AndroidUtilities.setMaterialTypeface(bindBtn);
        content.addView(bindBtn, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48, 16, 32, 16, 16));

        TextView helperTextView = new TextView(this);
        helperTextView.setTextColor(0xff808080);
        helperTextView.setTextSize(20);
        helperTextView.setLineSpacing(AndroidUtilities.dp(4), 1.0f);
        helperTextView.setGravity(Gravity.CENTER);
        StringBuilder helperText = new StringBuilder();
        helperText.append("小提示");
        helperText.append("\n\n");
        helperText.append("医保支付时,使用绑定的社会保障卡作为默认付款卡");
        helperText.append("\n\n");
        helperText.append("请仔细核对社会保障卡号,避免因卡号不对导致支付失败!");
        helperText.append("\n\n");
        helperText.append("我们使用数据加密技术,保证个人信息的安全");
        helperTextView.setText(helperText);
        content.addView(helperTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 32, 32, 32, 16));

        isBind = TextUtils.isEmpty(targetMedicareCardNo);
        if (isBind) {
            actionBar.setTitle("社会保障卡绑定");
            inputEditText.setText("");
            bindBtn.setText("绑定");
        } else {
            actionBar.setTitle("修改绑定的社会保障卡");
            inputEditText.setText(targetMedicareCardNo);
            bindBtn.setText("修改");
        }

        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medicareNo = inputEditText.getText().toString();
                tryBindUserMedicareNo(medicareNo);
            }
        });

    }

    @Override
    public void onBackPressed() {
        onCancel();
    }

    private void tryBindUserMedicareNo(final String medicareNo) {
        if (TextUtils.isEmpty(medicareNo)) {
            ToastCell.toast(this, "请输入社会保障卡号!");
            inputEditText.setSelection(0);
            AndroidUtilities.showKeyboard(inputEditText);
            return;
        }
        new AlertDialog.Builder(BindUserMedicareNoActivity.this)
                .setTitle("绑定社会保障卡")
                .setMessage(String.format("是否确定绑定卡号为 %s 的社会保障卡", medicareNo))
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestInsertMedicareCard(medicareNo);
                    }
                }).setNegativeButton("取消", null)
                .create().show();

    }

    private void requestInsertMedicareCard(String medicareNo) {
        targetMedicareCardNo = medicareNo;
        Map<String, String> args = new HashMap<>();
        args.put("MEDICARENO", medicareNo);
        UserEntity client = UserSession.getInstance().get();
        String userId = client.getGuid();
        String deviceId = UniqueCode.uniqueID(this);
        String note = String.format("由用户[%s]使用设备#%s操作", userId, deviceId);
        args.put("NOTE", note);

        FacadeProtocol protocol = new FacadeProtocol(FacadeConfig.getUrl(), "Handle", "BindUserMedicareNo", args);
        protocol.withToken(FacadeToken.getInstance().getAuthToken());

        Connect connect = new RMConnect.Builder(BindUserMedicareNoActivity.class)
                .withProtocol(protocol)
                .withParser(new JSONNodeParser())
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        needHideProgress();
                        if (errorMessage == null) {
                            ResponseProtocol<JsonNode> responseProtocol = (ResponseProtocol) message.protocol;
                            JsonNode response = responseProtocol.getResponse();
                            if (!response.has("ERROR")) {
                                ToastCell.toast(BindUserMedicareNoActivity.this, isBind ? "社会保障卡绑定成功!" : "社会保障卡修改成功!");
                                onBindSuccess(targetMedicareCardNo);
                                return;
                            }
                        }
                        ToastCell.toast(BindUserMedicareNoActivity.this, isBind ? "社会保障卡绑定失败,请重试!" : "社会保障卡修改失败,请重试!");
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void onBindSuccess(String medicareNo) {
        Intent data = new Intent();
        data.putExtra("MEDICARENO", medicareNo);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}

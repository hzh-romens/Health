package com.romens.yjk.health.ui.activity.medicare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
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
import com.romens.yjk.health.core.UserSession;
import com.romens.yjk.health.db.entity.UserEntity;
import com.romens.yjk.health.pay.MedicareCard;
import com.romens.yjk.health.ui.base.DarkActionBarActivity;
import com.romens.yjk.health.ui.cells.TextInputCell;
import com.romens.yjk.health.ui.components.ToastCell;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description 社会保障卡号绑定（现阶段属于哈药人民同泰 applicationId hy_rmtt）
 */
public class BindUserMedicareNoActivity extends DarkActionBarActivity {
    public static final String ARGUMENTS_KEY_MEDICARE_ID = "key_medicare_id";
    public static final String ARGUMENTS_KEY_MEDICARE_USER = "key_medicare_user";
    public static final String ARGUMENTS_KEY_MEDICARE_CERTNO = "key_medicare_certno";
    public static final String ARGUMENTS_KEY_MEDICARE_CARDNO = "key_medicare_cardno";

    private boolean isBind = true;

    //private TextInputCell userNameCell;
    private TextInputCell certNoCell;
    private TextInputCell cardNoCell;

    private MedicareCard currMedicareCard;

    private String medicareCardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra(ARGUMENTS_KEY_MEDICARE_ID)) {
            medicareCardId = intent.getStringExtra(ARGUMENTS_KEY_MEDICARE_ID);
            String userName = intent.getStringExtra(ARGUMENTS_KEY_MEDICARE_USER);
            String certNo = intent.getStringExtra(ARGUMENTS_KEY_MEDICARE_CERTNO);
            String cardNo = intent.getStringExtra(ARGUMENTS_KEY_MEDICARE_CARDNO);
            currMedicareCard = new MedicareCard.Builder(medicareCardId)
                    .withUserName(userName)
                    .withCertNo(certNo)
                    .withCardNo(cardNo)
                    .build();
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

//        userNameCell = new TextInputCell(this);
//        content.addView(userNameCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//        userNameCell.setValue("姓名", "");
//        userNameCell.setValueHint("请输入姓名");

        certNoCell = new TextInputCell(this);
        content.addView(certNoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        certNoCell.setValue("身份证号码", "");
        certNoCell.setValueHint("请输入18位身份证号码");

        cardNoCell = new TextInputCell(this);
        content.addView(cardNoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        cardNoCell.setValue("社会保障卡号", "");
        cardNoCell.setValueHint("请输入社会保障卡号");
        cardNoCell.setHelpText("请仔细核对社会保障卡号避免支付失败!");

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
        helperText.append("我们使用数据加密技术,保证个人信息的安全");
        helperTextView.setText(helperText);
        content.addView(helperTextView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 32, 32, 32, 16));

        isBind = TextUtils.isEmpty(medicareCardId);
        if (isBind) {
            actionBar.setTitle("社会保障卡绑定");
            bindBtn.setText("绑定");
        } else {
            actionBar.setTitle("修改绑定的社会保障卡");
            bindBtn.setText("修改");

            //userNameCell.setValue("姓名", currMedicareCard.userName);
            certNoCell.setValue("身份证号码", currMedicareCard.certNo);
            cardNoCell.setValue("社会保障卡号", currMedicareCard.cardNo);
        }

        bindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryBindUserMedicareNo();
            }
        });

    }

    @Override
    public void onBackPressed() {
        onCancel();
    }

    private void tryBindUserMedicareNo() {
//        if (userNameCell.isEmpty()) {
//            ToastCell.toast(this, "请输入姓名!");
//            return;
//        }

        if (certNoCell.isEmpty()) {
            ToastCell.toast(this, "请输入身份证号!");
            return;
        }

        if (cardNoCell.isEmpty()) {
            ToastCell.toast(this, "请输入社会保障卡号!");
            return;
        }

        String certNo = certNoCell.getValue().toUpperCase();

        if (certNo.length() != 18) {
            ToastCell.toast(this, "请输入正确的身份证号!");
            return;
        }

        //String userName = userNameCell.getValue();
        String cardNo = cardNoCell.getValue().toUpperCase();

        currMedicareCard = new MedicareCard.Builder(medicareCardId)
                .withCertNo(certNo)
                .withCardNo(cardNo)
                .build();
        new AlertDialog.Builder(BindUserMedicareNoActivity.this)
                .setTitle("绑定社会保障卡")
                .setMessage(String.format("是否确定绑定%s的卡号 %s 的社会保障卡", currMedicareCard.userName, currMedicareCard.cardNo))
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestInsertMedicareCard();
                    }
                }).setNegativeButton("取消", null)
                .create().show();

    }

    private void requestInsertMedicareCard() {
        Map<String, String> args = new HashMap<>();
        args.put("USERNAME", currMedicareCard.userName);
        args.put("CERTNO", currMedicareCard.certNo);
        args.put("MEDICARENO", currMedicareCard.cardNo);
        UserEntity client = UserSession.getInstance().get();
        String userId = client.getGuid();
        String deviceId = UniqueCode.create(this);
        String note = String.format("由用户[%s]使用设备#%s执行绑定操作", userId, deviceId);
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
                                onBindSuccess();
                                return;
                            }
                        }
                        ToastCell.toast(BindUserMedicareNoActivity.this, isBind ? "社会保障卡绑定失败,请重试!" : "社会保障卡修改失败,请重试!");
                    }
                }).build();
        ConnectManager.getInstance().request(this, connect);
    }

    private void onBindSuccess() {
        Intent data = new Intent();
        data.putExtra("MEDICARE_USER", currMedicareCard.userName);
        data.putExtra("MEDICARE_CERTNO", currMedicareCard.certNo);
        data.putExtra("MEDICARE_CARDNO", currMedicareCard.cardNo);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}

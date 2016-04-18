package com.romens.yjk.health.hyrmtt.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.romens.yjk.health.R;
import com.romens.yjk.health.pay.PayAppManager;
import com.romens.yjk.health.pay.PayMode;
import com.romens.yjk.health.pay.PayModeEnum;
import com.romens.yjk.health.ui.activity.medicare.MedicareCardListActivity;
import com.romens.yjk.health.ui.activity.medicare.MedicarePayBaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhou Lisi
 * @create 16/3/2
 * @description 医保支付方式选择
 */
public class MedicarePayActivity extends MedicarePayBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreatePayMode(SparseArray<PayMode> payModes) {
        payModes.put(0, new PayMode.Builder(0)
                .withIconResId(R.drawable.medicare_pay_haerbin)
                .withName("哈尔滨银行(社保卡支付)")
                .withDesc("支持使用哈尔滨银行账户进行社保卡支付")
                .withMode(PayModeEnum.YB_HEB).build());
        defaultPayModes.clear();
        defaultPayModes.add(0);

        otherPayModes.clear();
        if (supportOtherPay) {
            payModes.put(1, new PayMode.Builder(1)
                    .withIconResId(R.drawable.ic_appwx_logo)
                    .withName("微信支付(现金支付)")
                    .withDesc("推荐微信用户使用")
                    .withMode(PayModeEnum.WX).build());

            payModes.put(2, new PayMode.Builder(2)
                    .withIconResId(R.drawable.ic_pay_alipay)
                    .withName("支付宝支付(现金支付)")
                    .withDesc("推荐支付宝用户使用")
                    .withMode(PayModeEnum.ALIPAY).build());

            otherPayModes.add(1);
            otherPayModes.add(2);
        }
        selectedPayModeId = 0;
    }

    @Override
    protected String getPayModeSectionText() {
        return "社保卡支付方式";
    }

    protected String getOtherPayModeSectionText() {
        return "其他现金支付方式";
    }

    @Override
    protected boolean hasTip() {
        return true;
    }

    @Override
    protected CharSequence getTip() {
        return "小提示：社保卡支付请确认支付使用的社保卡余额充足。可以在选择社保卡界面查看余额。";
    }

    @Override
    protected void trySendPayPrepareRequest() {
        if (selectedPayModeId == 0) {
            if (!PayAppManager.isSetupYBHEB(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("社保卡支付")
                        .setMessage("检测手机没有安装 哈尔滨银行 所需的支付客户端,是否跳转到银行官方页面下载?")
                        .setPositiveButton("现在去下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PayAppManager.needDownloadPayApp(MedicarePayActivity.this, payModes.get(0).mode);
                            }
                        }).setNegativeButton("取消", null)
                        .create().show();
                return;
            }
            needSelectMedicareCardPay();
        } else {
            sendPayPrepareRequest();
        }

    }

    private void needSelectMedicareCardPay() {
        Intent intent = new Intent(MedicarePayActivity.this, MedicareCardListActivity.class);
        intent.putExtra(MedicareCardListActivity.ARGUMENTS_KEY_ONLY_SELECT, true);
        intent.putExtra(MedicareCardListActivity.ARGUMENTS_KEY_PAY_AMOUNT, orderPayAmount.doubleValue());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String medicareCardNo = data.getStringExtra("MEDICARE_CARDNO");
                sendMedicarePayPrepareRequest(medicareCardNo);
            }
        }
    }

    private void sendMedicarePayPrepareRequest(String medicareCardNo) {
        Map<String, String> args = new HashMap<>();
        args.put("APPTYPE", "ANDROID");
        args.put("ORDERCODE", orderNo);
        args.put("MEDICARECARD", medicareCardNo);
        String payMode = payModes.get(selectedPayModeId).getPayModeKey();
        args.put("PAYMODE", payMode);
        doPayPrepareRequest(args);
    }

    @Override
    protected String getActivityName() {
        return "医保在线支付选择";
    }


//    @Override
//    protected void formatPayParamsResponse(JsonNode jsonNode) {
//        String payMode = jsonNode.get("PayMode").asText();
//        JsonNode payParamsNode = jsonNode.get("PayParams");
//
//        PayParamsForYBHEB payParams = new PayParamsForYBHEB();
//        Iterator<String> fieldNames = payParamsNode.fieldNames();
//        while (fieldNames.hasNext()) {
//            String fieldName = fieldNames.next();
//            payParams.put(fieldName, payParamsNode.get(fieldName).asText());
//        }
//        Intent intent = new Intent();
//        ComponentName componentName = new ComponentName("com.romens.yjk.health.hyrmtt",
//                "com.romens.yjk.health.hyrmtt.pay.YBPayResult");
//        intent.setComponent(componentName);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(PayActivity.ARGUMENTS_KEY_FROM_PAY_PREPARE, true);
//        Bundle pay = new Bundle();
//        pay.putString("ORDER_NO", orderNo);
//        pay.putDouble("ORDER_PAY_AMOUNT", orderPayAmount.doubleValue());
//        pay.putBundle("PAY", payParams.toBundle());
//        bundle.putBundle(PayActivity.ARGUMENTS_KEY_PAY_PARAMS, pay);
//        intent.putExtras(bundle);
//        startActivity(intent);
//        finish();
//    }

}

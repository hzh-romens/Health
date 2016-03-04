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
    protected void onInitPayMode(SparseArray<PayMode> payModes) {
        payModes.put(0, new PayMode.Builder(0)
                .withIconResId(R.drawable.medicare_pay_haerbin)
                .withName("哈尔滨银行(医保支付)")
                .withDesc("支持使用哈尔滨银行账户进行医保支付")
                .withMode(PayModeEnum.YB_HEB).build());

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
        selectedPayModeKey = 0;
    }

    @Override
    protected boolean hasTip() {
        return true;
    }

    @Override
    protected CharSequence getTip() {
        return "小提示：医保支付请确认余额充足。如果医保账户余额不足，可以使用其他在线支付方式付款购买。";
    }

    @Override
    protected void trySendPayPrepareRequest() {
        if (selectedPayModeKey == 0) {
            if (!PayAppManager.isSetupYBHEB(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("医保支付")
                        .setMessage("检测手机没有安装 哈尔滨银行 所需的支付客户端,是否跳转到银行官方页面下载?")
                        .setPositiveButton("现在去下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PayAppManager.needDownloadPayApp(MedicarePayActivity.this, medicarePayModes.get(0).mode);
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
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String medicareCardNo = data.getStringExtra("MEDICARENO");
                sendMedicarePayPrepareRequest(medicareCardNo);
            }
        }
    }

    private void sendMedicarePayPrepareRequest(String medicareCardNo) {
        Map<String, String> args = new HashMap<>();
        args.put("APPTYPE", "ANDROID");
        args.put("ORDERCODE", orderNo);
        args.put("MEDICARECARD", medicareCardNo);
        String payMode = medicarePayModes.get(selectedPayModeKey).getPayModeKey();
        args.put("PAYMODE", payMode);
        doPayPrepareRequest(args);
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

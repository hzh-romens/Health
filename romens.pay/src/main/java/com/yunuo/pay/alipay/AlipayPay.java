package com.yunuo.pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.yunuo.pay.apply.PayResult;

/**
 * Created by siery on 16/1/27.
 */
public class AlipayPay {
    private static final int SDK_PAY_FLAG = 1;
    private Delegate delegate;

    public interface Delegate {
        void onPaySuccess(Bundle extData);

        void onPayFail(Bundle extData);

        void onPayProcessing(Bundle extData);
    }

    public AlipayPay(Delegate d) {
        delegate = d;
    }

    public void sendPayRequest(final Activity context, final String payInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                payCallback.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    @SuppressLint("HandlerLeak")
    private Handler payCallback = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    Bundle resultInfoBundle = formatResultInfo(resultInfo);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (delegate != null) {
                            delegate.onPaySuccess(resultInfoBundle);
                        }
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            if (delegate != null) {
                                delegate.onPayProcessing(resultInfoBundle);
                            }
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            if (delegate != null) {
                                delegate.onPayFail(resultInfoBundle);
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private Bundle formatResultInfo(String resultInfo) {
        //_input_charset=utf-8
        // &body=三也真品牌舒肝片
        // &it_b_pay=30m
        // &notify_id=RqPnCoPT3K9%2Fvwbh3InUHnxzGTJ%2FAvSZQPIdrB6hnZiZp30V317Lel8UEcrsnevR70ac
        // &notify_time=2016-03-16 17:20:13
        // &notify_type=trade_status_sync
        // &notify_url=http://115.28.244.190/index.php/Alipay&out_trade_no=2048131458119991
        // &partner=2088701740074813
        // &payment_type=1
        // &seller_id=2088701740074813
        // &service=mobile.securitypay.pay
        // &sign=PiBo0/1GO1CErdKD0xPOcBtDbDCXj6bSwmOMtHG+8h1lwqXRsf9k9hhI654VS8amUty5fILhHIE3JvUaTItyEzkV03NcPU9YXug+TaFFYMYUTNVf6ZK/XJ0Xps7cU5s7fWFbAP+EQBA2nYMr8xQH811WypwrNwFwIB2k1Uj+ymM=
        // &sign_type=RSA&subject=三也真品牌舒肝片
        // &success=true&total_fee=0.01
        // &trade_no=2016031621001004430203611620
        // &trade_status=TRADE_SUCCESS
        Bundle bundle = new Bundle();
        bundle.putString("RESULT_INFO", resultInfo);
        String[] resultKeyValue = resultInfo.split("&");
        String[] temp;
        for (String item : resultKeyValue) {
            temp = item.split("=");
            if (temp.length == 2) {
                bundle.putString(temp[0], temp[1]);
            }
        }
        return bundle;
    }
}

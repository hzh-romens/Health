package com.yunuo.pay.wx;


import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunuo.pay.R;

import org.json.JSONObject;

public class WXActivity extends Activity {

    private IWXAPI api;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
        //需要先获取统一生成的预付订单，再通过预付订单里面的prepay_id，然后在发起支付
        Button appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                    final String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
                    Toast.makeText(WXActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //判断手机软件是否支持微信支付
                            Looper.prepare();
                            s = Util.connectionGet(url);
                            try {
                                if (s != null && !("".equals(s))) {

                                    JSONObject json = new JSONObject(s);
                                    if (null != json && !json.has("retcode")) {
                                        PayReq req = new PayReq();
                                        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                                        //req.appId = json.getString("appid");
                                        req.appId = Constants.APP_ID;
                                        //  req.partnerId = json.getString("partnerid");
                                        req.partnerId = "1309412101";
                                        //req.prepayId = json.getString("prepayid");
                                        req.prepayId = "wx2016012116472426f2c181b10961558638";
                                        //  req.nonceStr = json.getString("noncestr");
                                        req.nonceStr = "LD0TDv0OV9adtijg";
                                        req.timeStamp = json.getString("timestamp");
                                        req.packageValue = json.getString("package");
                                        //req.sign = json.getString("sign");
                                        req.sign = "12327633FBF31F44B0624281EF2E8EC3";
                                        req.extData = "app data"; // optional
                                        Toast.makeText(WXActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
                                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                        api.sendReq(req);
                                    } else {
                                        Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                                        Toast.makeText(WXActivity.this, "返回错误" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("PAY_GET", "服务器请求错误");
                                    Toast.makeText(WXActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("PAY_GET", "异常：" + e.getMessage());
                                Toast.makeText(WXActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    }).start();

                } else {
                    Toast.makeText(WXActivity.this, "您的手机不支持微信支付", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}

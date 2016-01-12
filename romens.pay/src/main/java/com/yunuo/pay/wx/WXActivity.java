package com.yunuo.pay.wx;


import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunuo.pay.R;

import org.json.JSONObject;

public class WXActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        api = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1");
        //需要先获取统一生成的预付订单，再通过预付订单里面的prepay_id，然后在发起支付

        Button appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //判断手机软件是否支持微信支付
                        Looper.prepare();
                        if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                            String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
                            //  Button payBtn = (Button) findViewById(R.id.appay_btn);
                            //   payBtn.setEnabled(false);
                            Toast.makeText(WXActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
                            String s = Util.connectionGet(url);
                            try {
                                //   byte[] buf = Util.httpGet(url);
                                //if (buf != null && buf.length > 0) {
                                if (s != null && !("".equals(s))) {
                                    // String content = new String(buf);
                                    // Log.e("get server pay params:", content);
                                    // JSONObject json = new JSONObject(content);
                                    JSONObject json = new JSONObject(s);
                                    if (null != json && !json.has("retcode")) {
                                        PayReq req = new PayReq();
                                        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                                        req.appId = json.getString("appid");
                                        req.partnerId = json.getString("partnerid");
                                        req.prepayId = json.getString("prepayid");
                                        req.nonceStr = json.getString("noncestr");
                                        req.timeStamp = json.getString("timestamp");
                                        req.packageValue = json.getString("package");
                                        req.sign = json.getString("sign");
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
                            // payBtn.setEnabled(true);
                        } else {
                            Toast.makeText(WXActivity.this, "您的手机不支持微信支付", Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    }
                }).start();

            }
        });
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.i("是否回调了","="+baseResp.getType());
    }
}

package com.yunuo.pay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunuo.pay.wx.WXPay;

public class WXPayActivity extends Activity {

    private IWXAPI wxAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);
        initWXApi();
        //需要先获取统一生成的预付订单，再通过预付订单里面的prepay_id，然后在发起支付
        Button appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wxAPI.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                    requestWXPay();
                    return;

                } else {
                    Toast.makeText(WXPayActivity.this, "您的手机不支持微信支付", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initWXApi() {
        String packageName = getPackageName();
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxAPI = WXAPIFactory.createWXAPI(this, WXPay.getInstance().getAppId());
        // 将该app注册到微信
        boolean result = wxAPI.registerApp(WXPay.getInstance().getAppId());
    }

    private void requestWXPay() {
        PayReq request = new PayReq();
        request.appId = WXPay.getInstance().getAppId();
        request.partnerId = "1309412101";
        request.prepayId = "wx20160125175953b94c67fb140718322413";
        request.packageValue = "Sign=WXPay";
        request.nonceStr = "JwB1TnCZJmAL8UFA";
        request.timeStamp = "1453715993";
        request.sign = "2A49BCEC2D9DE126C2D80528A6A57595";
        if (request.checkArgs()) {
            wxAPI.sendReq(request);
        }
    }

    private void requestWXPayCenter() {
        FacadeProtocol protocol = new FacadeProtocol("http://115.28.244.190/index.php/", "Test", "getApipay", "");
        Connect connect = new RMConnect.Builder(WXPayActivity.class)
                .withProtocol(protocol)
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message message1) {

                    }
                }).build();
        ConnectManager.getInstance().request(WXPayActivity.this, connect);
    }


}

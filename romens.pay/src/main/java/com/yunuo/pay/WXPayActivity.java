package com.yunuo.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.romens.android.network.Message;
import com.romens.android.network.protocol.FacadeProtocol;
import com.romens.android.network.protocol.ResponseProtocol;
import com.romens.android.network.request.Connect;
import com.romens.android.network.request.ConnectManager;
import com.romens.android.network.request.RMConnect;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunuo.pay.wx.WXPay;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WXPayActivity extends Activity implements IWXAPIEventHandler {
    public static final String ARGUMENTS_KEY_PREPARE_PAY = "key_prepare_pay";

    private boolean isPreparePay = false;

    private WXPay wxPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle arguments = intent.getExtras();
        if (arguments != null) {
            isPreparePay = arguments.getBoolean(ARGUMENTS_KEY_PREPARE_PAY, false);
        }

        setContentView(R.layout.pay);
        initWXApi();
        //需要先获取统一生成的预付订单，再通过预付订单里面的prepay_id，然后在发起支付
        Button appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wxPay.getWxAPI().getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                    requestWXPayCenter();
                    return;

                } else {
                    Toast.makeText(WXPayActivity.this, "您的手机不支持微信支付", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!isPreparePay) {
            wxPay.handleIntent(intent);
        }
    }

    private void initWXApi() {
        wxPay = new WXPay(this, new WXPay.Delegate() {
            @Override
            public void onPaySuccess(String extData) {

            }

            @Override
            public void onPayFail(int errorCode, String error) {

            }

            @Override
            public void onPayCancel(String extData) {

            }

            @Override
            public void onPayProcessing() {

            }
        });
    }

    protected void requestWXPayCenter() {
        onWXPayProgress(true);
        FacadeProtocol protocol = new FacadeProtocol("http://115.28.244.190/index.php/", "Test", "getApipay", "");
        Connect connect = new RMConnect.Builder(WXPayActivity.class)
                .withProtocol(protocol)
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        onWXPayProgress(false);
                        if (errorMessage == null) {
                            ResponseProtocol<String> response = (ResponseProtocol) message.protocol;
                            String jsonString = response.getResponse();
                            //{"appid":"wx0c8c0d4ae1fc56d8",
                            // "partnerid":"1309412101",
                            // "prepayid":"wx201601271951453348a983700592071833",
                            // "package":"Sign=WXPay",
                            // "noncestr":"XIEYv6XVfDTtfGKI",
                            // "t imestamp":"1453895504",
                            // "sign":"E6A17FA06A855592824E152A9CA99B25",
                            // "time":"1453895504","ordercode":"130941210120160127195144"}
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                requestWXPay(jsonObject);
                            } catch (JSONException e) {
                                onWXPayFail(PAY_ERROR_CODE_CONFIG_LOSS, "服务器微信支付配置解析错误");
                            }
                        } else {
                            onWXPayFail(PAY_ERROR_CODE_CONFIG_LOSS, errorMessage.msg);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(WXPayActivity.this, connect);
    }

    private void requestWXPay(JSONObject jsonObject) throws JSONException {
        Map<String, String> ext = new HashMap<>();
        ext.put("orderId", jsonObject.getString("ordercode"));

        WXPay.WXPayBuilder builder = new WXPay.WXPayBuilder(wxPay.getAppId())
                .withPartnerId(jsonObject.getString("partnerid"))
                .withPrepayId(jsonObject.getString("prepayid"))
                .withPackageValue("Sign=WXPay")
                .withNonceStr(jsonObject.getString("noncestr"))
                .withTimeStamp(jsonObject.getString("time"))
                .withSign(jsonObject.getString("sign"))
                .withExt(ext);
        boolean isSend = wxPay.sendPayRequest(builder);
        if (!isSend) {
            onWXPayFail(PAY_ERROR_CODE_ARGS_LOSS, "微信支付参数缺失");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        wxPay.handleIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("WXAPI", baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        PayResp payResp = (PayResp) baseResp;
        if (payResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            final int errorCode = payResp.errCode;
            String errorString = payResp.errStr;
            if (errorCode == BaseResp.ErrCode.ERR_OK) {
                String extDate = payResp.extData;
                try {
                    JSONObject jsonObject = new JSONObject(extDate);
                    Map<String, Object> args = new HashMap<>();
                    args.put("ORDERCODE", jsonObject.getString("orderId"));
                    queryWXPayResult(args);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (errorCode == BaseResp.ErrCode.ERR_COMM) {

            } else if (errorCode == BaseResp.ErrCode.ERR_USER_CANCEL) {

            } else {
                finish();
            }
        }
    }

    protected static final int PAY_ERROR_CODE_ARGS_LOSS = 100;
    protected static final int PAY_ERROR_CODE_CONFIG_LOSS = 101;

    protected void onWXPayProgress(boolean progress) {

    }

    protected void onWXPayFail(int code, String error) {

    }


    protected void queryWXPayResult(Map<String, Object> args) {
        onWXPayProgress(true);
        FacadeProtocol protocol = new FacadeProtocol("http://115.28.244.190/index.php/", "Test", "CheckOrderPay", args);
        Connect connect = new RMConnect.Builder(WXPayActivity.class)
                .withProtocol(protocol)
                .withDelegate(new Connect.AckDelegate() {
                    @Override
                    public void onResult(Message message, Message errorMessage) {
                        onWXPayProgress(false);
                        if (errorMessage == null) {
                            ResponseProtocol<String> response = (ResponseProtocol) message.protocol;
                            Log.e("WXPayResult", response.getResponse());
                        } else {
                            onWXPayFail(PAY_ERROR_CODE_CONFIG_LOSS, errorMessage.msg);
                        }
                    }
                }).build();
        ConnectManager.getInstance().request(WXPayActivity.this, connect);
    }
}

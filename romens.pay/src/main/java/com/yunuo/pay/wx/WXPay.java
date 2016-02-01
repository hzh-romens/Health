package com.yunuo.pay.wx;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunuo.pay.PayBuilder;
import com.yunuo.pay.PayType;

import java.util.Map;

/**
 * Created by siery on 16/1/25.
 */
public class WXPay implements IWXAPIEventHandler {
    public interface Delegate {
        void paySuccess(String extData);

        void payFail(int errorCode, String error);

        void payCancel(String extData);

        void payProcessing();
    }

    private final IWXAPI wxAPI;
    private final boolean isRegisterApp;
    private Delegate delegate;

    public WXPay(Context context, Delegate delegate) {
        this.delegate = delegate;
        this.wxAPI = WXAPIFactory.createWXAPI(context, getAppId());
        this.isRegisterApp = wxAPI.registerApp(getAppId());
    }

    public IWXAPI getWxAPI() {
        return wxAPI;
    }

    public String getAppId() {
        return "wx0c8c0d4ae1fc56d8";
    }

    public boolean isRegisterApp() {
        return isRegisterApp;
    }

    public void handleIntent(Intent intent) {
        wxAPI.handleIntent(intent, this);
    }

    public boolean sendPayRequest(WXPayBuilder builder) {
        if (!isRegisterApp) {
            return false;
        }
        if (builder != null) {
            PayReq payReq = builder.build();
            if (payReq != null) {
                return wxAPI.sendReq(payReq);
            }
        }
        return false;
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof PayResp) {
            PayResp payResp = (PayResp) baseResp;
            if (payResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
                final int errorCode = payResp.errCode;
                if (errorCode == BaseResp.ErrCode.ERR_OK) {
                    delegate.paySuccess(payResp.extData);
                } else if (errorCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                    delegate.payCancel(payResp.extData);
                } else {
                    delegate.payFail(errorCode, payResp.errStr);
                }
            }
        }
    }

    public static class WXPayBuilder extends PayBuilder {
        private final String appId;
        private String partnerId;
        private String prepayId;
        private String packageValue;
        private String nonceStr;
        private String timeStamp;
        private String sign;

        private Map<String, String> ext;

        public WXPayBuilder(String appId) {
            this.appId = appId;
        }

        public WXPayBuilder withPartnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public WXPayBuilder withPrepayId(String prepayId) {
            this.prepayId = prepayId;
            return this;
        }

        public WXPayBuilder withPackageValue(String packageValue) {
            this.packageValue = packageValue;
            return this;
        }

        public WXPayBuilder withNonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public WXPayBuilder withTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public WXPayBuilder withSign(String sign) {
            this.sign = sign;
            return this;
        }

        public WXPayBuilder withExt(Map<String, String> ext) {
            this.ext = ext;
            return this;
        }

        @Override
        public PayType getPayType() {
            return PayType.WXPAY;
        }

        @Override
        protected PayReq build() {
            PayReq request = new PayReq();
            request.appId = appId;
            request.partnerId = partnerId;
            request.prepayId = prepayId;
            request.packageValue = packageValue;
            request.nonceStr = nonceStr;
            request.timeStamp = timeStamp;
            request.sign = sign;
            if (ext != null && ext.size() > 0) {
                Gson gson = new Gson();
                String extData = gson.toJson(ext);
                request.extData = extData;
            }
            if (request.checkArgs()) {
                return request;
            }
            return null;
        }
    }
}

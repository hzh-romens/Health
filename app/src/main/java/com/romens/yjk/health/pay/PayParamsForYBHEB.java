package com.romens.yjk.health.pay;

import android.os.Bundle;

/**
 * @author Zhou Lisi
 * @create 16/2/26
 * @description
 */
public class PayParamsForYBHEB extends PayParams {

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("flowno", get("flowno"));
        bundle.putString("orderno", get("orderno"));
        bundle.putString("certNo", get("certNo"));
        bundle.putString("EndDate", get("EndDate"));
        bundle.putString("MerType", get("MerType"));
        bundle.putString("acctFlag", get("acctFlag"));
        bundle.putString("payAmount", get("payAmount"));
        bundle.putString("agtname", get("agtname"));
        bundle.putString("cardNo", get("cardNo"));
        bundle.putString("O2TRSN", get("O2TRSN"));
        bundle.putString("transferFlowNo", get("transferFlowNo"));
        bundle.putString("BusiPeriod", get("BusiPeriod"));
        bundle.putString("hrbbType", get("hrbbType"));
        return bundle;
    }


//    //药店app送达字段
//    private String flowno = "201512110142500013";//购药流水号
//    private String orderno = "119152523";//单据号
//    private String EndDate = "20151206203259";//结算日期
//    private String MerType = "0";//就诊类别
//    private String acctFlag = "1";//账户使用标志
//    private String payAmount = "3.15";//账户支付金额
//    private String agtname = "qwer";//经办人
//    private String cardNo = "112047217";//社保卡
//    private String O2TRSN = "0A15";//网上药店编号
//    private String transferFlowNo = "20151216022933888813";//交易流水号
//    private String BusiPeriod = "20150000";//业务周期号 默认：20150000

}

package com.romens.yjk.health.hyrmtt.ui.activity;

import android.os.Bundle;

import com.romens.yjk.health.ui.activity.CommitOrderBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Lisi
 * @create 16/2/29
 * @description
 */
public class CommitOrderActivity extends CommitOrderBaseActivity {
    private boolean isNeedMedicareCardPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<OrderPayType> onSupportOrderPayType() {
        List<OrderPayType> orderPayTypes = new ArrayList<>();
        if (isNeedMedicareCardPay) {
            orderPayTypes.add(new OrderPayType(9, "医保在线支付"));
        } else {
            orderPayTypes.add(new OrderPayType(0, "在线支付"));
            orderPayTypes.add(new OrderPayType(2, "送货上门"));
        }
        orderPayTypes.add(new OrderPayType(1, "到店自提"));
        return orderPayTypes;
    }
}

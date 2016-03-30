package com.romens.yjk.health.db.entity;

import android.os.Bundle;

import com.fasterxml.jackson.databind.JsonNode;
import com.romens.android.time.FastDateFormat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anlc on 2015/9/22.
 * 订单的实体
 */
public class OrderEntity {
    public static final String ClassId = "com.romens.yjk.health.db.entity.OrderEntity";

    public String orderId;
    public String orderStatus;
    public String memberId;
    public String orderNo;
    public String createDate;
    public String orderStatusStr;
    public BigDecimal orderPrice;
    public List<OrderGoodsEntity> goodsList = new ArrayList<>();

    public Long created;
    public Long updated;

    public OrderEntity() {

    }

    public OrderEntity(JsonNode item) {
        orderId = item.get("ORDERID").asText();
        orderStatus = item.get("ORDERSTATUS").asText();
        memberId = item.get("memberId").asText();
        orderNo = item.get("ORDERNO").asText();
        createDate = item.get("CREATEDATE").asText();

        try {
            created = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").parse(createDate).getTime();
        } catch (ParseException e) {
            created = 0l;
        }

        updated=item.get("UPDATED").asLong(0);

        orderStatusStr = item.get("ORDERSTATUSSTR").asText();
        double price = item.get("ORDERPRICE").asDouble(0);
        orderPrice = new BigDecimal(price);

        JsonNode goodsListNode = item.get("JSON");
        int size = goodsListNode.size();
        for (int i = 0; i < size; i++) {
            goodsList.add(new OrderGoodsEntity(goodsListNode.get(i)));
        }
    }

    public Bundle toBundle() {
        return null;
    }

    public static class OrderGoodsEntity {
        private String icon;
        private String name;

        public OrderGoodsEntity() {

        }

        public String getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public OrderGoodsEntity(JsonNode jsonNode) {
            icon = jsonNode.get("PICSMALL").asText();
            name = jsonNode.get("MEDICINENAME").asText();
        }
    }
}

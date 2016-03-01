package com.romens.yjk.health.db.entity;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

/**
 * @author Zhou Lisi
 * @create 16/2/27
 * @description
 */
public class ShoppingCartDataEntity {
    public static final String ClassId = "com.romens.yjk.health.db.entity.ShoppingCartEntity";

    private String id;
    private int buyCount;//数量
    private Double goodsPrice;// 商品价格
    private String createTime;// 日期
    private String name; //商品名称
    private String goodsClassName; //商品分类名称
    private String goodsURL; //商品图片url
    private String code;// 商品code
    private String detailDesc;// 商品描述
    private String spec;// 商品规格
    private String shopID;// 药店id
    private String shopName;//药店名称
    private String goodsGuid;//药品ID
    private String memberId;
    private Long created;
    private Long updated;

    public ShoppingCartDataEntity() {

    }

    public String getIcon() {
        return goodsURL;
    }

    public String getName() {
        return name;
    }

    public String getSpec() {
        return spec;
    }

    public BigDecimal getUserPrice() {
        return new BigDecimal(goodsPrice);
    }

    public BigDecimal getMarketPrice() {
        return new BigDecimal(goodsPrice);
    }

    public int getBuyCount() {
        return buyCount;
    }

    public double getBuyPrice(){
        return goodsPrice;
    }

    public String getGuid() {
        return id;
    }

    public long getCrated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }

    public String getShopID() {
        return shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public BigDecimal getSum() {
        return new BigDecimal(buyCount * goodsPrice);
    }

    public ShoppingCartDataEntity(JsonNode jsonNode) {
        id = jsonNode.get("GUID").asText();
        spec = jsonNode.get("SPEC").asText();
        name = jsonNode.get("NAME").asText();
        buyCount = jsonNode.get("BUYCOUNT").asInt();
        code = jsonNode.get("CODE").asText();
        createTime = jsonNode.get("CREATETIME").asText();

        detailDesc = jsonNode.get("DETAILDESCRIPTION").asText();
        goodsClassName = jsonNode.get("GOODSCLASSNAME").asText();
        goodsGuid = jsonNode.get("GOODSGUID").asText();
        goodsPrice = jsonNode.get("GOODSPRICE").asDouble(0);
        goodsURL = jsonNode.get("GOODURL").asText();
        shopID = jsonNode.get("SHOPID").asText();
        shopName = jsonNode.get("SHOPNAME").asText();
        memberId = jsonNode.get("memberId").asText();

        created = jsonNode.get("CREATETIME").asLong(0);
        updated = jsonNode.get("updated").asLong(0);


        //{"memberId":"5673a3bcd20bf1450419132",
        // "GUID":"56d179027a5fc",
        // "BUYCOUNT":"1",
        // "GOODSGUID":"1627",
        // "GOODSPRICE":"5.30",
        // "CREATETIME":"1456568578",
        // "GOODSCLASSNAME":"肝胆胃肠",
        // "GOODURL":"http://files.yunuo365.com/images/conew_1627_zm.jpg",
        // "CODE":"",
        // "NAME":"启脾丸",
        // "DETAILDESCRIPTION":null,
        // "SPEC":"3gX10丸",
        // "SHOPID":"16650218-B89F-4EB2-BD24-0EEA683BAB8E",
        // "SHOPNAME":"人民同泰总部"}
    }

    public void updateCount(int count) {
        buyCount = count;
    }
}

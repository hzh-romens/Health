package com.romens.yjk.health.model;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineGoodsItem {
    public final String guid;
    public final String name;

    public final BigDecimal userPrice;

    public final BigDecimal marketPrice;

    public final String shortDescription;

    public final String detailDescription;

    public final String spec;

    public final String pzwh;

    public final String smallImageUrl;

    public final String cd;

    public final String manufacturerId;

    public final int assessCount;

    public final int totalSaledCount;


    public final List<String> largeImages = new ArrayList<>();

    //shop
    public final String shopId;
    public final String shopIcon;
    public final String shopName;
    public final String shopAddress;
    public final int storeCount;

    private String shippingCostsText;
    private String salesPromotionText;

    //提示信息
    private String tipDescText;

    public MedicineGoodsItem(JsonNode jsonObject) {
        guid = jsonObject.get("GUID").asText();
        name = jsonObject.get("NAME").asText();
        userPrice = new BigDecimal(jsonObject.get("USERPRICE").asDouble(0));
        marketPrice = new BigDecimal(jsonObject.get("MARKETPRICE").asDouble(0));
        shortDescription = jsonObject.get("SHORTDESCRIPTION").asText();
        detailDescription = jsonObject.get("DETAILDESCRIPTION").asText();
        spec = jsonObject.get("SPEC").asText();
        pzwh = jsonObject.get("PZWH").asText();
        smallImageUrl = jsonObject.get("URL").asText();
        cd = jsonObject.get("CD").asText();
        manufacturerId = jsonObject.get("manufacturerId").asText();
        assessCount = jsonObject.get("ASSESSCOUNT").asInt();
        totalSaledCount = jsonObject.get("TOTLESALEDCOUNT").asInt();

        JsonNode largeImagesJson = jsonObject.get("GOODSPICS");
        if (largeImagesJson != null) {
            for (int i = 0; i < largeImagesJson.size(); i++) {
                largeImages.add(largeImagesJson.get(i).get("URL").asText());
            }
        }

        shopId = jsonObject.get("SHOPID").asText();
        shopIcon = jsonObject.get("BRANCHIMAGEPATH").asText();
        shopName = jsonObject.get("SHOPNAME").asText();
        shopAddress = jsonObject.get("SHOPADDRESS").asText();
        storeCount = jsonObject.get("STORECOUNT").asInt(0);

        if (jsonObject.has("SHOPPINGINFO")) {
            JsonNode shoppingInfoNode = jsonObject.get("SHOPPINGINFO");
            if (shoppingInfoNode.has("TRNSPORTANTDESC")) {
                shippingCostsText = shoppingInfoNode.get("TRNSPORTANTDESC").asText();
            }
            if (shoppingInfoNode.has("PROMOTIONS")) {
                salesPromotionText = shoppingInfoNode.get("PROMOTIONS").asText();
            }

            if (shoppingInfoNode.has("TIPDESC")) {
                tipDescText = shoppingInfoNode.get("TIPDESC").asText();
            }
        }
    }

    public String getShippingCostsDesc() {
        return shippingCostsText;
    }

    public boolean hasShippingCostsDesc() {
        return !TextUtils.isEmpty(shippingCostsText);
    }

    public String getSalesPromotionDesc() {
        return salesPromotionText;
    }

    public boolean hasSalesPromotionDesc() {
        return !TextUtils.isEmpty(salesPromotionText);
    }

    public String getTipDescText() {
        return tipDescText;
    }
    public boolean hasTipDescText() {
        return !TextUtils.isEmpty(tipDescText);
    }
}

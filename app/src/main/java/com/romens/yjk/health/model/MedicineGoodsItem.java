package com.romens.yjk.health.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public final String shopName;
    public final String shopAddress;
    public final int storeCount;

    public MedicineGoodsItem(JSONObject jsonObject) throws JSONException {
        guid = jsonObject.getString("GUID");
        name = jsonObject.getString("NAME");
        userPrice = new BigDecimal(jsonObject.getDouble("USERPRICE"));
        marketPrice = new BigDecimal(jsonObject.getDouble("MARKETPRICE"));
        shortDescription = jsonObject.getString("SHORTDESCRIPTION");
        detailDescription= jsonObject.getString("DETAILDESCRIPTION");
        spec = jsonObject.getString("SPEC");
        pzwh = jsonObject.getString("PZWH");
        smallImageUrl = jsonObject.getString("URL");
        cd = jsonObject.getString("CD");
        manufacturerId = jsonObject.getString("manufacturerId");
        assessCount = jsonObject.getInt("ASSESSCOUNT");
        totalSaledCount = jsonObject.getInt("TOTLESALEDCOUNT");

        JSONArray largeImagesJson = jsonObject.getJSONArray("GOODSPICS");
        if (largeImagesJson != null) {
            for (int i = 0; i < largeImagesJson.length(); i++) {
                largeImages.add(largeImagesJson.getJSONObject(i).getString("URL"));
            }
        }

        shopId = jsonObject.getString("SHOPID");
        shopName = jsonObject.getString("SHOPNAME");
        shopAddress = jsonObject.getString("SHOPADDRESS");
        storeCount = jsonObject.getInt("STORECOUNT");
    }
}

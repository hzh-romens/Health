package com.romens.yjk.health.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by siery on 15/12/15.
 */
public class MedicineSaleStoreEntity {
    public final String id;

    public final String name;

    public final String address;

    public final String shopNo;

    public final double lat;

    public final double lon;

    public final double distance;

    public final BigDecimal price;

    public final int totalSaledCount;
    public final String chainPharmacy;
    public final String pid;
    public final int storeCount;
    public final int assesscoUNT;
    public final String merchandiseId;

    public MedicineSaleStoreEntity(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("GUID");
        name = jsonObject.getString("GUID");
        address = jsonObject.getString("GUID");
        shopNo = jsonObject.getString("GUID");
        lat = jsonObject.getDouble("GUID");
        lon = jsonObject.getDouble("GUID");
        distance = jsonObject.getDouble("GUID");
        price = new BigDecimal(jsonObject.getDouble("GUID"));
        totalSaledCount = jsonObject.getInt("GUID");
        chainPharmacy = jsonObject.getString("GUID");
        pid = jsonObject.getString("GUID");
        storeCount = jsonObject.getInt("GUID");
        assesscoUNT = jsonObject.getInt("GUID");
        merchandiseId = jsonObject.getString("GUID");

    }
}

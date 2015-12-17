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

    /**
     * [{"ID":"AF7B1497-6A4F-423D-A84C-A220FB90DF76",
     * "SHOPNAME":"\u5148\u58f0\u518d\u5eb7\u8fde\u9501\u836f\u5e97-\u82b1\u56ed\u8def12\u53f7\u5e97",
     * "CHAINPHARMACY":null,
     * "PID":null,
     * "ADDRESS":"\u82b1\u56ed\u8def12\u53f7",
     * "PRICE":"15",
     * "STORECOUNT":"177",
     * "TOTLESALEDCOUNT":"4",
     * "ASSESSCOUNT":"1",
     * "MERCHANDISEID":"564003d7beeb11447035863",
     * "PHONE":null,
     * "SHOPNO":null,
     * "LAT":"32.080086",
     * "LON":"118.824234",
     * "DISTANCE":"468.528"}]
     * @param jsonObject
     * @throws JSONException
     */
    public MedicineSaleStoreEntity(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("ID");
        name = jsonObject.getString("SHOPNAME");
        address = jsonObject.getString("ADDRESS");
        shopNo = jsonObject.getString("SHOPNO");
        lat = jsonObject.getDouble("LAT");
        lon = jsonObject.getDouble("LON");
        distance = jsonObject.getDouble("DISTANCE");
        price = new BigDecimal(jsonObject.getDouble("PRICE"));
        totalSaledCount = jsonObject.getInt("TOTLESALEDCOUNT");
        chainPharmacy = jsonObject.getString("CHAINPHARMACY");
        pid = jsonObject.getString("PID");
        storeCount = jsonObject.getInt("STORECOUNT");
        assesscoUNT = jsonObject.getInt("ASSESSCOUNT");
        merchandiseId = jsonObject.getString("MERCHANDISEID");

    }
}

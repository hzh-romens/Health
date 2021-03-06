package com.romens.yjk.health.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Created by anlc on 2015/10/22.
 */
public class CollectDataEntity implements Parcelable {
    private long id;
    private String merchandiseId;//商品id
    private String medicineName;//商品名称
    private String medicineSpec;//商品规格
    private String shopId;//药店id
    private String shopName;//药店名称
    private String picBig;//药品大图
    private String picSmall;//药品小图
    private String price;//商品价格
    private String memberPrice;//会员价格
    private String assessCount;//评论总数
    private String saleCount;//销量

    private int created;
    private int updated;
    private boolean isSelect;//是否选中

    public CollectDataEntity() {
    }

    protected CollectDataEntity(Parcel in) {
        id = in.readLong();
        merchandiseId = in.readString();
        medicineName = in.readString();
        medicineSpec = in.readString();
        shopId = in.readString();
        shopName = in.readString();
        picBig = in.readString();
        picSmall = in.readString();
        price = in.readString();
        memberPrice = in.readString();
        assessCount = in.readString();
        saleCount = in.readString();
        created = in.readInt();
        updated = in.readInt();
    }

    public static final Creator<CollectDataEntity> CREATOR = new Creator<CollectDataEntity>() {
        @Override
        public CollectDataEntity createFromParcel(Parcel in) {
            return new CollectDataEntity(in);
        }

        @Override
        public CollectDataEntity[] newArray(int size) {
            return new CollectDataEntity[size];
        }
    };

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMerchandiseId() {
        return merchandiseId;
    }

    public void setMerchandiseId(String merchandiseId) {
        this.merchandiseId = merchandiseId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineSpec() {
        return medicineSpec;
    }

    public void setMedicineSpec(String medicineSpec) {
        this.medicineSpec = medicineSpec;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getPicBig() {
        return picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getPicSmall() {
        return picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(String memberPrice) {
        this.memberPrice = memberPrice;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAssessCount() {
        return assessCount;
    }

    public void setAssessCount(String assessCount) {
        this.assessCount = assessCount;
    }

    public String getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(String saleCount) {
        this.saleCount = saleCount;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public static CollectDataEntity mapToEntity(LinkedTreeMap<String, String> item) {
        CollectDataEntity entity = new CollectDataEntity();
        entity.setMerchandiseId(item.get("MERCHANDISEID"));
        entity.setMedicineName(item.get("MEDICINENAME"));
        entity.setMedicineSpec(item.get("MEDICINESPEC"));
        entity.setShopId(item.get("SHOPID"));
        entity.setShopName(item.get("SHOPNAME"));
        entity.setPicBig(item.get("PICBIG"));
        entity.setPicSmall(item.get("PICSMALL"));
        entity.setPrice(item.get("PRICE"));
        entity.setMemberPrice(item.get("MEMBERPRICE"));
        entity.setAssessCount(item.get("ASSESSCOUNT"));
        entity.setSaleCount(item.get("SALECOUNT"));
        return entity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(merchandiseId);
        dest.writeString(medicineName);
        dest.writeString(medicineSpec);
        dest.writeString(shopId);
        dest.writeString(shopName);
        dest.writeString(picBig);
        dest.writeString(picSmall);
        dest.writeString(price);
        dest.writeString(memberPrice);
        dest.writeString(assessCount);
        dest.writeString(saleCount);
        dest.writeInt(created);
        dest.writeInt(updated);
    }
}

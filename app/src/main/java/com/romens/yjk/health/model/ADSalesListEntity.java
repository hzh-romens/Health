package com.romens.yjk.health.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/8/17.
 */
public class ADSalesListEntity extends ADSalesBaseEntity {
    public final List<ADProductEntity> productList = new ArrayList<>();

    public ADSalesListEntity(String _name, String _iconUrl) {
        super(_name, _iconUrl);
    }

    public void clear(){
        productList.clear();
    }

    public void addProduct(ADProductEntity entity) {
        productList.add(entity);
    }
}

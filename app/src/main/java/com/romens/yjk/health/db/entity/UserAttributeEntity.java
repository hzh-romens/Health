package com.romens.yjk.health.db.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siery on 15/12/19.
 */
public class UserAttributeEntity {
    public final String key;
    public final String name;
    public final List<String> values = new ArrayList<>();
    public final List<String> valuesDesc = new ArrayList<>();

    public UserAttributeEntity(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public UserAttributeEntity addValue(String value, String valueDesc) {
        values.add(value);
        valuesDesc.add(valueDesc);
        return this;
    }

    public void remove(String value) {
        values.remove(value);
        valuesDesc.remove(Integer.parseInt(value));
    }

    public void clear() {
        values.clear();
        valuesDesc.clear();
    }
}

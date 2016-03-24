package com.romens.yjk.health.model;

import android.content.Context;
import android.content.Intent;

import com.romens.yjk.health.ui.HealthActivity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anlc on 2015/11/20.
 */
public class SearchResultEntity implements Serializable {
    public final String id;
    public final String name;
    public final String type;

    private Map<String, String> properties = new HashMap<>();

    public int viewType = 0;

    public SearchResultEntity(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public SearchResultEntity withViewType(int viewType) {
        this.viewType = viewType;
        return this;
    }

    public SearchResultEntity addProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public String getProperty(String key) {
        if (properties.containsKey(key)) {
            return properties.get(key);
        }
        return null;
    }

    public void onAction(Context context) {
        context.startActivity(new Intent(context, HealthActivity.class));
    }
}

package com.romens.yjk.health.db.entity;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

/**
 * Created by anlc on 2015/9/8.
 * 搜索页面的历史搜索实体
 */
public class SearchHistoryEntity implements Serializable {

    private long id;
    private String historyKeyword;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHistoryKeyword() {
        return historyKeyword;
    }

    public void setHistoryKeyword(String historyKeyword) {
        this.historyKeyword = historyKeyword;
    }

    public static SearchHistoryEntity mapToEntity(LinkedTreeMap<String, String> map){
        SearchHistoryEntity entity=new SearchHistoryEntity();
        return entity;
    }
}

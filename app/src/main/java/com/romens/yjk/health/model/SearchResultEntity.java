package com.romens.yjk.health.model;

import java.io.Serializable;

/**
 * Created by anlc on 2015/11/20.
 */
public class SearchResultEntity implements Serializable {
    public final String guid;
    public final String name;
    public final String spec;

    public SearchResultEntity(String guid, String name, String spec) {
        this.guid = guid;
        this.name = name;
        this.spec = spec;
    }
}
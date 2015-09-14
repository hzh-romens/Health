package com.romens.yjk.health.db.entity;

import android.text.TextUtils;

/**
 * Created by siery on 15/8/18.
 */
public class DrugGroupEntity {
    private String id;
    private String name;
    private String parentId;
    private int status;
    private int created;
    private int updated;
    private int sortIndex;

    public DrugGroupEntity() {
    }


    public void setId(String _id) {
        this.id = _id;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public void setParentId(String _parentId) {
        this.parentId = _parentId;
    }

    public void setStatus(int _status) {
        this.status = _status;
    }

    public void setCreated(int _created) {
        this.created = _created;
    }

    public void setUpdated(int _updated) {
        this.updated = _updated;
    }

    public void setSortIndex(int _sortIndex) {
        sortIndex = _sortIndex;
    }

    public void setSortIndex(String _sortIndex) {
        if (TextUtils.isEmpty(_sortIndex)) {
            sortIndex = 0;
        } else {
            try {
                sortIndex = Integer.parseInt(_sortIndex);
            } catch (NumberFormatException e) {
                sortIndex = 0;
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getParentId() {
        return this.parentId;
    }

    public int getStatus() {
        return this.status;
    }

    public int getCreated() {
        return this.created;
    }

    public int getUpdated() {
        return this.updated;
    }

    public int getSortIndex() {
        return sortIndex;
    }
}

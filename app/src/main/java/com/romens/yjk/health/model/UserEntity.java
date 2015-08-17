package com.romens.yjk.health.model;

/**
 * Created by siery on 15/8/17.
 */
public class UserEntity {
    public final String id;
    public final String avatarUrl;
    public final String userName;

    public UserEntity(String _id, String _avatarUrl, String _userName) {
        this.id = _id;
        this.avatarUrl = _avatarUrl;
        this.userName = _userName;
    }
}

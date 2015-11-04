package com.romens.yjk.health.db.entity;

public class UserEntity {
//TODO 增加Guid
    private int id;

    private String guid;
    private String name;
    private String avatar;
    private String phone;
    private String email;
    private String departmentId;
    private int status;

    public UserEntity() {
    }

    public UserEntity(int id) {
        this.id = id;
    }

    public UserEntity(int id, String guid, String name, String avatar, String phone, String email, String departmentId, int status) {
        this.id = id;
        this.guid = guid;
        this.name = name;
        this.avatar = avatar;
        this.phone = phone;
        this.email = email;
        this.departmentId = departmentId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    public String getName() {
        return name;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Not-null value.
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Not-null value.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Not-null value.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // KEEP METHODS - put your custom methods here

    public boolean isDeleted() {
        return status == 3;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "key=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", departmentId=" + departmentId +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity)) return false;

        UserEntity entity = (UserEntity) o;

        if (departmentId != entity.departmentId) return false;
        if (status != entity.status) return false;
        if (avatar != null ? !avatar.equals(entity.avatar) : entity.avatar != null) return false;
        if (email != null ? !email.equals(entity.email) : entity.email != null) return false;
        if (name != null ? !name.equals(entity.name) : entity.name != null)
            return false;
        if (phone != null ? !phone.equals(entity.phone) : entity.phone != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (departmentId != null ? departmentId.hashCode() : 0);
        result = 31 * result + status;
        return result;
    }
}

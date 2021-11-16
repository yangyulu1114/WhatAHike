package com.ebookfrenzy.whatahike.model;

public class User {
    private String userId;
    private String userName;
    private String preference;
    private String headUrl;

    public User(String userId, String userName, String preference) {
        this.userId = userId;
        this.userName = userName;
        this.preference = preference;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }
}

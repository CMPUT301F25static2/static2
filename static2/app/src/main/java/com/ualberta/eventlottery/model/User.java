package com.ualberta.eventlottery.model;

import java.util.Date;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private Date createdAt;

    private String favRecCenter;

    private String fcmToken;

    private String userType;




    public User() {
    }


    public User(String userId, String name, String email, String fcmToken) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = new Date();
        this.phone = "";
        this.favRecCenter = "";
        this.fcmToken = fcmToken;
    }

    public User(String userId, String name, String email, String phone, String fcmToken) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = new Date();
        this.phone = phone;
        this.favRecCenter = "";
        this.fcmToken = fcmToken;
    }

    public User(String userId, String name, String email, String phone, String fcmToken, String userType, String favRecCenter) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = new Date();
        this.phone = phone;
        this.fcmToken = fcmToken;
        this.userType = userType;
        this.favRecCenter = favRecCenter;
    }



    public User(String userId, String name, String email, String phone, String favRecCenter, String fcmToken) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = new Date();
        this.phone = phone;
        this.favRecCenter = favRecCenter;
        this.fcmToken = fcmToken;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Date getCreatedAt() { return createdAt; }
    public String getFavRecCenter() { return favRecCenter; }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Setters
    public void setUserId(String userId){this.userId = userId;}
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setFavRecCenter(String favRecCenter) { this.favRecCenter = favRecCenter; }
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    /**
     * Updates user profile information
     * @param name New name
     * @param email New email
     * @return true if update successful, false if validation fails
     */
    public boolean updateProfile(String name, String email) {
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            return false;
        }
        this.name = name.trim();
        this.email = email.trim();
        return true;
    }

    /**
     * Updates user profile with phone number
     * @param name New name
     * @param email New email
     * @param phone New phone number
     * @return true if update successful, false if validation fails
     */
    public boolean updateProfile(String name, String email, String phone) {
        if (updateProfile(name, email)) {
            this.phone = phone != null ? phone : "";
            return true;
        }
        return false;
    }
}
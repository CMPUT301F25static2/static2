package com.ualberta.eventlottery.model;

import java.util.Date;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private Date createdAt;

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = new Date();
        this.phone = "";
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

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
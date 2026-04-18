package com.yinya.crosswarr.network.models;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUserData {
    private String uid;
    private String email;
    private String name;
    private String photo;
    private String role;
    private Timestamp accountCreationDate;
    private String notificationPushToken;
    private Map<String, Object> settings;
    private List<Map<String, Object>> challenges;

    public FirebaseUserData() {
    }

    public FirebaseUserData(String uid, String email, String name, String role, Timestamp accountCreationDate, String notificationPushToken) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = role;
        this.accountCreationDate = accountCreationDate;
        this.notificationPushToken = notificationPushToken;
    }

    public FirebaseUserData(String uid, String email, String name, String photo, String role, Timestamp accountCreationDate, String notificationPushToken, Map<String, Object> settings, List<Map<String, Object>> challenges) {
        this(uid, email, name, role, accountCreationDate, notificationPushToken);
        this.photo = photo;
        this.settings = settings;
        this.challenges = challenges;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(Timestamp accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    public String getNotificationPushToken() {
        return notificationPushToken;
    }

    public void setNotificationPushToken(String notificationPushToken) {
        this.notificationPushToken = notificationPushToken;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public List<Map<String, Object>> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Map<String, Object>> challenges) {
        this.challenges = challenges;
    }

    public UserData asUserData() {
        UserData userData = new UserData(
                this.uid, this.email, this.name, this.photo, this.role, this.accountCreationDate, this.notificationPushToken, this.settings, this.challenges);
        return userData;
    }

    public HashMap<String, Object> asHashMap() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("uid", this.uid);
        hashMap.put("email", this.email);
        hashMap.put("name", this.name);
        if (this.photo != null) {
            hashMap.put("photo", this.photo);
        } else {
            hashMap.put("photo", "");
        }
        hashMap.put("role", this.role);
        hashMap.put("accountCreationDate", this.accountCreationDate);
        hashMap.put("notificationPushToken", this.notificationPushToken);
        if (this.settings != null) {
            hashMap.put("settings", this.settings);
        } else {
            hashMap.put("settings", new HashMap<String, Object>());
        }
        if (this.challenges != null) {
            hashMap.put("challenges", this.challenges);
        } else {
            hashMap.put("challenges", new ArrayList<Map<String, Object>>());
        }
        return hashMap;
    }
}

package com.example.project.Model;

import com.google.firebase.Timestamp;
import java.util.HashMap;

public class UserModel {
    private String phone;
    private String email;
    private String username;
    private String userId;
    private String fcmToken;
    private String status;
    private String website;
    private String gender;
    private Double latitude;   // Vĩ độ
    private Double longitude;  // Kinh độ


    public UserModel() { }

    // Constructor đầy đủ
    public UserModel(String phone, String email, String username, String userId, String fcmToken,
                     String status, String website, String gender, Double latitude, Double longitude) {
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.status = status;
        this.website = website;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructor từ HashMap (tiện khi lấy dữ liệu Firestore)
    public UserModel(HashMap<String, Object> userData) {
        if (userData != null) {
            this.userId = (String) userData.get("userId");
            this.username = (String) userData.get("username");
            this.phone = (String) userData.get("phone");
            this.email = (String) userData.get("email");
            this.fcmToken = (String) userData.get("fcmToken");
            this.status = (String) userData.get("status");
            this.website = (String) userData.get("website");
            this.gender = (String) userData.get("gender");

            Object lat = userData.get("latitude");
            Object lon = userData.get("longitude");
            if (lat instanceof Double) this.latitude = (Double) lat;
            if (lon instanceof Double) this.longitude = (Double) lon;
        }
    }

    // Getter & Setter
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}

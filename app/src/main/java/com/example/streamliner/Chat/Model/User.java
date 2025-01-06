package com.example.streamliner.Chat.Model;

import com.google.firebase.firestore.DocumentSnapshot;

public class User {
    private String uid;
    private String displayName;
    private String email;
    private String phone;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String uid, String displayName, String email, String phone) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static User fromFirestore(DocumentSnapshot document) {
        if (document == null) return null;

        User user = new User();
        user.setUid(document.getId());
        user.setDisplayName(document.getString("name"));
        user.setEmail(document.getString("email"));
        user.setPhone(document.getString("phone"));
        return user;
    }
}
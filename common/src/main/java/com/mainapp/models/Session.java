package com.mainapp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    private String id;  // Session ID

    @Column(nullable = false)
    private Long userId;  // User ID

    @Column(length = 45)
    private String ipAddress;  // IP Address

    @Column(columnDefinition = "TEXT")
    private String userAgent;  // User Agent

    @Column(columnDefinition = "LONGTEXT")
    private String payload;  // Payload (session data)

    @Column(nullable = false)
    private Integer lastActivity;  // Last activity timestamp

    @Column(nullable = false)
    private LocalDateTime expiry;  // Session expiry timestamp

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Integer lastActivity) {
        this.lastActivity = lastActivity;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }
}

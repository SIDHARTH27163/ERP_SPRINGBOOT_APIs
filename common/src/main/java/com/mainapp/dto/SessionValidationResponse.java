package com.mainapp.dto;

public class SessionValidationResponse {
    private boolean sessionValid;
    private String message;
    private String username;
    private String email;
    private String phone;
    private String entityType;
    private int statusCode;
    private Long userId;
    private String name;



    private  Long entityID;
    // Additional fields for EntityTable data
    private String entityTableName;
    private String entityTableDescription;
    private String entityTablePolicy;

    // Getters and Setters
    public boolean isSessionValid() {
        return sessionValid;
    }

    public void setSessionValid(boolean sessionValid) {
        this.sessionValid = sessionValid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Long getUserId() {  // Getter for userId
        return userId;
    }

    public void setUserId(Long userId) {  // Setter for userId
        this.userId = userId;
    }

    public String getName() {  // Getter for name
        return name;
    }

    public void setName(String name) {  // Setter for name
        this.name = name;
    }

    // Getters and Setters for EntityTable data
    public String getEntityTableName() {
        return entityTableName;
    }

    public void setEntityTableName(String entityTableName) {
        this.entityTableName = entityTableName;
    }

    public String getEntityTableDescription() {
        return entityTableDescription;
    }

    public void setEntityTableDescription(String entityTableDescription) {
        this.entityTableDescription = entityTableDescription;
    }

    public String getEntityTablePolicy() {
        return entityTablePolicy;
    }

    public void setEntityTablePolicy(String entityTablePolicy) {
        this.entityTablePolicy = entityTablePolicy;
    }
    public Long getEntityID() {
        return entityID;
    }

    public void setEntityID(Long entityID) {
        this.entityID = entityID;
    }
}

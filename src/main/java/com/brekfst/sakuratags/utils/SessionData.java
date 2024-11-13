package com.brekfst.sakuratags.utils;

public class SessionData {

    private final String id;  // Final to indicate that it should be set once
    private String name;
    private String displayName;
    private String description;
    private String permission;
    private String type;  // New field to indicate the type of data being set

    // Constructor to initialize id
    public SessionData(String id) {
        this.id = id;
    }

    // Setters for each tag attribute
    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    // New setter for the type field
    public void setType(String type) {
        this.type = type;
    }

    // New getter for the type field
    public String getType() {
        return type;
    }

    // Check if all fields are set
    public boolean isComplete() {
        return name != null && displayName != null && description != null && permission != null;
    }

    // Getters for each tag attribute
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }
}

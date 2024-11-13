package com.brekfst.sakuratags.data;

public class Tag {

    private final String id;
    private String name;
    private String displayName;
    private String description;
    private String permission;

    public Tag(String id, String name, String displayName, String description, String permission) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.permission = permission;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}

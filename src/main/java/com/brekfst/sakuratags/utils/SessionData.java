package com.brekfst.sakuratags.utils;

import com.brekfst.sakuratags.SakuraTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class SessionData {

    private final SakuraTags plugin;
    private final UUID playerUUID;
    private String tagId;
    private String type; // Tracks the current field being edited
    private String name;
    private String displayName;
    private String description;
    private String permission;
    private boolean isCreationMode;
    private boolean isWaitingForInput = false;
    private BukkitTask timeoutTask;

    // Constructor for creation session
    public SessionData(UUID playerUUID, SakuraTags plugin) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.isCreationMode = true;
        startTimeout();
    }

    // Constructor for edit session with tag ID
    public SessionData(String tagId, UUID playerUUID, SakuraTags plugin) {
        this.tagId = tagId;
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.isCreationMode = false;
        startTimeout();
    }

    public void setWaitingForInput(boolean waiting) {
        this.isWaitingForInput = waiting;
        if (waiting) {
            startTimeout();
        } else {
            cancelTimeout();
        }
    }

    public boolean isWaitingForInput() {
        return isWaitingForInput;
    }

    private void startTimeout() {
        cancelTimeout();
        timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getSessionManager().endSession(playerUUID);
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(ColorFormatter.prefix("Your session has timed out due to inactivity."));
            }
        }, 600L); // 30 seconds
    }

    private void cancelTimeout() {
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public boolean isCreationMode() {
        return isCreationMode;
    }

    // End session and cancel timeout
    public void endSession() {
        if (timeoutTask != null) timeoutTask.cancel();
    }

    // Other field setters...
    public void setName(String name) { this.name = name; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setDescription(String description) { this.description = description; }
    public void setPermission(String permission) { this.permission = permission; }

    // Other field getters...
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getPermission() { return permission; }
}

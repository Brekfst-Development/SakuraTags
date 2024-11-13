package com.brekfst.sakuratags.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private final Map<UUID, SessionData> activeSessions = new HashMap<>();

    // Start a session for the player, storing data in the session
    public void startSession(UUID playerUUID, SessionData sessionData) {
        activeSessions.put(playerUUID, sessionData);
    }

    // Check if the player is in an active session
    public boolean isInSession(UUID playerUUID) {
        return activeSessions.containsKey(playerUUID);
    }

    // Get session data for a player
    public SessionData getSessionData(UUID playerUUID) {
        return activeSessions.get(playerUUID);
    }

    // End a session by removing it
    public void endSession(UUID playerUUID) {
        activeSessions.remove(playerUUID);
    }
}

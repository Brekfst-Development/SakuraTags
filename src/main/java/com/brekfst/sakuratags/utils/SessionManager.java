package com.brekfst.sakuratags.utils;

import com.brekfst.sakuratags.SakuraTags;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private final Map<UUID, SessionData> activeSessions = new HashMap<>();
    private final SakuraTags plugin;

    public SessionManager(SakuraTags plugin) {
        this.plugin = plugin;
    }

    public void startSession(UUID playerUUID, SessionData sessionData) {
        if (activeSessions.containsKey(playerUUID)) {
            endSession(playerUUID);
        }
        activeSessions.put(playerUUID, sessionData);
    }

    public boolean isInSession(UUID playerUUID) {
        return activeSessions.containsKey(playerUUID);
    }

    public SessionData getSessionData(UUID playerUUID) {
        return activeSessions.get(playerUUID);
    }

    public void endSession(UUID playerUUID) {
        SessionData session = activeSessions.remove(playerUUID);
        if (session != null) {
            session.endSession();
        }
    }
}

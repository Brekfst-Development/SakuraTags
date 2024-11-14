package com.brekfst.sakuratags.listeners;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.data.Tag;
import com.brekfst.sakuratags.menus.TagCreationMenu;
import com.brekfst.sakuratags.menus.TagEditMenu;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.ColorUtil;
import com.brekfst.sakuratags.utils.SessionData;
import com.brekfst.sakuratags.utils.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.IllegalFormatException;
import java.util.UUID;

public class ChatListener implements Listener {

    private final SakuraTags plugin;

    public ChatListener(SakuraTags plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        SessionManager sessionManager = plugin.getSessionManager();

        if (sessionManager.isInSession(playerUUID)) {
            // Cancel the event for direct input handling
            event.setCancelled(true);

            SessionData sessionData = sessionManager.getSessionData(playerUUID);
            String input = event.getMessage();

            // Set fields based on the current edit type
            switch (sessionData.getType()) {
                case "name" -> sessionData.setName(input);
                case "displayName" -> sessionData.setDisplayName(input);
                case "description" -> sessionData.setDescription(input);
                case "permission" -> sessionData.setPermission(input);
            }

            // Clear waiting input flag and cancel timeout
            sessionData.setWaitingForInput(false);

            // Reopen the appropriate menu
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (sessionData.isCreationMode()) {
                    new TagCreationMenu(plugin.getPlayerMenuUtility(player), plugin).open();
                } else {
                    Tag tag = plugin.getTagStorage().getTag(sessionData.getTagId());
                    if (tag != null) {
                        new TagEditMenu(plugin.getPlayerMenuUtility(player), plugin, tag.getId()).open();
                    } else {
                        player.sendMessage(ColorFormatter.prefix("&cError: Unable to reopen the tag edit menu. Tag not found."));
                    }
                }
            });
        } else {
            // Apply chat formatting if enabled in the config
            if (plugin.getConfig().getBoolean("format_chat.enabled")) {
                event.setFormat(formatChatMessage(player, event.getMessage()));
            }
        }
    }

    private String formatChatMessage(Player player, String message) {
        // Retrieve the format from config or use a default
        String format = plugin.getConfig().getString("format_chat.format", "{sakura_tag} {player}: {message}");

        // Get the player's active tag (if any)
        Tag playerTag = plugin.getTagStorage().getPlayerTag(player.getUniqueId());
        String sakuraTag = playerTag != null ? ColorUtil.parseColors(playerTag.getDisplayName()) + "&r" : "";

        // Replace the placeholders
        format = format
                .replace("{sakura_tag}", sakuraTag.isEmpty() ? "" : sakuraTag + " ")
                .replace("{player}", player.getName())
                .replace("{message}", message);

        // Escape any remaining '%' symbols in the format string
        format = format.replace("%", "%%");

        try {
            return ColorUtil.parseColors(format);
        } catch (IllegalFormatException e) {
            plugin.getLogger().severe("Error formatting chat message: " + e.getMessage());
            return message; // Fallback to raw message on error
        }
    }
}
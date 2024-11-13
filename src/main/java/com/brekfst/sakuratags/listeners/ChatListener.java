package com.brekfst.sakuratags.listeners;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.data.Tag;
import com.brekfst.sakuratags.menus.TagCreationMenu;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.SessionData;
import com.brekfst.sakuratags.utils.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

        // If the player is in a session, handle the input
        if (sessionManager.isInSession(playerUUID)) {
            event.setCancelled(true);

            SessionData sessionData = sessionManager.getSessionData(playerUUID);
            String input = event.getMessage();

            if (sessionData.getName() == null) {
                sessionData.setName(input);
                player.sendMessage("Name set! Please set the next field.");
            } else if (sessionData.getDisplayName() == null) {
                sessionData.setDisplayName(input);
                player.sendMessage("Display Name set! Please set the next field.");
            } else if (sessionData.getDescription() == null) {
                sessionData.setDescription(input);
                player.sendMessage("Description set! Please set the next field.");
            } else if (sessionData.getPermission() == null) {
                sessionData.setPermission(input);
                player.sendMessage("Permission set! You can now save the tag.");
            }

            // Schedule the menu opening on the main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                new TagCreationMenu(plugin.getPlayerMenuUtility(player), plugin).open();
            });
        } else {
            // Check if chat formatting is enabled and format the chat message
            if (plugin.getConfig().getBoolean("format_chat.enabled")) {
                event.setFormat(formatChatMessage(player, event.getMessage()));
            }
        }
    }

    private String formatChatMessage(Player player, String message) {
        // Get the format from config or use default
        String format = plugin.getConfig().getString("format_chat.format", "{sakura_tag} %1$s: %2$s");

        // Get the player's active tag (if any)
        Tag playerTag = plugin.getTagStorage().getPlayerTag(player.getUniqueId());
        String sakuraTag = playerTag != null ? playerTag.getDisplayName() : ""; // Use the tag display name or empty if none

        // Replace placeholder with actual tag, if present
        format = format.replace("{sakura_tag}", sakuraTag);

        // Format the final message with player name and message
        return ColorFormatter.parse(String.format(format, player.getName(), message));
    }

}
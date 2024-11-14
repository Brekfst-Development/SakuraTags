package com.brekfst.sakuratags.listeners;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.menus.Menu;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.SessionData;
import com.brekfst.sakuratags.utils.SessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class MenuListener implements Listener {

    private final SakuraTags plugin;

    public MenuListener(SakuraTags plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof Menu) {
            event.setCancelled(true);
            Menu menu = (Menu) holder;
            menu.handleMenu(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        SessionManager sessionManager = plugin.getSessionManager();

        if (sessionManager.isInSession(playerUUID)) {
            SessionData sessionData = sessionManager.getSessionData(playerUUID);

            // Only end the session if the player is not actively entering a value
            if (!sessionData.isWaitingForInput()) {
                sessionManager.endSession(playerUUID);
                player.sendMessage(ColorFormatter.prefix("Session ended because you closed the menu."));
            }
        }
    }
}

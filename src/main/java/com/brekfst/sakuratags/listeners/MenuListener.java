package com.brekfst.sakuratags.listeners;

import com.brekfst.sakuratags.menus.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        InventoryHolder holder = event.getInventory().getHolder();

        // Check if the inventory holder is a custom menu
        if (holder instanceof Menu) {
            event.setCancelled(true); // Prevent any item movement in the custom menu immediately

            Menu menu = (Menu) holder;
            menu.handleMenu(event); // Delegate handling to the menu's custom method
        }
    }
}
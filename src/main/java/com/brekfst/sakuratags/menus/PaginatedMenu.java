package com.brekfst.sakuratags.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected final List<Object> items = new ArrayList<>(); // Stores items to paginate

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        switch (event.getSlot()) {
            case 45: // Previous Page
                if (page > 0) {
                    page--;
                    setMenuItems();
                }
                break;
            case 53: // Next Page
                if ((page + 1) * 28 < items.size()) {
                    page++;
                    setMenuItems();
                }
                break;
            default:
                handleSpecificMenuClick(event);
                break;
        }
    }

    protected abstract void handleSpecificMenuClick(InventoryClickEvent event);
}

package com.brekfst.sakuratags.menus;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.data.Tag;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.SessionData;
import com.brekfst.sakuratags.utils.SessionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TagEditMenu extends Menu {

    private final SakuraTags plugin;
    private final String tagId;

    public TagEditMenu(PlayerMenuUtility playerMenuUtility, SakuraTags plugin, String tagId) {
        super(playerMenuUtility);
        this.plugin = plugin;
        this.tagId = tagId;
    }

    @Override
    public String getMenuName() {
        Tag tag = plugin.getTagStorage().getTag(tagId);
        return ColorFormatter.parse("&d&lEdit Tag - " + (tag != null ? tag.getDisplayName() : "Unknown Tag"));
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void setMenuItems() {
        Tag tag = plugin.getTagStorage().getTag(tagId);
        if (tag == null) return;

        inventory.setItem(10, makeItem(Material.NAME_TAG, "&e&lEdit Name", "&7Current: " + tag.getName()));
        inventory.setItem(12, makeItem(Material.WRITABLE_BOOK, "&e&lEdit Display Name", "&7Current: " + tag.getDisplayName()));
        inventory.setItem(14, makeItem(Material.PAPER, "&e&lEdit Description", "&7Current: " + tag.getDescription()));
        inventory.setItem(16, makeItem(Material.PAPER, "&e&lEdit Permission", "&7Current: " + tag.getPermission()));
        inventory.setItem(22, makeItem(Material.GREEN_DYE, "&a&lSave Changes", "&7Click to save the edited tag."));
        setFillerGlass();
    }

    public void setFillerGlass() {
        ItemStack fillerItem = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i != 22 && inventory.getItem(i) == null) {
                inventory.setItem(i, fillerItem);
            }
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = playerMenuUtility.getOwner();
        UUID playerUUID = player.getUniqueId();
        SessionManager sessionManager = plugin.getSessionManager();
        SessionData session = sessionManager.getSessionData(playerUUID);

        // Ensure session data is initialized
        if (session == null) {
            session = new SessionData(tagId, playerUUID, plugin);
            sessionManager.startSession(playerUUID, session);
        }

        switch (event.getSlot()) {
            case 10 -> {
                session.setType("name");
                session.setWaitingForInput(true);
                player.sendMessage("Please enter the new tag name in chat.");
                player.closeInventory();
            }
            case 12 -> {
                session.setType("displayName");
                session.setWaitingForInput(true);
                player.sendMessage("Please enter the new display name in chat.");
                player.closeInventory();
            }
            case 14 -> {
                session.setType("description");
                session.setWaitingForInput(true);
                player.sendMessage("Please enter the new description in chat.");
                player.closeInventory();
            }
            case 16 -> {
                session.setType("permission");
                session.setWaitingForInput(true);
                player.sendMessage("Please enter the new permission in chat.");
                player.closeInventory();
            }
            case 22 -> {
                // Update the tag with current session data
                plugin.getTagStorage().updateTag(tagId, session);
                player.sendMessage(ColorFormatter.prefix("&aTag updated successfully!"));
                sessionManager.endSession(playerUUID);
                new MainAdminMenu(playerMenuUtility, plugin).open();
            }
            default -> {}
        }
    }
}

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

public class TagCreationMenu extends Menu {

    private final SakuraTags plugin;

    public TagCreationMenu(PlayerMenuUtility playerMenuUtility, SakuraTags plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return ColorFormatter.parse("&d&lCreate New Tag");
    }

    @Override
    public int getSlots() {
        return 27;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(10, makeItem(Material.NAME_TAG, "&e&lSet Name", "&7Click to set the tag's name."));
        inventory.setItem(12, makeItem(Material.WRITABLE_BOOK, "&e&lSet Display Name", "&7Click to set the tag's display name."));
        inventory.setItem(14, makeItem(Material.PAPER, "&e&lSet Description", "&7Click to set the tag's description."));
        inventory.setItem(16, makeItem(Material.PAPER, "&e&lSet Permission", "&7Click to set the tag's permission node."));
        inventory.setItem(22, makeItem(Material.GREEN_DYE, "&a&lSave Tag", "&7Click to save the new tag."));
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
        SessionData sessionData = sessionManager.getSessionData(playerUUID);

        if (sessionData == null) {
            sessionData = new SessionData(playerUUID, plugin);
            sessionManager.startSession(playerUUID, sessionData);
        }

        switch (event.getSlot()) {
            case 10 -> {
                sessionData.setType("name");
                sessionData.setWaitingForInput(true);
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's name in chat:"));
                player.closeInventory();
            }
            case 12 -> {
                sessionData.setType("displayName");
                sessionData.setWaitingForInput(true);
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's display name in chat:"));
                player.closeInventory();
            }
            case 14 -> {
                sessionData.setType("description");
                sessionData.setWaitingForInput(true);
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's description in chat:"));
                player.closeInventory();
            }
            case 16 -> {
                sessionData.setType("permission");
                sessionData.setWaitingForInput(true);
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's permission in chat:"));
                player.closeInventory();
            }
            case 22 -> {
                String tagId = sessionData.getTagId() != null ? sessionData.getTagId() : generateUniqueId();
                Tag tag = new Tag(
                        tagId,
                        sessionData.getName() != null ? sessionData.getName() : "Default Name",
                        sessionData.getDisplayName() != null ? sessionData.getDisplayName() : "Default Display Name",
                        sessionData.getDescription() != null ? sessionData.getDescription() : "Default Description",
                        sessionData.getPermission() != null ? sessionData.getPermission() : "default.permission"
                );

                if (sessionData.isCreationMode()) {
                    plugin.getTagStorage().addTag(tag);
                    player.sendMessage(ColorFormatter.prefix("&aNew tag created successfully!"));
                } else {
                    plugin.getTagStorage().updateTag(tagId, sessionData);
                    player.sendMessage(ColorFormatter.prefix("&aTag updated successfully!"));
                }

                sessionManager.endSession(playerUUID);
                new MainAdminMenu(playerMenuUtility, plugin).open();
            }
        }
    }
}

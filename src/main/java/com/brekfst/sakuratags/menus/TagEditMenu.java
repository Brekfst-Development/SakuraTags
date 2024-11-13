package com.brekfst.sakuratags.menus;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.data.Tag;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.SessionData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TagEditMenu extends Menu {

    private final SakuraTags plugin;
    private final Tag tag;

    public TagEditMenu(PlayerMenuUtility playerMenuUtility, SakuraTags plugin, Tag tag) {
        super(playerMenuUtility);
        this.plugin = plugin;
        this.tag = tag;
    }

    @Override
    public String getMenuName() {
        return ColorFormatter.parse("&d&lEdit Tag - " + tag.getDisplayName());
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(19, makeItem(Material.NAME_TAG, "&e&lEdit Name", "&7Current: " + tag.getName()));
        inventory.setItem(21, makeItem(Material.WRITABLE_BOOK, "&e&lEdit Display Name", "&7Current: " + tag.getDisplayName()));
        inventory.setItem(23, makeItem(Material.PAPER, "&e&lEdit Description", "&7Current: " + tag.getDescription()));
        inventory.setItem(25, makeItem(Material.PAPER, "&e&lEdit Permission", "&7Current: " + tag.getPermission()));
        inventory.setItem(49, makeItem(Material.GREEN_DYE, "&a&lSave Changes", "&7Click to save the edited tag."));
        setFillerGlass();
    }

    public void setFillerGlass() {
        ItemStack fillerItem = makeItem(Material.BLACK_STAINED_GLASS_PANE, " "); // Empty name for aesthetic
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerItem); // Set filler item in empty slots
            }
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = playerMenuUtility.getOwner();
        UUID playerUUID = player.getUniqueId();

        // Pass the tag's ID to SessionData
        SessionData session = new SessionData(tag.getId());

        switch (event.getSlot()) {
            case 19:
                session.setType("name");
                plugin.getSessionManager().startSession(playerUUID, session);
                player.sendMessage("Please enter the new tag name in chat.");
                player.closeInventory();
                break;
            case 21:
                session.setType("displayName");
                plugin.getSessionManager().startSession(playerUUID, session);
                player.sendMessage("Please enter the new display name in chat.");
                player.closeInventory();
                break;
            case 23:
                session.setType("description");
                plugin.getSessionManager().startSession(playerUUID, session);
                player.sendMessage("Please enter the new description in chat.");
                player.closeInventory();
                break;
            case 25:
                session.setType("permission");
                plugin.getSessionManager().startSession(playerUUID, session);
                player.sendMessage("Please enter the new permission in chat.");
                player.closeInventory();
                break;
            case 49:
                if (session.isComplete()) {
                    plugin.getTagStorage().updateTag(tag.getId(), session); // Updated to use getTagStorage() directly
                    player.sendMessage(ColorFormatter.parse("&aTag updated successfully!"));
                    new MainAdminMenu(playerMenuUtility, plugin).open();
                } else {
                    player.sendMessage(ColorFormatter.parse("&cPlease complete all fields before saving."));
                }
                break;

            default:
                break;
        }
    }
}

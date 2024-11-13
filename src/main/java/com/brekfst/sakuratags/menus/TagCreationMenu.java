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
        return 54;
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString(); // Generates a random unique ID
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(19, makeItem(Material.NAME_TAG, "&e&lSet Name", "&7Click to set the tag's name."));
        inventory.setItem(21, makeItem(Material.WRITABLE_BOOK, "&e&lSet Display Name", "&7Click to set the tag's display name."));
        inventory.setItem(23, makeItem(Material.PAPER, "&e&lSet Description", "&7Click to set the tag's description."));
        inventory.setItem(25, makeItem(Material.PAPER, "&e&lSet Permission", "&7Click to set the tag's permission node."));
        inventory.setItem(49, makeItem(Material.GREEN_DYE, "&a&lSave Tag", "&7Click to save the new tag."));
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
        SessionManager sessionManager = plugin.getSessionManager();

        // Create a new session if it doesn't exist
        if (!sessionManager.isInSession(playerUUID)) {
            sessionManager.startSession(playerUUID, new SessionData(null)); // Pass null or temporary ID if needed
        }

        SessionData sessionData = sessionManager.getSessionData(playerUUID);

        switch (event.getSlot()) {
            case 19:
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's name in chat:"));
                player.closeInventory();
                break;
            case 21:
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's display name in chat:"));
                player.closeInventory();
                break;
            case 23:
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's description in chat:"));
                player.closeInventory();
                break;
            case 25:
                player.sendMessage(ColorFormatter.prefix("&aPlease enter the tag's permission in chat:"));
                player.closeInventory();
                break;
            case 49:
                if (sessionData.isComplete()) {
                    String newId = generateUniqueId(); // Implement a method to generate a unique ID for new tags
                    Tag newTag = new Tag(newId, sessionData.getName(), sessionData.getDisplayName(),
                            sessionData.getDescription(), sessionData.getPermission());

                    // Directly add the tag via TagStorage
                    plugin.getTagStorage().addTag(newTag);  // Now add directly to TagStorage

                    player.sendMessage(ColorFormatter.prefix("&aTag created successfully!"));
                    sessionManager.endSession(playerUUID);

                    new MainAdminMenu(playerMenuUtility, plugin).open();  // Return to the main admin menu
                } else {
                    player.sendMessage(ColorFormatter.prefix("&cPlease complete all fields before saving."));
                }
                break;

            default:
                break;
        }
    }


}

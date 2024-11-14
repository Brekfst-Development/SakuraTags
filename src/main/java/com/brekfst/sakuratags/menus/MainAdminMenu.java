package com.brekfst.sakuratags.menus;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.data.Tag;
import com.brekfst.sakuratags.data.TagStorage;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MainAdminMenu extends Menu {

    private final SakuraTags plugin;
    private final TagStorage tagStorage;
    private int page = 0;
    private static final int MAX_ITEMS_PER_PAGE = 27;

    public MainAdminMenu(PlayerMenuUtility playerMenuUtility, SakuraTags plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
        this.tagStorage = plugin.getTagStorage();
    }

    @Override
    public String getMenuName() {
        return ColorFormatter.parse("&d&lAdmin Tag Management");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        setFillerBorder();

        // Create New Tag Button
        ItemStack createTag = makeItem(Material.ANVIL, "&e&lCreate New Tag", "&7Click to create a new tag.");
        inventory.setItem(50, createTag);

        // Tag listing with pagination
        List<Tag> tags = new ArrayList<>(tagStorage.getTags().values());
        int startIndex = page * MAX_ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + MAX_ITEMS_PER_PAGE, tags.size());

        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            Tag tag = tags.get(i);
            ItemStack tagItem = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = tagItem.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ColorUtil.parseColors(tag.getDisplayName()));

                // Set lore with click instructions
                List<String> lore = new ArrayList<>();
                lore.add(ColorUtil.parseColors("&7Description: " + tag.getDescription()));
                lore.add(ColorUtil.parseColors("&7Permission: " + tag.getPermission()));
                lore.add("");
                lore.add(ColorUtil.parseColors("&aClick to edit this tag"));
                lore.add(ColorUtil.parseColors("&cShift-Click to delete this tag"));
                meta.setLore(lore);

                tagItem.setItemMeta(meta);
            }

            inventory.setItem(slot++, tagItem);
        }

        // Pagination Controls
        if (page > 0) {
            inventory.setItem(45, makeItem(Material.ARROW, "&6&lPrevious Page"));
        }
        if (endIndex < tags.size()) {
            inventory.setItem(53, makeItem(Material.ARROW, "&6&lNext Page"));
        }

        // Close Button
        inventory.setItem(49, makeItem(Material.BARRIER, "&c&lClose"));
    }

    private void setFillerBorder() {
        ItemStack fillerItem = makeItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, fillerItem);         // Top border
            inventory.setItem(45 + i, fillerItem);    // Bottom border
        }

        // Left and right columns
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, fillerItem);         // Left border
            inventory.setItem(i + 8, fillerItem);     // Right border
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 50:
                new TagCreationMenu(playerMenuUtility, plugin).open();
                break;
            case 45:
                if (page > 0) {
                    page--;
                    open();
                }
                break;
            case 49:
                player.closeInventory();
                break;
            case 53:
                int maxPages = (int) Math.ceil((double) tagStorage.getTags().size() / MAX_ITEMS_PER_PAGE);
                if (page + 1 < maxPages) {
                    page++;
                    open();
                }
                break;
            default:
                if (slot >= 10 && slot < 10 + MAX_ITEMS_PER_PAGE) {
                    handleTagClick(event, slot);
                }
                break;
        }
    }

    private void handleTagClick(InventoryClickEvent event, int slot) {
        int tagIndex = page * MAX_ITEMS_PER_PAGE + (slot - 10);
        List<Tag> tags = new ArrayList<>(tagStorage.getTags().values());

        if (tagIndex < tags.size()) {
            Tag selectedTag = tags.get(tagIndex);

            if (event.isShiftClick()) {
                // Shift-click to delete tag
                tagStorage.removeTag(selectedTag.getId());
                event.getWhoClicked().sendMessage(ColorUtil.parseColors("&cTag " + selectedTag.getDisplayName() + "&c has been deleted."));
                setMenuItems();
            } else {
                // Normal click to open edit menu
                new TagEditMenu(playerMenuUtility, plugin, selectedTag.getId()).open();
            }
        }
    }

}
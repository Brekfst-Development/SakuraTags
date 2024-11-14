package com.brekfst.sakuratags.menus;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.data.Tag;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TagsMenu extends Menu {

    private final SakuraTags plugin;
    private int page = 0;
    private static final int TAGS_PER_PAGE = 28;

    public TagsMenu(PlayerMenuUtility playerMenuUtility, SakuraTags plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return ColorUtil.parseColors(plugin.getConfig().getString("gui.title", "&d&lSelect a Tag!"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        setFillerGlass();

        int[] tagSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42};
        Map<String, Tag> tags = plugin.getTagStorage().getTags();
        List<Tag> tagList = new ArrayList<>(tags.values());
        int start = page * TAGS_PER_PAGE;
        int end = Math.min(start + TAGS_PER_PAGE, tagList.size());
        Player player = playerMenuUtility.getOwner();

        for (int i = start, slotIndex = 0; i < end && slotIndex < tagSlots.length; i++, slotIndex++) {
            Tag tag = tagList.get(i);
            ItemStack item;

            // Perform permission check
            if (player.hasPermission(tag.getPermission()) || player.hasPermission("sakuratags.admin") || player.isOp()) {
                item = createTagItem(tag);  // Show selectable tag item
            } else {
                item = createRestrictedItem();  // Show restricted tag item
            }

            NamespacedKey key = new NamespacedKey(plugin, "tag-id");
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tag.getId());
                item.setItemMeta(meta);
            }

            inventory.setItem(tagSlots[slotIndex], item);
        }

        // Pagination Controls
        if (page > 0) {
            inventory.setItem(45, createNavigationItem("gui.previous_page", "&6&lPrevious Page"));
        }
        if (end < tagList.size()) {
            inventory.setItem(53, createNavigationItem("gui.next_page", "&6&lNext Page"));
        }

        // Player head in the center top row
        inventory.setItem(4, createPlayerHead(player));
    }

    private ItemStack createPlayerHead(Player player) {
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();

        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName(ColorUtil.parseColors("&aYour Tag"));

            Tag currentTag = plugin.getTagStorage().getPlayerTag(player.getUniqueId());
            List<String> lore = new ArrayList<>();
            if (currentTag != null) {
                lore.add(ColorUtil.parseColors("&7Current Tag: " + currentTag.getDisplayName()));
                lore.add(ColorUtil.parseColors("&eClick to remove your current tag."));
            } else {
                lore.add(ColorUtil.parseColors("&7No tag applied"));
            }

            skullMeta.setLore(lore);
            headItem.setItemMeta(skullMeta);
        }

        return headItem;
    }


    private ItemStack createTagItem(Tag tag) {
        // Fetch material and set item based on config
        Material material = Material.valueOf(plugin.getConfig().getString("gui.tag_select_item.material", "NAME_TAG"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Set display name and lore from config
            meta.setDisplayName(ColorUtil.parseColors(tag.getDisplayName()));
            List<String> lore = plugin.getConfig().getStringList("gui.tag_select_item.lore").stream()
                    .map(line -> ColorUtil.parseColors(line.replace("%tag_description%", tag.getDescription())))
                    .toList();
            meta.setLore(lore);

            // Store tag ID in the item’s PersistentDataContainer for later retrieval
            NamespacedKey key = new NamespacedKey(plugin, "tag-id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tag.getId());

            item.setItemMeta(meta);
        }
        return item;
    }


    private ItemStack createRestrictedItem() {
        Material material = Material.valueOf(plugin.getConfig().getString("gui.restricted_item.material", "GRAY_DYE"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtil.parseColors(plugin.getConfig().getString("gui.restricted_item.displayname", "&cLocked Tag")));
            List<String> lore = plugin.getConfig().getStringList("gui.restricted_item.lore").stream()
                    .map(ColorUtil::parseColors)
                    .toList();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNavigationItem(String configPath, String defaultName) {
        Material material = Material.valueOf(plugin.getConfig().getString(configPath + ".material", "PAPER"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtil.parseColors(plugin.getConfig().getString(configPath + ".displayname", defaultName)));
            List<String> lore = plugin.getConfig().getStringList(configPath + ".lore").stream()
                    .map(ColorUtil::parseColors)
                    .toList();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void setFillerGlass() {
        Material material = Material.valueOf(plugin.getConfig().getString("gui.filler_item.material", "MAGENTA_STAINED_GLASS_PANE"));
        ItemStack fillerItem = new ItemStack(material);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, fillerItem);
            inventory.setItem(45 + i, fillerItem);
        }

        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, fillerItem);
            inventory.setItem(i + 8, fillerItem);
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = playerMenuUtility.getOwner();
        int slot = e.getRawSlot();

        if (slot == 4) { // Player head slot for removing tag
            Tag currentTag = plugin.getTagStorage().getPlayerTag(player.getUniqueId());
            if (currentTag != null) {
                plugin.getTagStorage().removePlayerTag(player.getUniqueId());
                player.sendMessage(ColorUtil.parseColors("&aYour current tag has been removed."));
                setMenuItems();
                player.openInventory(inventory);
            }
            return;
        }

        if (slot == 45 && page > 0) {
            page--;
            setMenuItems();
            player.openInventory(inventory);
        } else if (slot == 53 && (page + 1) * TAGS_PER_PAGE < plugin.getTagStorage().getTags().size()) {
            page++;
            setMenuItems();
            player.openInventory(inventory);
        } else {
            ItemStack clicked = e.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta()) {
                ItemMeta meta = clicked.getItemMeta();
                NamespacedKey key = new NamespacedKey(plugin, "tag-id");

                if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    String tagId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    Tag tag = plugin.getTagStorage().getTag(tagId);

                    // Double-check permissions on selection
                    if (tag != null && (player.hasPermission("sakuratags.admin") || player.isOp() || player.hasPermission(tag.getPermission()))) {
                        plugin.getTagStorage().setPlayerTag(player.getUniqueId(), tag);
                        player.sendMessage(ColorUtil.parseColors("&aTag set to: " + tag.getDisplayName()));
                        setMenuItems();
                        player.openInventory(inventory);
                    } else {
                        player.sendMessage(ColorUtil.parseColors("&cYou do not have permission to use this tag."));
                    }
                }
            }
        }
    }
}

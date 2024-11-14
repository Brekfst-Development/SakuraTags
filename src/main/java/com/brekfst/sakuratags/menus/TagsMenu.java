package com.brekfst.sakuratags.menus;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.data.Tag;
import com.brekfst.sakuratags.utils.ColorUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // Define the slots where tags will be placed
        int[] tagSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42};

        Map<String, Tag> tags = plugin.getTagStorage().getTags();
        List<Tag> tagList = new ArrayList<>(tags.values());
        int start = page * TAGS_PER_PAGE;
        int end = Math.min(start + TAGS_PER_PAGE, tagList.size());

        Player player = playerMenuUtility.getOwner();

        for (int i = start, slotIndex = 0; i < end && slotIndex < tagSlots.length; i++, slotIndex++) {
            Tag tag = tagList.get(i);

            // Create item for each tag
            ItemStack item;
            if (player.hasPermission(tag.getPermission()) || player.hasPermission("sakuratags.admin") || player.isOp()) {
                item = createTagItem(tag, "gui.tag_select_item");
            } else {
                item = createRestrictedItem();
            }

            // Store the tag's UUID in the item's PersistentDataContainer
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                NamespacedKey key = new NamespacedKey(plugin, "tag-id");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, tag.getId());
                item.setItemMeta(meta);
            }

            inventory.setItem(tagSlots[slotIndex], item);
        }

        // Pagination controls
        if (page > 0) {
            inventory.setItem(45, createNavigationItem("gui.previous_page"));
        }
        if (end < tagList.size()) {
            inventory.setItem(53, createNavigationItem("gui.next_page"));
        }

        // Display current tag
        Tag currentTag = plugin.getTagStorage().getPlayerTag(player.getUniqueId());
        if (currentTag != null) {
            inventory.setItem(49, createCurrentTagItem(currentTag));
        }
    }


    private ItemStack createTagItem(Tag tag, String configPath) {
        Material material = Material.getMaterial(plugin.getConfig().getString(configPath + ".material", "NAME_TAG"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.parseColors(plugin.getConfig().getString(configPath + ".displayname", "%tag_displayname%").replace("%tag_displayname%", tag.getDisplayName())));
            List<String> lore = new ArrayList<>();
            for (String loreLine : plugin.getConfig().getStringList(configPath + ".lore")) {
                lore.add(ColorUtil.parseColors(loreLine.replace("%tag_description%", tag.getDescription())));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createRestrictedItem() {
        Material material = Material.getMaterial(plugin.getConfig().getString("gui.restricted_item.material", "GRAY_DYE"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.parseColors(plugin.getConfig().getString("gui.restricted_item.displayname", "Locked Tag")));
            List<String> lore = new ArrayList<>();
            for (String loreLine : plugin.getConfig().getStringList("gui.restricted_item.lore")) {
                lore.add(ColorUtil.parseColors(loreLine));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNavigationItem(String configPath) {
        Material material = Material.getMaterial(plugin.getConfig().getString(configPath + ".material", "PAPER"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.parseColors(plugin.getConfig().getString(configPath + ".displayname", "Next Page")));
            List<String> lore = new ArrayList<>();
            for (String loreLine : plugin.getConfig().getStringList(configPath + ".lore")) {
                lore.add(ColorUtil.parseColors(loreLine));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createCurrentTagItem(Tag tag) {
        Material material = Material.getMaterial(plugin.getConfig().getString("gui.has_tag_item.material", "PLAYER_HEAD"));
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.parseColors(plugin.getConfig().getString("gui.has_tag_item.displayname", "Current tag: <white>%tag_displayname%").replace("%tag_displayname%", tag.getDisplayName())));
            List<String> lore = new ArrayList<>();
            for (String loreLine : plugin.getConfig().getStringList("gui.has_tag_item.lore")) {
                lore.add(ColorUtil.parseColors(loreLine));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void setFillerGlass() {
        ItemStack fillerItem = makeItem(Material.valueOf(plugin.getConfig().getString("gui.filler_item.material", "BLACK_STAINED_GLASS_PANE")), plugin.getConfig().getString("gui.filler_item.displayname", " "));

        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, fillerItem);
            if (inventory.getItem(45 + i) == null) inventory.setItem(45 + i, fillerItem);
        }
        for (int i = 9; i < 45; i += 9) {
            if (inventory.getItem(i) == null) inventory.setItem(i, fillerItem);
            if (inventory.getItem(i + 8) == null) inventory.setItem(i + 8, fillerItem);
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = playerMenuUtility.getOwner();
        int slot = e.getRawSlot();

        // Handle pagination
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

                    if (tag != null) {
                        if (player.hasPermission("sakuratags.admin") || player.isOp() || player.hasPermission(tag.getPermission())) {
                            plugin.getTagStorage().setPlayerTag(player.getUniqueId(), tag);
                            player.sendMessage(ColorUtil.parseColors("&aTag set to: " + tag.getDisplayName()));
                            setMenuItems();
                            player.openInventory(inventory);
                        } else {
                            player.sendMessage(ColorUtil.parseColors("&cYou do not have permission to use this tag."));
                        }
                    } else {
                        player.sendMessage(ColorUtil.parseColors("&cCannot find the specified tag."));
                    }
                }
            }
        }
    }

}


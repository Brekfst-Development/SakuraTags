package com.brekfst.sakuratags.menus;

import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    public PlayerMenuUtility playerMenuUtility;
    protected Inventory inventory;
    protected String menuName;
    protected int slots;

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
    }

    public void open() {
        menuName = getMenuName();
        slots = getSlots();

        // Ensure parseColors returns a String with legacy color codes (e.g., §l for bold)
        String title = ColorUtil.parseColors(menuName);

        inventory = Bukkit.createInventory(this, slots, title);

        setMenuItems();

        playerMenuUtility.getOwner().openInventory(inventory);
    }

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void setMenuItems();

    public abstract String getMenuName();

    public abstract int getSlots();

    // Method to create an ItemStack with a display name and lore
    protected ItemStack makeItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorFormatter.parse(displayName));
            if (lore.length > 0) {
                List<String> formattedLore = Arrays.stream(lore)
                        .map(ColorFormatter::parse)
                        .toList();
                meta.setLore(formattedLore);
            }
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}

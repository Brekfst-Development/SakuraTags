package com.brekfst.sakuratags;

import com.brekfst.sakuratags.utils.ColorUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.brekfst.sakuratags.data.Tag;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SakuraTagsExpansion extends PlaceholderExpansion {

    private final SakuraTags plugin;

    public SakuraTagsExpansion(SakuraTags plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sakuratags";
    }

    @Override
    public @NotNull String getAuthor() {
        return "brekfst";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Persist through reloads
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // Get the player's current tag
        Tag playerTag = plugin.getTagStorage().getPlayerTag(player.getUniqueId());

        // Check which placeholder was requested and return the appropriate value
        switch (identifier) {
            case "name":
                // Returns the tag name or "No Tag" if none is assigned
                return playerTag != null ? ColorUtil.parseColors(playerTag.getName()) : "No Tag";
            case "description":
                // Returns the tag description or "No Description" if none is assigned
                return playerTag != null ? ColorUtil.parseColors(playerTag.getDescription()) : "No Description";
            case "permission":
                // Returns the tag permission node or "No Permission" if none is assigned
                return playerTag != null ? playerTag.getPermission() : "No Permission";
            case "tag":
                // Convert color codes before returning the display name
                return playerTag != null ? ColorUtil.parseColors(playerTag.getDisplayName()) : "";
            default:
                return null; // Unknown placeholder identifier
        }
    }
}
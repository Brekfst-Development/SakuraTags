package com.brekfst.sakuratags.data;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.files.TagsConfig;
import com.brekfst.sakuratags.utils.SessionData;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagStorage {
    private final SakuraTags plugin;
    private final Map<String, Tag> tags = new HashMap<>();

    public TagStorage(SakuraTags plugin) {
        this.plugin = plugin;
    }

    public void loadTags() {
        FileConfiguration tagsConfig = TagsConfig.get();
        tags.clear();

        if (tagsConfig.getConfigurationSection("tags") == null) {
            plugin.getLogger().warning("The 'tags' section is missing in tags.yml. Please ensure it exists.");
            return;
        }

        for (String key : tagsConfig.getConfigurationSection("tags").getKeys(false)) {
            String path = "tags." + key;
            Tag tag = new Tag(
                    key,
                    tagsConfig.getString(path + ".name"),
                    tagsConfig.getString(path + ".displayname"),
                    tagsConfig.getString(path + ".description"),
                    tagsConfig.getString(path + ".permission")
            );
            tags.put(key, tag);
        }
    }

    public Tag getTag(String id) {
        return tags.get(id);
    }

    public Tag getPlayerTag(UUID uuid) {
        String tagId = plugin.getDataManager().getPlayerDataConfig().getString("players." + uuid + ".current_tag");
        return tagId != null ? getTag(tagId) : null; // Uses getTag to retrieve Tag object by its ID
    }

    public void setPlayerTag(UUID playerUUID, Tag tag) {
        FileConfiguration playerDataConfig = plugin.getPlayerDataConfig();
        playerDataConfig.set("players." + playerUUID + ".current_tag", tag.getId());
        plugin.savePlayerDataConfig(); // Ensure the changes are saved to playerdata.yml
    }

    public void addTag(Tag tag) {
        tags.put(tag.getId(), tag); // Add tag to in-memory map
        saveTagsToConfig(); // Persist in tags.yml
    }

    public void updateTag(String id, SessionData sessionData) {
        Tag tag = tags.get(id);
        if (tag != null) {
            if (sessionData.getName() != null) tag.setName(sessionData.getName());
            if (sessionData.getDisplayName() != null) tag.setDisplayName(sessionData.getDisplayName());
            if (sessionData.getDescription() != null) tag.setDescription(sessionData.getDescription());
            if (sessionData.getPermission() != null) tag.setPermission(sessionData.getPermission());
            saveTagsToConfig();
        }
    }

    private void saveTagsToConfig() {
        FileConfiguration tagsConfig = TagsConfig.get();

        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            String path = "tags." + entry.getKey();
            Tag tag = entry.getValue();
            tagsConfig.set(path + ".name", tag.getName());
            tagsConfig.set(path + ".displayname", tag.getDisplayName());
            tagsConfig.set(path + ".description", tag.getDescription());
            tagsConfig.set(path + ".permission", tag.getPermission());
        }

        TagsConfig.save(); // Save changes to tags.yml
    }

    public Tag getTagByDisplayName(String displayName) {
        return tags.values().stream()
                .filter(tag -> tag.getDisplayName().equalsIgnoreCase(displayName))
                .findFirst()
                .orElse(null);
    }

    public Map<String, Tag> getTags() {
        return tags;
    }
}


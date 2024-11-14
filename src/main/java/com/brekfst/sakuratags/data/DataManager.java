package com.brekfst.sakuratags.data;

import com.brekfst.sakuratags.SakuraTags;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class DataManager {

    private final SakuraTags plugin;
    private File tagsFile;
    private FileConfiguration tagsConfig;
    private File playerDataFile;
    private FileConfiguration playerDataConfig;
    private TagStorage tagStorage;

    public DataManager(SakuraTags plugin) {
        this.plugin = plugin;
        loadTagsConfig();
        loadPlayerDataConfig();
        tagStorage = new TagStorage(plugin);
    }

    // Load tags.yml configuration
    private void loadTagsConfig() {
        tagsFile = new File(plugin.getDataFolder(), "tags.yml");
        if (!tagsFile.exists()) {
            try {
                tagsFile.getParentFile().mkdirs();
                tagsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create tags.yml file!");
            }
        }
        tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);
    }
    public void reloadTagsConfig() {
        tagsConfig = YamlConfiguration.loadConfiguration(tagsFile);
    }


    public void saveTagsConfig() {
        if (tagsConfig != null && tagsFile != null) {
            try {
                tagsConfig.save(tagsFile); // Saves `tags.yml`
            } catch (IOException e) {
                getLogger().severe("Could not save tags.yml!");
                e.printStackTrace();
            }
        }
    }

    // Getter for tags.yml configuration
    public FileConfiguration getTagsConfig() {
        if (tagsConfig == null) {
            loadTagsConfig();
        }
        return tagsConfig;
    }

    public void loadPlayerDataConfig() {
        playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.getParentFile().mkdirs();
                playerDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml file!");
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public FileConfiguration getPlayerDataConfig() {
        if (playerDataConfig == null) {
            loadPlayerDataConfig();
        }
        return playerDataConfig;
    }

    public void savePlayerDataConfig() {
        try {
            if (playerDataConfig != null && playerDataFile != null) {
                playerDataConfig.save(playerDataFile);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
        }
    }

    // Gets the active tag of a player by UUID
    public Tag getPlayerTag(UUID uuid) {
        String tagId = playerDataConfig.getString("players." + uuid + ".current_tag");
        return tagId != null ? plugin.getTagStorage().getTag(tagId) : null;
    }

    public TagStorage getTagStorage() {
        return tagStorage;
    }

    // Sets the active tag of a player by UUID
    public void setPlayerTag(UUID uuid, Tag tag) {
        playerDataConfig.set("players." + uuid + ".current_tag", tag.getId());
        savePlayerDataConfig();
    }
}

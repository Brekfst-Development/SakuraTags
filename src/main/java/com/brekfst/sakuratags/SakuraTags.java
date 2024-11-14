package com.brekfst.sakuratags;

import com.brekfst.sakuratags.commands.TagAdminCommand;
import com.brekfst.sakuratags.commands.TagsCommand;
import com.brekfst.sakuratags.commands.TagsReloadCommand;
import com.brekfst.sakuratags.data.DataManager;
import com.brekfst.sakuratags.data.TagStorage;
import com.brekfst.sakuratags.files.TagsConfig;
import com.brekfst.sakuratags.listeners.ChatListener;
import com.brekfst.sakuratags.listeners.MenuListener;
import com.brekfst.sakuratags.menus.PlayerMenuUtility;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SakuraTags extends JavaPlugin {

    private DataManager dataManager;
    private SessionManager sessionManager;
    private ColorFormatter colorFormatter;
    private TagStorage tagStorage;
    private File playerDataFile;
    private FileConfiguration playerDataConfig;

    private final Map<UUID, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Set up config files first
        saveDefaultConfig();
        TagsConfig.setup(this); // Initialize tags.yml configuration
        TagsConfig.get().options().copyDefaults(true);
        TagsConfig.save();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getLogger().info("PlaceholderAPI found! Registering expansion...");
            new SakuraTagsExpansion(this).register();
        }


        // Initialize DataManager and TagStorage, which might use configurations
        dataManager = new DataManager(this);
        tagStorage = new TagStorage(this);

        // Now load the configs to ensure dataManager is initialized
        loadConfigs();

        // Initialize other components
        sessionManager = new SessionManager(this);
        colorFormatter = new ColorFormatter();

        // Load tags from TagsConfig
        tagStorage.loadTags();

        // Register commands and event listeners
        getCommand("tagsadmin").setExecutor(new TagAdminCommand(this));
        getCommand("tags").setExecutor(new TagsCommand(this));
        getCommand("tagsreload").setExecutor(new TagsReloadCommand(this));
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
    }

    @Override
    public void onDisable() {
        TagsConfig.save();
    }

    public void loadPlayerDataConfig() {
        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.getParentFile().mkdirs();
                playerDataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create playerdata.yml file!");
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
        if (playerDataConfig != null && playerDataFile != null) {
            try {
                playerDataConfig.save(playerDataFile);
            } catch (IOException e) {
                getLogger().severe("Could not save playerdata.yml!");
                e.printStackTrace();
            }
        }
    }

    public void reloadConfigs() {
        reloadConfig();
        TagsConfig.reload();

        if (playerDataConfig != null) {
            playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        }

        tagStorage.loadTags();

        getLogger().info("All configurations have been reloaded successfully.");
    }

    public void loadConfigs() {
        saveDefaultConfig();
        dataManager.reloadTagsConfig();
    }


    public TagStorage getTagStorage() {
        return tagStorage;
    }
    // Getter for DataManager
    public DataManager getDataManager() {
        return dataManager;
    }


    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player player) {
        return playerMenuUtilityMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerMenuUtility(player));
    }
}

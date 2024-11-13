package com.brekfst.sakuratags;

import com.brekfst.sakuratags.commands.TagAdminCommand;
import com.brekfst.sakuratags.commands.TagsCommand;
import com.brekfst.sakuratags.data.DataManager;
import com.brekfst.sakuratags.data.TagStorage;
import com.brekfst.sakuratags.files.TagsConfig;
import com.brekfst.sakuratags.listeners.ChatListener;
import com.brekfst.sakuratags.listeners.MenuListener;
import com.brekfst.sakuratags.menus.PlayerMenuUtility;
import com.brekfst.sakuratags.utils.ColorFormatter;
import com.brekfst.sakuratags.utils.SessionManager;
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

        saveDefaultConfig();
        loadPlayerDataConfig();

        sessionManager = new SessionManager();
        colorFormatter = new ColorFormatter();
        TagsConfig.setup(this); // Initialize tags.yml configuration
        TagsConfig.get().options().copyDefaults(true);
        TagsConfig.save();

        tagStorage = new TagStorage(this);
        dataManager = new DataManager(this);

        tagStorage.loadTags();  // Load tags from TagsConfig

        getCommand("tagsadmin").setExecutor(new TagAdminCommand(this));
        getCommand("tags").setExecutor(new TagsCommand(this));
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
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

    public ColorFormatter getColorFormatter() {
        return colorFormatter;
    }

    public PlayerMenuUtility getPlayerMenuUtility(Player player) {
        return playerMenuUtilityMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerMenuUtility(player));
    }
}

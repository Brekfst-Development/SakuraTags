package com.brekfst.sakuratags.files;

import com.brekfst.sakuratags.SakuraTags;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TagsConfig {

    private static File file;
    private static FileConfiguration tagsConfig;

    public static void setup(SakuraTags plugin) {
        file = new File(plugin.getDataFolder(), "tags.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create tags.yml file!");
                e.printStackTrace();
            }
        }
        tagsConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return tagsConfig;
    }

    public static void save() {
        try {
            if (tagsConfig != null && file != null) {
                tagsConfig.save(file);
            }
        } catch (IOException e) {
            System.out.println("Couldn't save tags.yml!");
            e.printStackTrace();
        }
    }

    public static void reload() {
        tagsConfig = YamlConfiguration.loadConfiguration(file);
    }
}

package com.brekfst.sakuratags.commands;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.utils.ColorFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagsReloadCommand implements CommandExecutor {

    private final SakuraTags plugin;

    public TagsReloadCommand(SakuraTags plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("sakuratags.admin")) {
            sender.sendMessage(ColorFormatter.prefix("&cYou do not have permission to execute this command."));
            return true;
        }

        plugin.reloadConfigs();
        sender.sendMessage(ColorFormatter.prefix("&aAll SakuraTags configurations have been reloaded successfully!"));
        return true;
    }
}
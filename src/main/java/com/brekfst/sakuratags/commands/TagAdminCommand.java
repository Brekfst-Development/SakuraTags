package com.brekfst.sakuratags.commands;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.menus.MainAdminMenu;
import com.brekfst.sakuratags.menus.PlayerMenuUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagAdminCommand implements CommandExecutor {

    private final SakuraTags plugin;

    public TagAdminCommand(SakuraTags plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("sakura.admin")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        new MainAdminMenu(new PlayerMenuUtility(player), plugin).open();
        return true;
    }
}

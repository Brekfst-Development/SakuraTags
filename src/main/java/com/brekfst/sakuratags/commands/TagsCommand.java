package com.brekfst.sakuratags.commands;

import com.brekfst.sakuratags.SakuraTags;
import com.brekfst.sakuratags.menus.PlayerMenuUtility;
import com.brekfst.sakuratags.menus.TagsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagsCommand implements CommandExecutor {

    private final SakuraTags plugin;

    public TagsCommand(SakuraTags plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        PlayerMenuUtility playerMenuUtility = plugin.getPlayerMenuUtility(player);

        TagsMenu menu = new TagsMenu(playerMenuUtility, plugin);
        menu.open();
        return true;
    }
}
